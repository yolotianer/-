package version02单线程;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;


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
 * @date 2020/1/13-21:23
 */
public class TextDemo {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //1.
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        //2.
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        //3.
        String URL="https://so.gushiwen.org/gushi/tangshi.aspx";
        HtmlPage page=webClient.getPage(URL);
        //4.
        HtmlElement body=page.getBody();
        List<HtmlElement>elements=body.getElementsByAttribute(
                "div",
                "class",
                "typecont"
        );

        //5.
        List<String>url=new ArrayList<>();
        int count=0;
        for (HtmlElement element : elements) {
            List<HtmlElement>aElements=element.getElementsByTagName("a");
            for (HtmlElement a : aElements) {
                url.add(a.getAttribute("href"));
                count++;
            }
        }

        //6.
        MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3305);
        dataSource.setUser("root");
        dataSource.setPassword("mmwan980815");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF-8");

        //7.
        String tempUrl=null;
        for (String s : url) {
            tempUrl="https://so.gushiwen.org"+s;
            page=webClient.getPage(tempUrl);
            body=page.getBody();

            String title = null;
            String dynasty = null;
            String author = null;
            String content = null;
            //7.1标题
            {
                String xpath="//div[@class='cont']/h1/text()";
                Object o=body.getByXPath(xpath).get(0);
                DomText domText=(DomText)o;
                title=domText.asText();
            }
            //7.2朝代
            {
                String xpath = "//div[@class='cont']/p[@class='source']/a[1]/text()";
                Object o=body.getByXPath(xpath).get(0);
                DomText domText=(DomText)o;
                dynasty=domText.asText();
            }
            //7.3作者
            {
                String xpath = "//div[@class='cont']/p[@class='source']/a[2]/text()";
                Object o=body.getByXPath(xpath).get(0);
                DomText domText=(DomText)o;
                author=domText.asText();
            }
            //7.4正文
            {
                String xpath = "//div[@class='cont']/div[@class='contson']";
                Object o = body.getByXPath(xpath).get(0);
                HtmlElement element = (HtmlElement)o;
                content=element.getTextContent().trim();
            }

            //7.5　　SHA＝标题＋正文
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            String shaTemp=title+content;
            byte[] bytes = shaTemp.getBytes("UTF-8");
            messageDigest.update(bytes);
            byte[] result = messageDigest.digest();
            String SHA="";
            for (byte b : result) {
                SHA=SHA+b;
            }
            try (Connection connection = dataSource.getConnection()) {
                String sql = "INSERT INTO t_tangshi " +
                        "(sha256, dynasty, title, author, " +
                        "content, word) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, SHA);
                    statement.setString(2, dynasty);
                    statement.setString(3, title);
                    statement.setString(4, author);
                    statement.setString(5, content);
                    statement.setString(6, "");

                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
