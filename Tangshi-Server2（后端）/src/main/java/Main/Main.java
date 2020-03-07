package Main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yolo
 * @date 2020/3/5-17:22
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        String baseUrl="https://so.gushiwen.org";
        String pathUrl="/gushi/tangshi.aspx";

        List<String> pathList=getPath(webClient, baseUrl, pathUrl);
        System.out.println(pathList);

        //连接池
        MysqlConnectionPoolDataSource dataSource = getDataSource();

        //线程池
        ExecutorService pool = Executors.newFixedThreadPool(30);

        CountDownLatch countDownLatch = new CountDownLatch(pathList.size());

        for (String url : pathList) {
            pool.execute(new Job(baseUrl,url,dataSource,countDownLatch));
        }
        countDownLatch.await();
        pool.shutdown();

    }

    /**
     *
     * @return
     */
    private static MysqlConnectionPoolDataSource getDataSource() {
        MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3305);
        dataSource.setUser("root");
        dataSource.setPassword("mmwan980815");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF-8");
        return dataSource;
    }

    /**
     * 获取详情页path
     * @param webClient
     * @param baseUrl
     * @param pathUrl
     * @return
     */
    private static List<String> getPath(WebClient webClient, String baseUrl, String pathUrl) {
        List<String> pathList=new ArrayList<>();
        String URL=baseUrl+pathUrl;
        HtmlPage page = null;
        try {
            page = webClient.getPage(URL);
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
            return pathList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
