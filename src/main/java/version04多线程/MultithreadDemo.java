package version04多线程;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yolo
 * @date 2020/2/18-19:47
 */
public class MultithreadDemo {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, SQLException {

        //创建客户端
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        //获取列表页
        String baseURL="https://so.gushiwen.org";
        String pathURL="/gushi/tangshi.aspx";
        List<String>pathList=new ArrayList<>();
        {
            String url=baseURL+pathURL;
            HtmlPage page = webClient.getPage(url);
            //分析列表页
            HtmlElement body = page.getBody();
            List<HtmlElement> elements = body.getElementsByAttribute("div",
                    "class",
                    "typecont");
            for (HtmlElement element : elements) {
                List<HtmlElement> aElements = element.getElementsByTagName("a");
                for (HtmlElement aElement : aElements) {
                    pathList.add(aElement.getAttribute("href"));
                }
            }
        }


        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");

        //创建数据库连接
        MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3305);
        dataSource.setUser("root");
        dataSource.setPassword("mmwan980815");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF-8");

        Connection connection = dataSource.getConnection();

        String sql = "INSERT INTO t_tangshi " +
                "(sha256, dynasty, title, author, " +
                "content, word) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement=connection.prepareStatement(sql);

        //获取详情页
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        for (String path : pathList) {
                            try {
                                HtmlPage page = webClient.getPage(baseURL+path);
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

                                //计算sha-256
                                String s=title+content;
                                messageDigest.update(s.getBytes("UTF-8"));
                                byte[] result = messageDigest.digest();
                                StringBuilder sha256=new StringBuilder();
                                for (byte b : result) {
                                    sha256.append(String.format("%02x",b));
                                }

                                //分词

                                List<Term> termList = new ArrayList<>();
                                termList.addAll(NlpAnalysis.parse(title).getTerms());
                                termList.addAll(NlpAnalysis.parse(content).getTerms());
                                //将分好的词存入List，对其进行筛选，去掉符号，null，长度小于2的
                                List<String>words=new ArrayList<>();
                                for (Term term : termList) {
                                    if(term.getNatureStr().equals("null")){
                                        continue;
                                    }
                                    if(term.getNatureStr().equals("w")){
                                        continue;
                                    }
                                    if(term.getRealName().length()<2){
                                        continue;
                                    }

                                    words.add(term.getRealName());
                                }
                                //将分词用逗号连接起来
                                String insertWords=String.join(",",words);

                                //存入数据库
                                statement.setString(1, sha256.toString());
                                statement.setString(2, dynasty);
                                statement.setString(3, title);
                                statement.setString(4, author);
                                statement.setString(5, content);
                                statement.setString(6, insertWords);

                                statement.executeUpdate();

                            } catch (IOException | SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        ).start();
    }
}
