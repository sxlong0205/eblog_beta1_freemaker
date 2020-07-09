

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



### Day3

- 增加登陆注册功能

1. 完善 login.ftl 和 reg.ftl 页面

login.ftl

```html
<#include "/inc/layout.ftl"/>
<@layout "登陆">
    <div class="layui-container fly-marginTop">
        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li class="layui-this">登入</li>
                    <li><a href="reg.ftl">注册</a></li>
                </ul>
                <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <div class="layui-form layui-form-pane">
                            <form method="post">
                                <div class="layui-form-item">
                                    <label for="L_email" class="layui-form-label">邮箱</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_email" name="email" required lay-verify="required"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_pass" class="layui-form-label">密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_pass" name="pass" required lay-verify="required"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">人类验证</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                               placeholder="请回答后面的问题" autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid">
                                        <span style="color: #c00;">{{d.vercode}}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-filter="*" lay-submit>立即登录</button>
                                    <span style="padding-left:20px;">
                  <a href="forget.html">忘记密码？</a>
                </span>
                                </div>
                                <div class="layui-form-item fly-form-app">
                                    <span>或者使用社交账号登入</span>
                                    <a href="" onclick="layer.msg('正在通过QQ登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-qq" title="QQ登入"></a>
                                    <a href="" onclick="layer.msg('正在通过微博登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-weibo" title="微博登入"></a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="/res/layui/layui.js"></script>
    <script>
        layui.cache.page = 'user';
    </script>
</@layout>
```

reg.ftl

```html
<#include "/inc/layout.ftl"/>
<@layout "注册">
    <div class="layui-container fly-marginTop">
        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li><a href="login.ftl">登入</a></li>
                    <li class="layui-this">注册</li>
                </ul>
                <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <div class="layui-form layui-form-pane">
                            <form method="post">
                                <div class="layui-form-item">
                                    <label for="L_email" class="layui-form-label">邮箱</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_email" name="email" required lay-verify="email"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid layui-word-aux">将会成为您唯一的登入名</div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_username" class="layui-form-label">昵称</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_username" name="username" required
                                               lay-verify="required" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_pass" class="layui-form-label">密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_pass" name="pass" required lay-verify="required"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid layui-word-aux">6到16个字符</div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_repass" class="layui-form-label">确认密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_repass" name="repass" required
                                               lay-verify="required" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">人类验证</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                               placeholder="请回答后面的问题" autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid">
                                        <span style="color: #c00;">{{d.vercode}}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-filter="*" lay-submit>立即注册</button>
                                </div>
                                <div class="layui-form-item fly-form-app">
                                    <span>或者直接使用社交账号快捷注册</span>
                                    <a href="" onclick="layer.msg('正在通过QQ登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-qq" title="QQ登入"></a>
                                    <a href="" onclick="layer.msg('正在通过微博登入', {icon:16, shade: 0.1, time:0})"
                                       class="iconfont icon-weibo" title="微博登入"></a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
    <script>
        layui.cache.page = 'user';
    </script>
</@layout>

```

2. 添加 AuthController

```java
@Controller
public class AuthController extends BaseController {
    @GetMapping("/login")
    public String login(){
        return "/auth/login";
    }

    @GetMapping("/register")
    public String register(){
        return "/auth/reg";
    }
}
```

- 添加图片验证码功能

1. 引入 Maven 依赖

```xml
<!--验证码-->
<dependency>
    <groupId>com.github.axet</groupId>
    <artifactId>kaptcha</artifactId>
    <version>0.0.9</version>
</dependency>
```

2. 添加 KaptchaConfig 配置类

```java
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha producer() {
        Properties propertis = new Properties();
        propertis.put("kaptcha.border", "no");
        propertis.put("kaptcha.image.height", "38");
        propertis.put("kaptcha.image.width", "150");
        propertis.put("kaptcha.textproducer.font.color", "black");
        propertis.put("kaptcha.textproducer.font.size", "32");
        Config config = new Config(propertis);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }
}
```

3. 在 AuthController 中添加相应方法

```java
    @Autowired
    Producer producer;

    @GetMapping("/capthca.jpg")
    public void kaptcha(HttpServletResponse resp) throws IOException {
        //验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        resp.setHeader("Cache-Control", "no-store, nocache");
        resp.setContentType("image/jpeg");
        ServletOutputStream outputStream = resp.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }
```

4. 前端 JS 实现点击刷新验证码功能

```javascript
<div>
    <img id="capthca" src="/capthca.jpg">
</div>

$("#capthca").click(function () {
    this.src = "/capthca.jpg";
})
```

5. 编写统一返回结果 Result 类

```java
@Data
public class Result implements Serializable {
    //状态码 0代表成功 -1代表失败
    private int status;

    private String msg;

    //返回结果
    private Object data;

    private String action;

    //操作失败返回结果
    public static Result fail(String msg, Object data) {
        Result result = new Result();
        result.status = -1;
        result.msg = msg;
        result.data = null;
        return result;
    }

    //操作成功返回结果
    public static Result success(Object data) {
        return Result.success("操作成功", data);
    }
    public static Result success() {
        return Result.success("操作成功", null);
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.status = 0;
        result.msg = msg;
        result.data = data;
        return result;
    }

    //返回指定跳转页面
    public Result action(String action){
        this.action = action;
        return this;
    }
}
```



- 引入 Hibernate Validator 进行表单校验

1. 引入 ValidationUtil 工具类，这个类直接从网上 copy 哈

```java
public class ValidationUtil {

    /**
     * 开启快速结束模式 failFast (true)
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();
    /**
     * 校验对象
     *
     * @param t bean
     * @param groups 校验组
     * @return ValidResult
     */
    public static <T> ValidResult validateBean(T t,Class<?>...groups) {
        ValidResult result = new ValidationUtil().new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validate(t,groups);
        boolean hasError = violationSet != null && violationSet.size() > 0;
        result.setHasErrors(hasError);
        if (hasError) {
            for (ConstraintViolation<T> violation : violationSet) {
                result.addError(violation.getPropertyPath().toString(), violation.getMessage());
            }
        }
        return result;
    }
    /**
     * 校验bean的某一个属性
     *
     * @param obj          bean
     * @param propertyName 属性名称
     * @return ValidResult
     */
    public static <T> ValidResult validateProperty(T obj, String propertyName) {
        ValidResult result = new ValidationUtil().new ValidResult();
        Set<ConstraintViolation<T>> violationSet = validator.validateProperty(obj, propertyName);
        boolean hasError = violationSet != null && violationSet.size() > 0;
        result.setHasErrors(hasError);
        if (hasError) {
            for (ConstraintViolation<T> violation : violationSet) {
                result.addError(propertyName, violation.getMessage());
            }
        }
        return result;
    }
    /**
     * 校验结果类
     */
    @Data
    public class ValidResult {

        /**
         * 是否有错误
         */
        private boolean hasErrors;

        /**
         * 错误信息
         */
        private List<ErrorMessage> errors;

        public ValidResult() {
            this.errors = new ArrayList<>();
        }
        public boolean hasErrors() {
            return hasErrors;
        }

        public void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }

        /**
         * 获取所有验证信息
         * @return 集合形式
         */
        public List<ErrorMessage> getAllErrors() {
            return errors;
        }
        /**
         * 获取所有验证信息
         * @return 字符串形式
         */
        public String getErrors(){
            StringBuilder sb = new StringBuilder();
            for (ErrorMessage error : errors) {
                sb.append(error.getPropertyPath()).append(":").append(error.getMessage()).append(" ");
            }
            return sb.toString();
        }

        public void addError(String propertyName, String message) {
            this.errors.add(new ErrorMessage(propertyName, message));
        }
    }

    @Data
    public class ErrorMessage {

        private String propertyPath;

        private String message;

        public ErrorMessage() {
        }

        public ErrorMessage(String propertyPath, String message) {
            this.propertyPath = propertyPath;
            this.message = message;
        }
    }
```

2. 使用 Hibernate Validator 对属性值进行校验

```java
/**
 * 昵称
 */
@NotBlank(message = "用户名不能为空")
private String username;

/**
 * 密码
 */
@NotBlank(message = "密码不能为空")
private String password;

/**
 * 邮件
 */
@Email
@NotBlank(message = "邮件不能为空")
private String email;
```

3. 在 AuthController 中编写方法进行注册校验

```java
@ResponseBody
@PostMapping("/register")
public Result doRegister(User user, String repass, String vercode) {

    //校验用户输入是否合法
    ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
    if (validResult.hasErrors())
        return Result.fail(validResult.getErrors());

    //校验密码
    if (!user.getPassword().equals(repass))
        return Result.fail("两次输入密码不相同");

    //获取并校验验证码
    String capthca = (String) req.getSession().getAttribute(KAPTCHA_SESSION_KEY);
    System.out.println(capthca);
    if (vercode == null || !vercode.equalsIgnoreCase(capthca))
        return Result.fail("验证码输入不正确");
    return Result.success().action("/login");
}
```

- 注册功能

1.  UserServiceImpl 验证用户名和密码是否已经被注册
2. 使用 hutool 自带md5加密对用户密码进行加密

```java
//注册功能实现
@Override
public Result register(User user) {
    //判断用户名邮箱是否已经被注册
    int count = this.count(new QueryWrapper<User>()
            .eq("email", user.getEmail())
            .or()
            .eq("username", user.getUsername()));
    if (count > 0) return Result.fail("用户名或邮箱已被占用");

    User temp = new User();
    temp.setUsername(user.getUsername());
    temp.setPassword(SecureUtil.md5(user.getPassword()));
    temp.setEmail(user.getEmail());

    temp.setCreated(new Date());
    temp.setPoint(0);
    temp.setVipLevel(0);
    temp.setCommentCount(0);
    temp.setPostCount(0);
    this.save(temp);
    return Result.success();
}
```

- 注册功能

1. 集成 Shiro 权限框架，引入 Maven 依赖

```xml
<!--集成Shiro权限框架-->
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>net.mingsoft</groupId>
    <artifactId>shiro-freemarker-tags</artifactId>
    <version>0.1</version>
</dependency>
```

2. 添加 ShiroConfig 配置类

```java
@Slf4j
@Configuration
public class ShiroConfig {
    //配置安全中心
    @Bean
    public SecurityManager securityManager(AccountRealm accountRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(accountRealm);
        log.info("---------------->securityManager注入成功");
        return securityManager;
    }

    //配置拦截器
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);
        // 配置登录的url和登录成功的url
        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/user/center");
        // 配置未授权跳转页面
        filterFactoryBean.setUnauthorizedUrl("/error/403");

        Map<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("/login", "anon");
        filterFactoryBean.setFilterChainDefinitionMap(hashMap);

        return filterFactoryBean;
    }
}
```

3. 登陆操作返回属性 AccountProfile 类

```java
public class AccountProfile implements Serializable {
    private String username;
    private String email;
    private Data created;
}
```

4.  AccountRealm 实现权限和角色的自动检查

```java
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        AccountProfile profile = userService.login(usernamePasswordToken.getUsername(), String.valueOf(usernamePasswordToken.getPassword()));
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
        return info;
    }
}
```

5. 在 UserServiceImpl 实现登陆功能

```java
@Override
public AccountProfile login(String email, String password) {
    User user = this.getOne(new QueryWrapper<User>().eq("email", email));
    if (user == null)
        throw new UnknownAccountException();
    if (!user.getPassword().equals(password))
        throw new IncorrectCredentialsException();

    user.setLasted(new Date());
    this.updateById(user);
    AccountProfile profile = new AccountProfile();
    BeanUtils.copyProperties(user, profile);
    return profile;
}
```

- 退出功能

1. 更新 header.ftl 页面

```html
<div class="fly-header layui-bg-black">
    <div class="layui-container">
        <a class="fly-logo" href="/">
            <img src="https://www.markerhub.com/dist/images/logo/markerhub-logo.png" alt="MarkerHub" style="height: 41px;">
        </a>
        <ul class="layui-nav fly-nav layui-hide-xs">
            <li class="layui-nav-item layui-this">
                <a href="/"><i class="iconfont icon-jiaoliu"></i>主页</a>
            </li>
            <li class="layui-nav-item">
                <a target="_blank" href="https://mp.weixin.qq.com/s/lR5LC5GnD2Gs59ecV5R0XA"><i class=""></i>最新企业面试题</a>
            </li>
        </ul>

        <ul class="layui-nav fly-nav-user">

            <@shiro.guest>
                <!-- 未登入的状态 -->
                <li class="layui-nav-item">
                    <a class="iconfont icon-touxiang layui-hide-xs" href="/login"></a>
                </li>
                <li class="layui-nav-item">
                    <a href="/login">登入</a>
                </li>
                <li class="layui-nav-item">
                    <a href="/register">注册</a>
                </li>
                <li class="layui-nav-item layui-hide-xs">
                    <a href="/app/qq/" onclick="layer.msg('正在通过QQ登入', {icon:16, shade: 0.1, time:0})" title="QQ登入" class="iconfont icon-qq"></a>
                </li>
                <li class="layui-nav-item layui-hide-xs">
                    <a href="/app/weibo/" onclick="layer.msg('正在通过微博登入', {icon:16, shade: 0.1, time:0})" title="微博登入" class="iconfont icon-weibo"></a>
                </li>
            </@shiro.guest>

            <@shiro.user>
                <!-- 登入后的状态 -->
                <li class="layui-nav-item">
                    <a class="fly-nav-avatar" href="javascript:;">
                        <cite class="layui-hide-xs"><@shiro.principal property="username" /></cite>
                        <i class="iconfont icon-renzheng layui-hide-xs" title="认证信息：layui 作者"></i>
                        <img src="<@shiro.principal property="avatar" />">
                    </a>
                    <dl class="layui-nav-child">
                        <dd><a href="/user/set"><i class="layui-icon">&#xe620;</i>基本设置</a></dd>
                        <dd><a href="/user/mess"><i class="iconfont icon-tongzhi" style="top: 4px;"></i>我的消息</a></dd>
                        <dd><a href="/user/home"><i class="layui-icon" style="margin-left: 2px; font-size: 22px;">&#xe68e;</i>我的主页</a></dd>
                        <hr style="margin: 5px 0;">
                        <dd><a href="/user/logout/" style="text-align: center;">退出</a></dd>
                    </dl>
                </li>
            </@shiro.user>
        </ul>
    </div>
</div>
```

2. 实现退出功能

```java
//退出功能
@RequestMapping("/user/logout")
public String logout(){
    SecurityUtils.getSubject().logout();
    return "redirect:/";
}
```

- 我的主页功能

1. 在 BaseController 获取用户信息

```java
//从Shiro中获取用户信息
protected AccountProfile getProfile(){
    return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
}

//获取用户ID
protected Long getProfileId(){
    return getProfile().getId();
}
```

2. 在 UserController 中实现方法

```java
//实现个人主页功能
@GetMapping("/user/home")
public String home() {
    User user = userService.getById(getProfileId());
    List<Post> posts = postService.list(new QueryWrapper<Post>()
            .eq("user_id", getProfileId())
            .orderByDesc("created"));
    req.setAttribute("user", user);
    req.setAttribute("posts", posts);
    return "/user/home";
}
```

3. 在 ShiroConfig 中添加拦截器

```java
hashMap.put("/user/home", "authc");
```

4. home.ftl

```html
<#include "/inc/layout.ftl"/>

<@layout "我的主页">
    <div class="fly-home fly-panel" style="background-image: url();">
        <img src="${user.avatar}" alt="${user.username}">
        <i class="iconfont icon-renzheng" title="Fly社区认证"></i>
        <h1>
            ${user.username}
            <i class="iconfont icon-nan"></i>
            <!-- <i class="iconfont icon-nv"></i>  -->
            <i class="layui-badge fly-badge-vip">VIP3</i>
            <!--
            <span style="color:#c00;">（管理员）</span>
            <span style="color:#5FB878;">（社区之光）</span>
            <span>（该号已被封）</span>
            -->
        </h1>

        <p style="padding: 10px 0; color: #5FB878;">认证信息：layui 作者</p>

        <p class="fly-home-info">
            <i class="iconfont icon-kiss" title="飞吻"></i><span style="color: #FF7200;">66666 飞吻</span>
            <i class="iconfont icon-shijian"></i><span>${user.created?string('yyyy-MM-dd')} 加入</span>
            <i class="iconfont icon-chengshi"></i><span>来自地球</span>
        </p>

        <p class="fly-home-sign">（${user.sign!'这个人好懒，什么都没留下！'}）</p>

        <div class="fly-sns" data-user="">
            <a href="javascript:;" class="layui-btn layui-btn-primary fly-imActive" data-type="addFriend">加为好友</a>
            <a href="javascript:;" class="layui-btn layui-btn-normal fly-imActive" data-type="chat">发起会话</a>
        </div>

    </div>

    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md6 fly-home-jie">
                <div class="fly-panel">
                    <h3 class="fly-panel-title">${user.username} 最近的提问</h3>
                    <ul class="jie-row">
                        <#list posts as post>
                            <#if !posts>
                                <div class="fly-none" style="min-height: 50px; padding:30px 0; height:auto;">
                                    <i style="font-size:14px;">没有发表任何求解</i>
                                </div>
                            </#if>
                            <li>
                                <#if post.recommend><span class="fly-jing">精</span></#if>
                                <a href="/post/${post.id}" class="jie-title"> ${post.title}</a>
                                <i>${timeAgo(post.created)}</i>
                                <em class="layui-hide-xs">${post.viewCount}阅/${post.commentCount}答</em>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>

            <div class="layui-col-md6 fly-home-da">
                <div class="fly-panel">
                    <h3 class="fly-panel-title">${user.username} 最近的回答</h3>
                    <ul class="home-jieda">
                        <div class="fly-none" style="min-height: 50px; padding:30px 0; height:auto;">
                            <span>没有回答任何问题</span></div>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script>
        layui.cache.page = 'user';
    </script>
</@layout>
```

- 用户基本设置页面功能实现

1. 在 UserController 中实现方法

```java
//用户设置页面
@GetMapping("/user/set")
public String set() {
    User user = userService.getById(getProfileId());
    req.setAttribute("user", user);
    return "/user/set";
}
```

2. 在 ShiroConfig 中添加拦截器

```java
hashMap.put("/user/set", "authc");
```

3. set.ftl

```html
<#include "/inc/layout.ftl"/>

<@layout "基本设置">
    <div class="layui-container fly-marginTop fly-user-main">
        <#--        <@centerLeft level=2></@centerLeft>-->
        <ul class="layui-nav layui-nav-tree layui-inline" lay-filter="uesr">
            <li class="layui-nav-item">
                <a href="/user/home">
                    <i class="layui-icon">&#xe609;</i>
                    我的主页
                </a>
            </li>
            <li class="layui-nav-item">
                <a href="/user/index">
                    <i class="layui-icon">&#xe612;</i>
                    用户中心
                </a>
            </li>
            <li class="layui-nav-item">
                <a href="/user/set">
                    <i class="layui-icon">&#xe620;</i>
                    基本设置
                </a>
            </li>
            <li class="layui-nav-item">
                <a href="/user/message">
                    <i class="layui-icon">&#xe611;</i>
                    我的消息
                </a>
            </li>
        </ul>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title" id="LAY_mine">
                    <li class="layui-this" lay-id="info">我的资料</li>
                    <li lay-id="avatar">头像</li>
                    <li lay-id="pass">密码</li>
                    <li lay-id="bind">帐号绑定</li>

                    <@shiro.hasRole name="admin">
                        <li lay-id="es">同步ES</li>
                    </@shiro.hasRole>
                </ul>
                <div class="layui-tab-content" style="padding: 20px 0;">
                    <div class="layui-form layui-form-pane layui-tab-item layui-show">
                        <form method="post">
                            <div class="layui-form-item">
                                <label for="L_email" class="layui-form-label">邮箱</label>
                                <div class="layui-input-inline">
                                    <input type="text" id="L_email" name="email" required lay-verify="email"
                                           autocomplete="off" value="${user.email}" class="layui-input" readonly>
                                </div>
                                <div class="layui-form-mid layui-word-aux">如果您在邮箱已激活的情况下，变更了邮箱，需<a href="activate.html"
                                                                                                   style="font-size: 12px; color: #4f99cf;">重新验证邮箱</a>。
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label for="L_username" class="layui-form-label">昵称</label>
                                <div class="layui-input-inline">
                                    <input type="text" id="L_username" name="username" required lay-verify="required"
                                           value="${user.username}" autocomplete="off" class="layui-input">
                                </div>
                                <div class="layui-inline">
                                    <div class="layui-input-inline">
                                        <input type="radio" name="sex" <#if user.gender =='0'>checked</#if>
                                               title="男">
                                        <input type="radio" name="sex" <#if user.gender =='1'>checked</#if>
                                               title="女">
                                    </div>
                                </div>
                            </div>
                            <div class="layui-form-item layui-form-text">
                                <label for="L_sign" class="layui-form-label">签名</label>
                                <div class="layui-input-block">
                                    <textarea placeholder="随便写些什么刷下存在感" id="L_sign" name="sign" autocomplete="off"
                                              class="layui-textarea" style="height: 80px;"></textarea>
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <button class="layui-btn" key="set-mine" lay-filter="*" lay-submit alert="true"
                                        reload="true" alert="true" reload="true ">确认修改
                                </button>
                            </div>
                        </form>
                    </div>

                    <div class="layui-form layui-form-pane layui-tab-item">
                        <div class="layui-form-item">
                            <div class="avatar-add">
                                <p>建议尺寸168*168，支持jpg、png、gif，最大不能超过50KB</p>
                                <button type="button" class="layui-btn upload-img">
                                    <i class="layui-icon">&#xe67c;</i>上传头像
                                </button>
                                <img src="<@shiro.principal property="avatar" />">
                                <span class="loading"></span>
                            </div>
                        </div>
                    </div>

                    <div class="layui-form layui-form-pane layui-tab-item">
                        <form action="/user/" method="post">
                            <div class="layui-form-item">
                                <label for="L_nowpass" class="layui-form-label">当前密码</label>
                                <div class="layui-input-inline">
                                    <input type="password" id="L_nowpass" name="nowpass" required lay-verify="required"
                                           autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label for="L_pass" class="layui-form-label">新密码</label>
                                <div class="layui-input-inline">
                                    <input type="password" id="L_pass" name="pass" required lay-verify="required"
                                           autocomplete="off" class="layui-input">
                                </div>
                                <div class="layui-form-mid layui-word-aux">6到16个字符</div>
                            </div>
                            <div class="layui-form-item">
                                <label for="L_repass" class="layui-form-label">确认密码</label>
                                <div class="layui-input-inline">
                                    <input type="password" id="L_repass" name="repass" required lay-verify="required"
                                           autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <button class="layui-btn" key="set-mine" lay-filter="*" lay-submit alert="true"
                                        reload="true">确认修改
                                </button>
                            </div>
                        </form>
                    </div>

                    <div class="layui-form layui-form-pane layui-tab-item">
                        <ul class="app-bind">
                            <li class="fly-msg app-havebind">
                                <i class="iconfont icon-qq"></i>
                                <span>已成功绑定，您可以使用QQ帐号直接登录Fly社区，当然，您也可以</span>
                                <a href="javascript:;" class="acc-unbind" type="qq_id">解除绑定</a>

                                <!-- <a href="" onclick="layer.msg('正在绑定微博QQ', {icon:16, shade: 0.1, time:0})" class="acc-bind" type="qq_id">立即绑定</a>
                                <span>，即可使用QQ帐号登录Fly社区</span> -->
                            </li>
                            <li class="fly-msg">
                                <i class="iconfont icon-weibo"></i>
                                <!-- <span>已成功绑定，您可以使用微博直接登录Fly社区，当然，您也可以</span>
                                <a href="javascript:;" class="acc-unbind" type="weibo_id">解除绑定</a> -->

                                <a href="" class="acc-weibo" type="weibo_id"
                                   onclick="layer.msg('正在绑定微博', {icon:16, shade: 0.1, time:0})">立即绑定</a>
                                <span>，即可使用微博帐号登录Fly社区</span>
                            </li>
                        </ul>
                    </div>

                    <@shiro.hasRole name="admin">
                        <div class="layui-form layui-form-pane layui-tab-item">
                            <form action="/admin/initEsData" method="post">
                                <button class="layui-btn" key="set-mine" lay-filter="*" lay-submit alert="true">同步ES数据
                                </button>
                            </form>
                        </div>
                    </@shiro.hasRole>
                </div>

            </div>
        </div>
    </div>

    <script>
        layui.cache.page = 'user';
    </script>
</@layout>
```

4. 修改 /res/mods/index.js 下的实现点击保存后重新刷新页面

```javascript
//表单提交
form.on('submit(*)', function(data){
  var action = $(data.form).attr('action'), button = $(data.elem);
  fly.json(action, data.field, function(res){
    var end = function(){
      if(res.action){
        location.href = res.action;
      }
      if (button.attr('reload')){
        location.reload();
      }
      // else {
      //   fly.form[action||button.attr('key')](data.field, data.form);
      // }
    };
    if(res.status == 0){
      button.attr('alert') ? layer.alert(res.msg, {
        icon: 1,
        time: 10*1000,
        end: end
      }) : end();
    };
  });
  return false;
});
```

5. 在 UserController 中实现相应方法

```java
@ResponseBody
@PostMapping("/user/set")
public Result doSet(User user) {
    if (StrUtil.isBlank(user.getUsername()))
        return Result.fail("昵称不能为空");
    int count = userService.count(new QueryWrapper<User>()
            .eq("username", getProfile().getUsername())
            .ne("id", getProfileId())
    );

    //用户名不能重复
    if (count > 0)
        return Result.fail("该昵称已被占用");

    //将用户更新的值入库
    User temp = userService.getById(getProfileId());
    temp.setUsername(user.getUsername());
    temp.setGender(user.getGender());
    temp.setSign(user.getSign());
    userService.updateById(temp);

    //更新Shiro中的用户信息
    AccountProfile profile = getProfile();
    profile.setUsername(temp.getUsername());
    profile.setSign(temp.getSign());

    return Result.success().action("/user/set#info");
}
```

6. 实现用户头像上传功能

   1. 修改 set.ftl

   ```html
   <div class="layui-form layui-form-pane layui-tab-item">
       <div class="layui-form-item">
           <div class="avatar-add">
               <p>建议尺寸168*168，支持jpg、png、gif，最大不能超过50KB</p>
               <button type="button" class="layui-btn upload-img">
                   <i class="layui-icon">&#xe67c;</i>上传头像
               </button>
               <img src="<@shiro.principal property="avatar" />">
               <span class="loading"></span>
           </div>
       </div>
   </div>
   ```

   2. 编写 UploadUtil 上传工具类

   ```java
   @Slf4j
   @Component
   public class UploadUtil {
       @Autowired
       Consts consts;
   
       public final static String type_avatar = "avatar";
   
       public Result upload(String type, MultipartFile file) throws IOException {
   
           if(StrUtil.isBlank(type) || file.isEmpty()) {
               return Result.fail("上传失败");
           }
   
           // 获取文件名
           String fileName = file.getOriginalFilename();
           log.info("上传的文件名为：" + fileName);
           // 获取文件的后缀名
           String suffixName = fileName.substring(fileName.lastIndexOf("."));
           log.info("上传的后缀名为：" + suffixName);
           // 文件上传后的路径
           String filePath = consts.getUploadDir();
   
           if ("avatar".equalsIgnoreCase(type)) {
               AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
               fileName = "/avatar/avatar_" + profile.getId() + suffixName;
   
           } else if ("post".equalsIgnoreCase(type)) {
               fileName = "/post/post_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + suffixName;
           }
   
           File dest = new File(filePath + fileName);
           // 检测是否存在目录
           if (!dest.getParentFile().exists()) {
               dest.getParentFile().mkdirs();
           }
           try {
               file.transferTo(dest);
               log.info("上传成功后的文件路径未：" + filePath + fileName);
   
               String path = filePath + fileName;
               String url = "/upload" + fileName;
   
               log.info("url ---> {}", url);
   
               return Result.success(url);
           } catch (IllegalStateException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
   
           return Result.success(null);
       }
   }
   ```

   3. 配置通用功能 Consts 类

   ```java
   @Slf4j
   @Component
   @Data
   public class Consts {
       @Value("${file.upload.dir}")
       private String uploadDir;
   }
   ```

   4. 在 application.yml 中配置文件存放路径

   ```yml
   file:
     upload:
       dir: ${user.dir}/upload
   ```

   5. 在 UserController 中编写对应方法

   ```java
   //用户上传头像
   @ResponseBody
   @PostMapping("/user/upload")
   public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
       return uploadUtil.upload(UploadUtil.type_avatar, file);
   }
   ```

   6. 在 doSet 方法中更新用户头像信息

   ```java
   //更新用户头像
   if (StrUtil.isNotBlank(user.getAvatar())) {
       User temp = userService.getById(getProfileId());
       temp.setAvatar(user.getAvatar());
       userService.updateById(temp);
   
       AccountProfile profile = getProfile();
       profile.setAvatar(temp.getAvatar());
       return Result.success().action("/user/set#avatar");
   }
   ```

   7. 在 ShiroConfig 中添加过滤器

   ```java
   hashMap.put("/user/upload", "authc");
   ```

   8. 配置用户上传图片的路径 MvcConfig

   ```java
   @Configuration
   public class MvcConfig implements WebMvcConfigurer {
       @Autowired
       Consts consts;
   
       @Override
       public void addResourceHandlers(ResourceHandlerRegistry registry) {
           registry.addResourceHandler("/upload/avatar/**")
                   .addResourceLocations("file:///" + consts.getUploadDir() + "/avatar/");
       }
   }
   ```

7. 实现更新密码功能

   1. 编写 UserController 中的更新密码方法

   ```java
       @ResponseBody
       @PostMapping("/user/repass")
       public Result repass(String nowpass, String pass, String repass) {
           if(!pass.equals(repass)) {
               return Result.fail("两次密码不相同");
           }
   
           User user = userService.getById(getProfileId());
   
           String nowPassMd5 = SecureUtil.md5(nowpass);
           if(!nowPassMd5.equals(user.getPassword())) {
               return Result.fail("密码不正确");
           }
   
           user.setPassword(SecureUtil.md5(pass));
           userService.updateById(user);
   
           return Result.success().action("/user/set#pass");
   
       }
   ```

### Day4

- 完成用户信息页面

1. 在 common.ftl 中定义公共左侧边栏

```html
<#macro centerLeft level>
    <ul class="layui-nav layui-nav-tree layui-inline" lay-filter="uesr">
        <li class="layui-nav-item <#if level == 0>layui-this</#if>">
            <a href="/post/home">
                <i class="layui-icon">&#xe609;</i>
                我的主页
            </a>
        </li>
        <li class="layui-nav-item <#if level == 1>layui-this</#if>">
            <a href="/user/index">
                <i class="layui-icon">&#xe612;</i>
                用户中心
            </a>
        </li>
        <li class="layui-nav-item <#if level == 2>layui-this</#if>">
            <a href="/user/set">
                <i class="layui-icon">&#xe620;</i>
                基本设置
            </a>
        </li>
        <li class="layui-nav-item <#if level == 3>layui-this</#if>">
            <a href="/user/message">
                <i class="layui-icon">&#xe611;</i>
                我的消息
            </a>
        </li>
    </ul>
</#macro>
```

2. 在 set.ftl 标签中引用

```html
<@centerLeft level=2></@centerLeft>
```

- 实现发表文章懒加载功能

1. index.ftl

```html
<#include "/inc/layout.ftl"/>

<@layout "用户中心">
    <div class="layui-container fly-marginTop fly-user-main">
        <@centerLeft level=1></@centerLeft>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title" id="LAY_mine">
                    <li data-type="mine-jie" lay-id="index" class="layui-this">我发的帖（<span>89</span>）</li>
                    <li data-type="collection" data-url="/collection/find/" lay-id="collection">我收藏的帖（<span>16</span>）
                    </li>
                </ul>
                <div class="layui-tab-content" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <ul class="mine-view jie-row" id="fabu">
                            <script id="tpl-fabu" type="text/html">
                                <li>
                                    <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{ d.title }}</a>
                                    <i>{{layui.util.toDateString(d.created, 'yyyy-MM-dd HH:mm:ss')}}</i>
                                    <a class="mine-edit" href="/post/edit?id={{d.id}}">编辑</a>
                                    <em>{{d.viewCount}}阅/{{d.commentCount}}答</em>
                                </li>
                            </script>

                        </ul>
                        <div id="LAY_page"></div>
                    </div>
                    <div class="layui-tab-item">
                        <ul class="mine-view jie-row" id="collection">
                            <script id="tpl-collection" type="text/html">
                                <li>
                                    <a class="jie-title" href="/post/{{d.id}}" target="_blank">{{d.title}}</a>
                                    <i>收藏于{{layui.util.timeAgo(d.created, true)}}</i>
                                </li>
                            </script>
                        </ul>
                        <div id="LAY_page1"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        layui.cache.page = 'user';
        layui.use(['laytpl', 'flow', 'util'], function () {
            var $ = layui.jquery;
            var laytpl = layui.laytpl;
            var flow = layui.flow;
            var util = layui.util;
            flow.load({
                elem: '#fabu' //指定列表容器
                , isAuto: false
                , done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                    var lis = [];

                    //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                    $.get('/user/public?pn=' + page, function (res) {
                        //假设你的列表返回在data集合中
                        layui.each(res.data.records, function (index, item) {
                            var tpl = $("#tpl-fabu").html();
                            laytpl(tpl).render(item, function (html) {
                                $("#fabu .layui-flow-more").before(html);
                            });
                        });

                        //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                        //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现更多加载
                        next(lis.join(''), page < res.data.pages);
                    });
                }
            });

            flow.load({
                elem: '#collection'
                , isAuto: false
                , done: function (page, next) {
                    var lis = [];
                    $.get('/user/collection?pn=' + page, function (res) {
                        layui.each(res.data.records, function (index, item) {
                            var tpl = $("#tpl-collection").html();
                            laytpl(tpl).render(item, function (html) {
                                $("#collection .layui-flow-more").before(html);
                            });
                        });
                        next(lis.join(''), page < res.data.pages);
                    });
                }
            });
        });
    </script>
</@layout>
```

2. 在 UserController 中编写相应的方法

```java
@ResponseBody
@GetMapping("/user/public")
public Result userP() {
    IPage page = postService.page(getPage(), new QueryWrapper<Post>()
            .eq("user_id", getProfileId())
            .orderByDesc("created"));
    return Result.success(page);
}

@ResponseBody
@GetMapping("/user/collection")
public Result collection() {
    IPage id = postService.page(getPage(), new QueryWrapper<Post>()
            .inSql("id", "select post_id from user_collection where user_id = " + getProfileId())
    );
    return Result.success(id);
}
```

- 完成我的消息页面

1. 完成  UserMessage、UserMessageService、UserMessageServiceImpl、UserMessageMapper、UserMessageMapper.xml、UserMessageVo

UserMessage

```java
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发送消息的用户ID
     */
    private Long fromUserId;

    /**
     * 接收消息的用户ID
     */
    private Long toUserId;

    /**
     * 消息可能关联的帖子
     */
    private Long postId;

    /**
     * 消息可能关联的评论
     */
    private Long commentId;

    private String content;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * @author: Code Dragon
     * @description: 判断消息已读还是未读
     * @date: 2020/7/9 18:04
     * @param null
     * @return
     */
    private Integer status;
}
```

UserMessageService

```java
public interface UserMessageService extends IService<UserMessage> {

    IPage paging(Page page, QueryWrapper<UserMessage> wrapper);
}
```

UserMessageServiceImpl

```java
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Autowired
    UserMessageMapper userMessageMapper;

    @Override
    public IPage paging(Page page, QueryWrapper<UserMessage> wrapper) {
        return userMessageMapper.selectMessages(page, wrapper);
    }
}
```

UserMessageMapper

```java
@Component
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    IPage<UserMessageVo> selectMessages(Page page, QueryWrapper<UserMessage> wrapper);
}
```

UserMessageMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="codedragon.eblog.mapper.UserMessageMapper">
    <select id="selectMessages" resultType="codedragon.eblog.VO.UserMessageVo">
		SELECT
			m.*,
			( SELECT username FROM `user` WHERE id = m.from_user_id ) AS fromUserName,
			( SELECT title FROM post WHERE id = m.post_id ) AS postTitle
		FROM
			`user_message` m

		${ew.customSqlSegment}

	</select>
</mapper>
```

UserMessageVo

```java
@Data
public class UserMessageVo extends UserMessage {
    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;
}
```

2. 在 ShiroConfig 中加入过滤器

```java
hashMap.put("/user/message", "authc");
```

3. 在 UserController 中编写删除评论功能

```java
//删除评论
@ResponseBody
@PostMapping("/message/remove")
public Result msgRemove(Long id,@RequestParam(defaultValue = "false") Boolean all) {
    boolean remove = userMessageService.remove(new QueryWrapper<UserMessage>()
            .eq("to_user_id", getProfileId())
            .eq(!all, "id", id)
    );

    return remove ? Result.success(null) : Result.fail("删除失败");
}
```

#### 使用说明 

将该项目克隆到本地，修改application.yml文件中的数据库连接信息，创建eblog数据库(SQL脚本在resources下的SQL目录)，即可在localhost:8080端口运行该项目


