package Main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import dao.DBUtils;

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
        WebClient webClient=new WebClient(BrowserVersion.CHROME);//新建一个模拟谷歌浏览器的浏览器客户端对象
        webClient.getOptions().setCssEnabled(false);//不启用css
        webClient.getOptions().setJavaScriptEnabled(false);//不启用js
        //列表页URL
        String baseUrl="https://so.gushiwen.org";
        String pathUrl="/gushi/tangshi.aspx";

        //解析列表页，获取详情页URL
        List<String> pathList=getPath(webClient, baseUrl, pathUrl);

        //连接池
        MysqlConnectionPoolDataSource dataSource = DBUtils.getDataSource();

        //线程池：320/30  大约需要十趟
        ExecutorService pool = Executors.newFixedThreadPool(30);

        CountDownLatch countDownLatch = new CountDownLatch(pathList.size());

        for (String url : pathList) {
            pool.execute(new Job(baseUrl,url,dataSource,countDownLatch));
        }
        countDownLatch.await();//表示所有的线程全部结束
        pool.shutdown();//关闭线程池

    }

    /**
     * 解析列表页，获取详情页path
     * @param webClient　浏览器客户端对象
     * @param baseUrl
     * @param pathUrl
     * @return
     */


    private static List<String> getPath(WebClient webClient, String baseUrl, String pathUrl) {
        List<String> pathList=new ArrayList<>();
        String URL=baseUrl+pathUrl;
        HtmlPage page = null;
        try {
            page = webClient.getPage(URL);//尝试加载网页，得到HTML文档
            HtmlElement body = page.getBody();
            List<HtmlElement> elements = body.getElementsByAttribute("div",
                    "class",
                    "typecont");
            for (HtmlElement element : elements) {
                List<HtmlElement> aElements = element.getElementsByTagName("a");//a:标签
                for (HtmlElement aElement : aElements) {
                    pathList.add(aElement.getAttribute("href"));//href：属性
                }
            }
            return pathList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
