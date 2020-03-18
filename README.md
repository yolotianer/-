<<<<<<< HEAD
# 《爬虫-唐诗》项目开发
## 　　一、分析阶段
#### 一. 模块 抓取模块
1. 抓取列表页（HTML格式）
2. 从列表页中提取
    - 详情页信息（pathURL）
    - 保存详情页pathURL
3. 分别抓取详情页（HTML格式）
4. 从详情页中提取诗词信息
    - 标题
    - 朝代
    - 作者
    - 正文
5. 计算sha256(标题+正文)
　　　求了一个哈希值，保证数据不重复
6. 调用第三方分词库，对内容进行分词
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

### 　　三、相关知识
#### 1.抓取页面信息部分
###### WebClient:浏览器客户端对象
```
新建一个模拟谷歌浏览器的浏览器客户端对象
WebClient webClient=new WebClient(BrowserVersion.CHROME);
webClient.getOptions().setCssEnabled(false);//不启用css
webClient.getOptions().setJavaScriptEnabled(false);//不启用js
```

###### HtmlPage:网页对象

```
HtmlPage page=webClient.getPage(URL)
```

###### HtmlElement：元素对象

```
public HtmlElement getBody()
public final <E extends HtmlElement> List<E> getElementsByAttribute(
          final String elementName,
          final String attributeName,
          final String attributeValue)：通过属性名称 查找当前html下的所有元素
public DomNodeList<HtmlElement> getElementsByTagName(final String tagName)：查找当前元素或者当前元素之下的相同标签的元素.
public String getAttribute(final String attributeName):获取当前属性对应的值
```
###### Xpath

```
public <T> List<T> getByXPath(final String xpathExpr)
 * Xpath:级联选择 
     * ① //：从匹配选择的当前节点选择文档中的节点，而不考虑它们的位置
     * ② div：匹配<div>标签
     * ③ [@class='cont']：属性名为class的值为cont
     * ④ h1：匹配<h1>标签
     * ⑤ /text():文本
     
List<HtmlElement> spanList=page.getByXPath("//div[@class='cont']/h1/text()");
```
#### 2.数据处理
###### MessageDigest：为程序提高消息摘要算法放入功能
- 目的：保证存入数据库的数据不重复，即避免重复插入
- SHA-1
- SHA-256

```
public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException
    返回实现指定摘要算法的MessageDigest对象。 
    MessageDigest messageDigest=MessageDigest.getInstance("SHA-256")；
    
public void update(byte input)使用指定的字节更新摘要。  input - 用于更新摘要的字节
    messageDigest.update(s.getBytes("UTF-8"));  s:字符串

public byte[] digest()通过执行最后的操作（如填充）来完成哈希计算。 此通话完成后，摘要将重置。 
    byte[] result = messageDigest.digest();

```
###### NlpAnalysis：nlp分词

```
最完整的分词，可以识别出未登陆的词
    List<Term> parse = NlpAnalysis.parse
```
其它分词学习[：](https://note.youdao.com/)http://nlpchina.github.io/ansj_seg/

#### 3.数据库存储
###### 创建连接池

```
 MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();//创建了一个数据库连接池
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3305);
        dataSource.setUser("root");
        dataSource.setPassword("******");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(false);
        dataSource.setCharacterEncoding("UTF-8");
```
#### 4.多线程
###### 线程池

```
创建线程池
ExecutorService pool = Executors.newFixedThreadPool(30);
启动线程任务
pool.execute(new Job(baseUrl,url,dataSource,countDownLatch));
关闭线程池
pool.shutdown();
```
###### 线程池情况下如让线程停止：countDownLatch/原子类计数
**在这选择countDownLatch**
 CountDownLatch
 - 用给定的计数初始化。 await方法阻塞，直到由于countDown()方法的调用而导致当前计数达到零，之后所有等待线程被释放，并且任何后续的await 调用立即返回。
 
```
CountDownLatch countDownLatch = new CountDownLatch(pathList.size());
countDownLatch.await();//等待所有的线程全部结束
pool.shutdown();//关闭线程池
```

=======
# Tangshi
>>>>>>> b8f997aa576378f1f16e73da3edd8d87fb7fb680
