import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

/**
 * @author yolo
 * @date 2020/1/9-20:17
 */
public class 列表页Demo {
    public static void main(String[] args){
        //1.获取指定URL页面

        //构造一个WebClient，模拟Chrome浏览器客户端
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        //是否启用js,css
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        //URL统一资源定位符
        String url="https://so.gushiwen.org/gushi/tangshi.aspx";
        //加载指定url对应的页面
        try {
            HtmlPage page=webClient.getPage(url);
            //HtmlElement表示 HTML 文档中任何可能的元素类型, 如BODY TABLE、和FORM。
            HtmlElement body=page.getBody();
            /*
             System.out.println(body);
             HtmlBody[<body onclick="closeshowBos()">]
             */
            //通过属性名，查去HTML下的所有元素
            List<HtmlElement> elements=body.getElementsByAttribute(
                    "div",
                    "class",
                    "typecont"
            );
            int count=0;
            for (HtmlElement element : elements) {
                //查找当前元素或者当前元素之下的相同标签的元素.
                List<HtmlElement>aElements=element.getElementsByTagName("a");
                for (HtmlElement a : aElements) {//获取了每首诗的详情页url
                    System.out.println(a.getAttribute("href"));
                    count++;
                }
                System.out.println(count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }
        /*
        System.out.println(page);
         // HtmlPage(https://so.gushiwen.org/gushi/tangshi.aspx)@1280603381
         */
    }
}
