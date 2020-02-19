# 《爬虫-唐诗》项目开发
## 一、分析阶段
#### 一. 模块 抓取模块
1. 抓取详情页（HTML格式）
2. 从HTML详情页中提取
    - 详情页信息
    - 共抓多少首诗
3. 分别抓取详情页（HTML格式）
4. 从详情页中提取诗词信息
    - 标题
    - 朝代
    - 作者
    - 正文
5. 计算sha256(标题+正文)
　　　求了一个哈希值，保证数据不重复
6. 调用第三方分词库，堆内容进行分词
7. 把数据保存到数据库的表中

#### 二、数据抓取阶段需要的第三方发依赖（maven仓库可以了解更多）
1. 数据库操作：mysql-connector-java:5.1.47

```
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
```

2. HTML页面请求+解析：htmlunit


```
        <dependency>
            <groupId>net.sourceforge.htmlunit</groupId>
            <artifactId>htmlunit</artifactId>
            <version>2.36.0</version>
        </dependency>

```

3. 分词： ansj_seg

```
        <dependency>
            <groupId>org.ansj</groupId>
            <artifactId>ansj_seg</artifactId>
            <version>5.1.6</version>
        </dependency>
```

#### 三、项目编写
1. 预研阶段
    1. 技术选型：网页抓取第三方库很多，为什么用htmlunit
    2. 用法调研：可以通过实现简单的demo，来熟悉库的使用
    3. 实验评估：
        - 效率是否满足需求
        - 内存是否满足需求
        - 使用过程是否遇到复杂问题或者BUG，以及如何解决
    
- Demo1: HTML相关操作（获取列表页html文件，筛选相关信息，得到详情页path）
```
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
        
   
        //获取html的body
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
```
- Demo2：分词

```
        String sentence="中华人民中华人民共和国成立了！中国人民从此站起来了。";
        /*
        AnsjSeg提供了四种分词调用的方式：
            ①基本分词(BaseAnalysis)
            ②精准分词(ToAnalysis)
            ③NLP分词(NlpAnalysis)
            ④面向索引分词(IndexAnalysis)
          NLP分词方式可是未登录词，但速度较慢：
          NlpAnalysis.parse(sentence)--分词
          public List<Term> getTerms() {return this.terms;}--类型转换

         */

        List<Term> termList= NlpAnalysis.parse(sentence).getTerms();
        for (Term term : termList) {
            System.out.println(term.getNatureStr()+":"+term.getRealName());
        }//term.getNatureStr()：拿到词性     term.getRealName()：拿到词
```
- Demo3:计算SHA

```
        MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
        String s="你好世界";
        byte[] bytes = s.getBytes("UTF-8");
        messageDigest.update(bytes);
        byte[] result = messageDigest.digest();

```

- Demo4:保存数据库

```
public class 数据库存储Demo {
    public static void main(String[] args) throws SQLException {
       MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
       dataSource.setServerName("127.0.0.1");
       dataSource.setPort(3305);
       dataSource.setUser("root");
       dataSource.setPassword("password");
       dataSource.setDatabaseName("tangshi");
       dataSource.setUseSSL(false);
       dataSource.setCharacterEncoding("UTF-8");

        String 朝代 = "唐代";
        String 作者 = "白居易";
        String 标题 = "问刘十九";
        String 正文 = "绿蚁新醅酒，红泥小火炉。晚来天欲雪，能饮一杯无？";

        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO t_tangshi " +
                    "(sha256, dynasty, title, author, " +
                    "content, word) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "sha256");
                statement.setString(2, 朝代);
                statement.setString(3, 标题);
                statement.setString(4, 作者);
                statement.setString(5, 正文);
                statement.setString(6, "");

                statement.executeUpdate();
            }
        }
    }
}

```
2. 整体流程

```
    请求列表页->解析列表页
    for(...){
        请求详情页->解析详情页
        分词->计算sha->存入数据库
    }
```

