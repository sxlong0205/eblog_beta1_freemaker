

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

![](C:\Users\sxl\AppData\Roaming\Typora\typora-user-images\image-20200705201434451.png)

- 使用 MyBatisPlus 自动生成插件生成了数据库对应的实体类、Mapper 接口和 Service 接口实现类

  ![](C:\Users\sxl\AppData\Roaming\Typora\typora-user-images\image-20200705201316035.png)

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

  

#### 使用说明

将该项目克隆到本地，修改application.yml文件中的数据库连接信息，创建eblog数据库，即可在localhost:8080端口运行该项目


