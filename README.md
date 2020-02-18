# 《爬虫-唐诗》项目开发
## 　　一、分析阶段
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

#### 　　三、项目编写
1. 预研阶段
    1. 技术选型：网页抓取第三方库很多，为什么用htmlunit
    2. 用法调研：可以通过实现简单的demo，熟悉库的使用
    3. 实验评估：
        - 效率是否满足需求
        - 内存是否满足需求
        - 使用过程是否遇到复杂问题或者BUG，以及如何解决