package lab;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;

/**
 * @author yolo
 * @date 2020/2/18-16:03
 */
public class HtmlUnitDemo {
    public static void main(String[] args) throws IOException {
        //无界面的浏览器（HTTP客户端）
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        //关闭浏览器css执行引擎
        webClient.getOptions().setCssEnabled(false);
        //关闭浏览器js浏览器引擎
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = webClient.getPage("https://so.gushiwen.org/gushi/tangshi.aspx");
        //打印出列表页对象
        System.out.println(page);
        //把详情页的信息存储到文件中
        page.save(new File("唐诗三百首\\列表页.html"));
    }
}
