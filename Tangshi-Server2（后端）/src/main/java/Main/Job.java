package Main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dao.DBUtils;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 分析详情页
 * @author yolo
 * @date 2020/3/5-17:24
 */
public class Job implements Runnable{
    private String baseURL;
    private String pathURL;
    private DataSource dataSource;
    private CountDownLatch countDownLatch;


    public Job(String baseURL,String pathURL, DataSource dataSource, CountDownLatch countDownLatch) {
        this.baseURL=baseURL;
        this.pathURL = pathURL;
        this.dataSource = dataSource;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        Connection connection=null;
        PreparedStatement statement=null;
        try {

            HtmlPage page=webClient.getPage(baseURL+ pathURL);
            HtmlElement body = page.getBody();
            String xpath;
            DomText domText;

            //①获取标题title
            xpath="//div[@class='cont']/h1/text()";
            //public <T> List<T> getByXPath(final String xpathExpr):返回的是一个链表，而我们只需要链表的第一个元素
            domText=(DomText)body.getByXPath(xpath).get(0);;
            String title=domText.asText();

            //②获取朝代dynasty
            xpath="//div[@class='cont']/p[@class='source']/a[1]/text()";
            domText=(DomText)body.getByXPath(xpath).get(0);
            String dynasty=domText.asText();

            //③获取作者
            xpath="//div[@class='cont']/p[@class='source']/a[2]/text()";
            domText=(DomText)body.getByXPath(xpath).get(0);
            String author=domText.asText();

            //⑤获取正文
            xpath = "//div[@class='cont']/div[@class='contson']";
            HtmlElement htmlElement=(HtmlElement) body.getByXPath(xpath).get(0);
            String content=htmlElement.getTextContent();

            //计算sha-256--保证数插入数据的唯一性
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            String s = title+content;
            messageDigest.update(s.getBytes("UTF-8"));
            byte[] result = messageDigest.digest();
            StringBuilder sha256 = new StringBuilder();
            for (byte b : result) {
                sha256.append(String.format("%02x",b));
            }

            //计算分词
            List<Term> termList = new ArrayList<>();
            termList.addAll(NlpAnalysis.parse(title).getTerms());
            termList.addAll(NlpAnalysis.parse(content).getTerms());
            List<String> words = new ArrayList<>();
            for (Term term : termList) {
                if (term.getNatureStr().equals("w")){
                    continue;
                }
                if (term.getNatureStr().equals(null)){
                    continue;
                }
                if (term.getRealName().length()<2){
                    continue;
                }
                words.add(term.getRealName());
            }

            String insertWords=String.join(",",words);

            connection = dataSource.getConnection();
            String sql = "INSERT INTO t_tangshi(sha256,dynasty,title,author,content,word)VALUES(?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1,sha256.toString());
            statement.setString(2,dynasty);
            statement.setString(3,title);
            statement.setString(4,author);
            statement.setString(5,content);
            statement.setString(6,insertWords);

            statement.executeUpdate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            if(!e.getMessage().contains("Duplicate entry")){//为了处理重复插入
                e.printStackTrace();
            }
        }finally {
            DBUtils.close(connection,statement);
            countDownLatch.countDown();//表示现一个线程的任务结束了
        }
    }
}
