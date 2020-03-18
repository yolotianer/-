package lib;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * @author yolo
 * @date 2020/1/18-16:43
 * 核心技术：练习相关使用
 * 1、HtmlUnit 基本使用架构
 * 2、HtmlUnit 模拟浏览器
 * 3、使用代理 IP
 * 4、静态网页爬取，取消 CSS、JS 支持，提高速度
 *
 */
public class Demo {
    public static void main(String[] args) {

        // 实例化Web客户端、①模拟 Chrome 浏览器  、②使用代理IP
        WebClient webClient = new WebClient(BrowserVersion.CHROME, "118.114.77.47", 8080);
        webClient.getOptions().setCssEnabled(false); // 取消 CSS 支持
        webClient.getOptions().setJavaScriptEnabled(false); // 取消 JavaScript支持
        try {
            HtmlPage page = webClient.getPage("https://www.csdn.net/"); // 解析获取页面

            /**
             * Xpath:级联选择
             * ① //：从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置
             * ② h3：匹配<h3>标签
             * ③ [@class='company_name']：属性名为class的值为company_name
             * ④ a：匹配<a>标签
             */
            List<HtmlElement> spanList=page.getByXPath("//h3[@class='company_name']/a");

            for(int i=0;i<spanList.size();i++) {
                //asText ==> innerHTML
                System.out.println(i+1+"、"+spanList.get(i).asText());
            }

        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webClient.close(); // 关闭客户端，释放内存
        }
    }

}
