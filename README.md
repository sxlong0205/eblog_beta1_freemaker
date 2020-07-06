

# eblog_beta1

#### 介绍
该项目为一个博客项目，基于MarkerHub的[eblog](https://github.com/MarkerHub/eblog)项目实现，前端利用Freemarker渲染引擎搭建，后台使用SpringBoot搭建。

#### 软件架构
软件架构说明
- JDK 1.8
- IDEA 2020
- MySQL 8.0
- layui
- MybatisPlus


#### 安装教程

### Day1
- 使用了基于 layui 的 Fly 前端框架，链接：[Fly社区](https://fly.layui.com/)，[Layui模板官网](https://fly.layui.com/store/)
- 将页面头部、导航栏、左边、右边和底部模块进行抽离

![](https://gitee.com/xlshi/blog_img/raw/master/img/20200705223829.png)

- 使用 MyBatisPlus 自动生成插件生成了数据库对应的实体类、Mapper 接口和 Service 接口实现类

  ![](https://gitee.com/xlshi/blog_img/raw/master/img/20200705223832.png)

- 引入 MyBatisPlus 分页插件，Spring Boot 配置分页插件代码如下：

  ```java
  //Spring boot方式
  @EnableTransactionManagement
  @Configuration
  @MapperScan("com.baomidou.cloud.service.*.mapper*")
  public class MybatisPlusConfig {
  
      @Bean
      public PaginationInterceptor paginationInterceptor() {
          PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
          // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
          // paginationInterceptor.setOverflow(false);
          // 设置最大单页限制数量，默认 500 条，-1 不受限制
          // paginationInterceptor.setLimit(500);
          // 开启 count 的 join 优化,只针对部分 left join
          paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
          return paginationInterceptor;
      }
  }
  ```


### Day2

- MyBatisPlus 引入执行SQL分析打印插件(p6spy依赖)	

  1. 引入依赖

  ```xml
  <!--SQL分析器-->
  <dependency>
      <groupId>p6spy</groupId>
      <artifactId>p6spy</artifactId>
      <version>3.8.6</version>
  </dependency>
  ```

  2. application.yml 配置

  ```yml
  spring:
    datasource:
      driver-class-name: com.p6spy.engine.spy.P6SpyDriver
      url: jdbc:p6spy:mysql://localhost:3306/eblog
      ...
  ```

  3. spy.properties 配置（MyBatisPlus 官网最新配置文件可能存在`ClassNotFoundException: com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory`异常，建议使用如下配置代码）

  ```properties
  module.log=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory
  # 自定义日志打印
  logMessageFormat=com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger
  #日志输出到控制台
  appender=com.baomidou.mybatisplus.extension.p6spy.StdoutLogger
  # 使用日志系统记录 sql
  #appender=com.p6spy.engine.spy.appender.Slf4JLogger
  # 设置 p6spy driver 代理
  deregisterdrivers=true
  # 取消JDBC URL前缀
  useprefix=true
  # 配置记录 Log 例外,可去掉的结果集有error,info,batch,debug,statement,commit,rollback,result,resultset.
  excludecategories=info,debug,result,batch,resultset
  # 日期格式
  dateformat=yyyy-MM-dd HH:mm:ss
  # 实际驱动可多个
  #driverlist=org.h2.Driver
  # 是否开启慢SQL记录
  outagedetection=true
  # 慢SQL记录标准 2 秒
  outagedetectioninterval=2
  ```

- Layui引入分页

```html
<div id="laypage-main">

</div>
<script>
    layui.use('laypage', function () {
        var laypage = layui.laypage;

        //执行一个laypage实例
        laypage.render({
            elem: 'laypage-main' //注意，这里的 test1 是 ID，不用加 # 号
            , count: ${pageData.total} //数据总数，从服务端得到
            , curr: ${pageData.current} //当前页数
            , limit: ${pageData.size} //每页显示条数
            , jump: function (obj, first) {//回调函数
                //首次不执行
                if (!first) {
                    location.href = "?pn=" + obj.curr;
                }
            }
        });
    });
</script>
```

- 利用 FreemarkerConfig 实现多长时间前发表内容

1. 配置 Freemarker 模板引擎

```java
import codedragon.eblog.template.TimeAgoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;

    @PostConstruct
    public void setUp() {
        configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
    }
}
```

2. 前端页面调用 timeAgo 函数

```html
<span>${timeAgo(post.created)}</span>
```

3. 效果如下

![](https://gitee.com/xlshi/blog_img/raw/master/img/20200706110932.png)

- 通过 PostTemplate 抽离置顶功能

1.  PostTemplate 

```java
import codedragon.eblog.common.templates.DirectiveHandler;
import codedragon.eblog.common.templates.TemplateDirective;
import codedragon.eblog.service.PostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostTemplate extends TemplateDirective {

    @Autowired
    PostService postService;

    @Override
    public String getName() {
        return "posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn", 1);
        Integer size = handler.getInteger("size", 2);
        Long categoryId = handler.getLong("categoryId");

        IPage page = postService.paging(new Page(pn, size), categoryId, null, level, null, "created");
        handler.put(RESULTS, page).render();
    }
}
```

2. index.ftl 引用

```html
<div class="fly-panel">
    <div class="fly-panel-title fly-filter">
        <a>置顶</a>
        <a href="#signin" class="layui-hide-sm layui-show-xs-block fly-right" id="LAY_goSignin"
           style="color: #FF5722;">去签到</a>
    </div>
    <ul class="fly-list">
        <@posts size=3 level=1>
            <#list results.records as post>
                <@plisting post></@plisting>
            </#list>
        </@posts>
    </ul>
</div>
```

3. 效果如下

![](https://gitee.com/xlshi/blog_img/raw/master/img/20200706113218.png)

- 使用 Redis 缓存实现本周热议功能

1. 使用到的命令

   1. `ZADD` :将一个或多个 `member` 元素及其 `score` 值加入到有序集 `key` 当中

      ```shell
      ZADD day:18 10 post:1
      ZADD day:19 10 post:1
      ZADD day:20 10 post:1
      ZADD day:18 6 post:2
      ZADD day:19 6 post:2
      ZADD day:20 6 post:2
      ```

   2. `ZREVRANGE`:返回有序集 `key` 中，指定区间内的成员，其中成员的位置按 `score` 值递减(从大到小)来排列

      ```shell
      127.0.0.1:6379> ZREVRANGE day:18 0 -1 withscores
      1) "post:1"
      2) "10"
      3) "post:2"
      4) "6"
      ```

   3. `ZUNIONSTORE`:计算给定的一个或多个有序集的并集，其中给定 `key` 的数量必须以 `numkeys` 参数指定，并将该并集(结果集)储存到 `destination`

      ```shell
      127.0.0.1:6379> ZUNIONSTORE week:rank 3 day:20 day:19 day:18
      (integer) 2
      127.0.0.1:6379> keys *
      1) "day:20"
      2) "day:18"
      3) "week:rank"
      4) "day:19"
      ```

   4. `ZINCRBY`:为有序集 `key` 的成员 `member` 的 `score` 值加上增量 `increment` 

      ```shell
      127.0.0.1:6379> ZINCRBY day:18 10 post:1
      "20"
      ```

2. 通过 RedisConfig 配置序列化方式

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(new ObjectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }
}
```







#### 使用说明

将该项目克隆到本地，修改application.yml文件中的数据库连接信息，创建eblog数据库(SQL脚本在resources下的SQL目录)，即可在localhost:8080端口运行该项目


