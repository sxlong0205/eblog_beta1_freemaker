

# eblog_beta1

#### 介绍
该项目为一个博客项目，前端利用Freemarker渲染引擎搭建，后台使用SpringBoot搭建。

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







#### 使用说明

将该项目克隆到本地，修改application.yml文件中的数据库连接信息，创建eblog数据库(SQL脚本在resources下的SQL目录)，即可在localhost:8080端口运行该项目


