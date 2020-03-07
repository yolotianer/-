package version01;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

/**
 * @author yolo
 * @date 2020/1/13-16:12
 */
public class 详情页Demo {
    public static void main(String[] args) {
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        String url="https://so.gushiwen.org/shiwenv_45c396367f59.aspx";
        try {
            HtmlPage page=webClient.getPage(url);
            HtmlElement body=page.getBody();
            /*
             //通过属性名，查取HTML下的所有元素
            List<HtmlElement>elements=body.getElementsByAttribute(
                    "div",
                    "class",
                    "contson"
            );
            //打印
            for (HtmlElement element : elements) {
                System.out.println(element);
            }
            System.out.println(elements.get(0).getTextContent().trim());
             */
            {
                String xpath="//div[@class='cont']/h1/text()";
                //    public <T> List<T> getByXPath(final String xpathExpr):返回的是一个链表，而我们只需要链表的第一个元素
                Object o=body.getByXPath(xpath).get(0);
                DomText domText=(DomText) o;
                System.out.println(domText.asText());
            }
            {
                String xpath = "//div[@class='cont']/p[@class='source']/a[1]/text()";
                Object o = body.getByXPath(xpath).get(0);
                DomText domText = (DomText)o;
                System.out.println(domText.asText());
            }
            {
                String xpath = "//div[@class='cont']/p[@class='source']/a[2]/text()";
                Object o = body.getByXPath(xpath).get(0);DomText domText = (DomText)o;
                System.out.println(domText.asText());
            }
            {
                String xpath = "//div[@class='cont']/div[@class='contson']";
                Object o = body.getByXPath(xpath).get(0);
                HtmlElement element = (HtmlElement)o;
                System.out.println(element.getTextContent().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
