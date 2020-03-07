package version05;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yolo
 * @date 2020/2/20-18:56
 */
public class MultiThread {
    private static class Job implements Runnable{
        private String URL;
        private MessageDigest messageDigest;
        private DataSource dataSource;

        public Job( String URL, MessageDigest messageDigest, DataSource dataSource) {
            this.URL = URL;
            this.messageDigest = messageDigest;
            this.dataSource = dataSource;
        }


        @Override
        public void run() {
            //创建客户端
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            Connection connection=null;
            PreparedStatement statement=null;

            //获取详情页
            try {
                HtmlPage page = webClient.getPage(URL);
                HtmlElement body = page.getBody();
                //分析详情页
                String xpath;
                DomText domText;

                //①获取标题title
                xpath="//div[@class='cont']/h1/text()";
                //public <T> List<T> getByXPath(final String xpathExpr):返回的是一个链表，而我们只需要链表的第一个元素
                domText=(DomText)body.getByXPath(xpath).get(0);;
                String title=domText.asText();
                System.out.println(title);

                //②获取朝代dynasty
                xpath="//div[@class='cont']/p[@class='source']/a[1]/text()";
                domText=(DomText)body.getByXPath(xpath).get(0);
                String dynasty=domText.asText();
                System.out.println(dynasty);
                //③获取作者
                xpath="//div[@class='cont']/p[@class='source']/a[2]/text()";
                domText=(DomText)body.getByXPath(xpath).get(0);
                String author=domText.asText();

                System.out.println(author);

                //⑤获取正文
                xpath = "//div[@class='cont']/div[@class='contson']";
                HtmlElement htmlElement=(HtmlElement) body.getByXPath(xpath).get(0);
                String content=htmlElement.getTextContent();
                System.out.println(content);

                //计算SHA
                String s=title+content;
                messageDigest.update(s.getBytes("UTF-8"));
                byte[] result = messageDigest.digest();
                StringBuilder sha256=new StringBuilder();
                for (byte b : result) {
                 sha256.append(b);
                }
                System.out.println(sha256.toString());
                //分词
                List<Term>termList=new ArrayList<>();
                termList.addAll(NlpAnalysis.parse(title).getTerms());
                termList.addAll(NlpAnalysis.parse(content).getTerms());

                List<String>words=new ArrayList<>();
                for (Term term : termList) {
                    if(term==null){
                        continue;
                    }
                    if(term.getRealName().length()<2){
                        continue;
                    }
                    if(term.getNatureStr().equals('w')){
                        continue;
                    }
                    words.add(term.getRealName());
                }

                String insertWords=String.join(",",words);

                String sql = "INSERT INTO t_tangshi " +
                        "(sha256, dynasty, title, author, " +
                        "content, word) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                 connection = dataSource.getConnection();
                 statement=connection.prepareStatement(sql);


                //存入数据库
                statement.setString(1, sha256.toString());
                statement.setString(2, dynasty);
                statement.setString(3, title);
                statement.setString(4, author);
                statement.setString(5, content);
                statement.setString(6, insertWords);
                System.out.println("插入成功");

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, SQLException {
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        String baseUrl="https://so.gushiwen.org";
        String pathUrl="/gushi/tangshi.aspx";
        List<String>pathList=new ArrayList<>();

        {
            String URL=baseUrl+pathUrl;
            HtmlPage page = webClient.getPage(URL);
            HtmlElement body = page.getBody();
            List<HtmlElement> elements = body.getElementsByAttribute("div",
                    "class",
                    "typecont");
            for (HtmlElement element : elements) {
                List<HtmlElement> aElements = element.getElementsByTagName("a");
                for (HtmlElement aElement : aElements) {
                    pathList.add(aElement.getAttribute("href"));
                }
                System.out.println(pathList);
            }

        }

        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");

        MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3305);
        dataSource.setUser("root");
        dataSource.setPassword("mmwan980815");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF8");

        ExecutorService pool = Executors.newFixedThreadPool(30);

        for (String path : pathList) {
            Job job=new Job(baseUrl+path,messageDigest,dataSource);
            pool.execute(job);
        }
        pool.shutdown();
    }
}
