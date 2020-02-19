package version04多线程;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author yolo
 * @date 2020/2/18-19:47
 */
public class TextDemo {
    public static void main(String[] args) {

        //创建客户端
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        //获取列表页
        String baseURL="https://so.gushiwen.org";
        String pathURL="/gushi/tangshi.aspx";
        {
            String url=baseURL+pathURL;

            //HtmlPage page = webClient.getPage(url);
        }

        //分析列表页

        //获取详情页
        //分析详情页
        //计算sha256
        //分词
        //存入数据库
    }
}
