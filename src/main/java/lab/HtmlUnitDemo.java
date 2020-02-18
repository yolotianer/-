package lab;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.util.List;

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


        //保存列表页html文档，放到文件里
        page.save(new File("唐诗三百首\\列表页.html"));


        //html的body
        HtmlElement body=page.getBody();

        //筛选包含我们所需内容的标签，保存标签的内容
        List<HtmlElement> elements = body.getElementsByAttribute("div",
                "class",
                "typecont");

        HtmlElement divElement = elements.get(0);
        List<HtmlElement> aElement = divElement.getElementsByAttribute("a",
                "target",
                "_blank");

        //一个详情页的URL
       String str=aElement.get(0).getAttribute("href");
    }
}
