
- [Spring Boot项目中的parent](#Spring_Boot项目中的parent)
- [Spring Boot的配置文件](#Spring_Boot的配置文件)
    * [properties](#properties)
    * [yaml](#yaml)
- [Spring Boot中的静态资源](#Spring_Boot中的静态资源)
    * [SSM中的配置](#SSM中的配置)
    * [Spring Boot中的配置](#Spring_Boot中的配置)
- [Spring Boot中的自定义异常处理](#Spring_Boot中的自定义异常处理)
    * [静态异常页面](#静态异常页面)
    * [动态异常页面](#动态异常页面)
    * [自定义异常数据](#自定义异常数据)
    * [自定义异常视图](#自定义异常视图)

---
* Refs:
    * > http://www.javaboy.org/springboot/
---

## Spring Boot项目中的parent
* 当我们创建一个Spring Boot工程时，可以继承自一个spring-boot-starter-parent，也可以不继承自它，其基本功能有: 
    * 定义了Java编译版本为1.8。
    * 使用UTF-8格式编码。
    * 继承自`spring-boot-dependencies`，这个里边定义了依赖的版本，也正是因为继承了这个依赖，所以我们在写依赖时才不需要写版本号。
    * 执行打包操作的配置。
    * 自动化的资源过滤。
    * 自动化的插件配置。
    * 针对`application.properties`和`application.yml`的资源过滤，包括通过`profile`定义的不同环境的配置文件，例如`application-dev.properties`和`application-dev.yml`。
* 由于`application.properties`和`application.yml`文件接受Spring样式占位符`$ {...}`，因此 Maven 过滤更改为使用`@ .. @`占位符，当然开发者可以通过设置名为`resource.delimiter`的Maven属性来覆盖`@ .. @`占位符。

## Spring Boot的配置文件
* 在 Spring Boot中，配置文件有两种不同的格式:
    * properties
        * 比较常见
        * 数据是无序的
    * yaml
        * 更加简洁
        * 数据是有序的

### properties
* 配置文件位置
    * 在 Spring Boot中，一共有4个地方可以存放application.properties文件。
        * 当前项目根目录下的config目录下
        * 当前项目的根目录下
        * resources目录下的config目录下
        * resources目录下
    * 这四个位置是默认位置，即Spring Boot启动，默认会从这四个位置按顺序去查找相关属性并加载。可以通过`spring.config.location`属性来手动的指定配置文件位置，指定完成后，系统就会自动去指定目录下查找application.properties文件。
        * 如果项目已经打包成jar，在启动命令中加入位置参数即可: 
            ```
            java -jar properties-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/javaboy/
            ```
* 配置文件名
    * 对于application.properties而言，它不一定非要叫application，但是项目默认是去加载名为application的配置文件；如果我们的配置文件不叫application，需要用`spring.config.name`明确指定配置文件的文件名。
* 普通的属性注入
    * 由于Spring Boot中，默认会自动加载application.properties文件，所以简单的属性注入可以直接在这个配置文件中写。
        ```java
        public class Book {
            private Long id;
            private String name;
            private String author;
        }
        ```
        ```xml
        book.name=三国演义
        book.author=罗贯中
        book.id=1
        ```
        * 可以直接通过`@Value`注解将这些属性注入到`Book`对象中: 
        ```java
        @Component
        public class Book {
            @Value("${book.id}")
            private Long id;
            @Value("${book.name}")
            private String name;
            @Value("${book.author}")
            private String author;
        }
        ```
        * `Book`对象本身也要交给Spring容器去管理，如果`Book`没有交给Spring容器，那么`Book`中的属性也无法从Spring容器中获取到值。
        * 配置完成后，在Controller或者单元测试中注入`Book`对象，启动项目，就可以看到属性已经注入到对象中了。
    * 一般来说，我们在application.properties文件中主要存放系统配置，这种自定义配置不建议放在该文件中，可以自定义properties文件来存在自定义配置。
    * 项目启动并不会自动的加载自定义配置文件
        * 如果是在XML配置中，可以通过如下方式引用该properties文件: 
            ```xml
            <context:property-placeholder location="classpath:book.properties"/>
            ```
        * 如果是在Java配置中，可以通过`@PropertySource`来引入配置: 
            ```java
            @Component
            @PropertySource("classpath:book.properties")
            public class Book {
                @Value("${book.id}")
                private Long id;
                @Value("${book.name}")
                private String name;
                @Value("${book.author}")
                private String author;
            }
            ```
            * 这样，当项目启动时，就会自动加载`book.properties`文件。
* 类型安全的属性注入
    * Spring Boot引入了类型安全的属性注入，如果采用Spring中的配置方式，当配置的属性非常多的时候，工作量就很大了，而且容易出错。使用类型安全的属性注入，可以有效的解决这个问题。
        ```java
        @Component
        @PropertySource("classpath:book.properties")
        @ConfigurationProperties(prefix = "book")
        public class Book {
            private Long id;
            private String name;
            private String author;
        }
        ```
    * 这里，主要是引入`@ConfigurationProperties(prefix = “book”)`注解，并且配置了属性的前缀，此时会自动将Spring容器中对应的数据注入到对象对应的属性中，就不用通过`@Value`注解挨个注入了，减少工作量并且避免出错。


### yaml
* 配置文件位置与配置文件名设置方式与properties一致
* 数组注入
    * yaml也支持数组注入
        ```yaml
        my:
            servers:
                - dev.example.com
                - another.example.com
        ```
        * 这段数据可以绑定到一个带Bean的数组中: 
        ```java
        @ConfigurationProperties(prefix="my")
        @Component
        public class Config {

            private List<String> servers = new ArrayList<String>();

            public List<String> getServers() {
                return this.servers;
            }
        }
        ```
    * 项目启动后，配置中的数组会自动存储到servers集合中。当然，yaml不仅可以存储这种简单数据，也可以在集合中存储对象。
        ```yaml
        redis:
            redisConfigs:
                - host: 192.168.66.128
                  port: 6379
                - host: 192.168.66.129
                  port: 6380
        ```
        * 这个可以被注入到如下类中: 
        ```java
        @Component
        @ConfigurationProperties(prefix = "redis")
        public class RedisCluster {
            private List<SingleRedisConfig> redisConfigs;
        }
        ```

## Spring Boot中的静态资源
* 当我们使用SpringMVC框架时，静态资源会被拦截，需要添加额外配置

### SSM中的配置
* 要讲Spring Boot中的问题，我们得先回到SSM环境搭建中，一般来说，我们可以通过`<mvc:resources />`节点来配置不拦截静态资源
    ```xml
    <mvc:resources mapping="/js/**" location="/js/"/>
    <mvc:resources mapping="/css/**" location="/css/"/>
    <mvc:resources mapping="/html/**" location="/html/"/>
    ```
* 这种配置是在XML中的配置，大家知道，SpringMVC的配置除了在XML中配置，也可以在Java代码中配置，如果在Java代码中配置的话，我们只需要自定义一个类，继承自`WebMvcConfigurationSupport`即可: 
    ```java
    @Configuration
    @ComponentScan(basePackages = "org.sang.javassm")
    public class SpringMVCConfig extends WebMvcConfigurationSupport {
        @Override
        protected void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("/");
        }
    }
    ```
    * 重写`WebMvcConfigurationSupport`类中的`addResourceHandlers`方法，在该方法中配置静态资源位置即可，这里的含义和上面xml配置的含义一致

### Spring Boot中的配置
* Spring Boot 初始化工具创建的项目，默认都会存在 resources/static 目录，很多小伙伴也知道静态资源只要放到这个目录下，就可以直接访问
* 整体规划
    * 在 Spring Boot中，默认情况下，一共有5个位置可以放静态资源，五个路径分别是如下5个: 
        * classpath: `/META-INF/resources/`
        * classpath: `/resources/`
        * classpath: `/static/`
        * classpath: `/public/`
        * `/`
    * 在 Spring Boot项目中，默认是没有webapp这个目录的，当然我们也可以自己添加，这里第5个`/`其实就是表示webapp目录中的静态资源也不被拦截。如果同一个文件分别出现在五个目录下，那么优先级也是按照上面列出的顺序。
    * 不过，虽然有5个存储目录，除了第5个用的比较少之外，其他四个，系统默认创建了`classpath:/static/`， 正常情况下，我们只需要将我们的静态资源放到这个目录下即可，也不需要额外去创建其他静态资源目录，例如我在`classpath:/static/`目录下放了一张名`1.png`的图片，那么我的访问路径是:
        ```
        http://localhost:8080/1.png 
        ```
        * 这里大家注意，请求地址中并不需要static，如果加上了static反而多此一举会报404错误。其实这个效果很好实现，例如在SSM配置中，我们的静态资源拦截配置如果是下面这样: 
        ```xml
        <mvc:resources mapping="/**" location="/static/"/>
        ```
* 自定义配置
    * 当然，这个是系统默认配置，如果我们并不想将资源放在系统默认的这五个位置上，也可以自定义静态资源位置和映射，自定义的方式也有两种，可以通过`application.properties`来定义，也可以在Java代码中来定义，下面分别来看。
        * application.properties在配置文件中定义的方式比较简单
            ```
            spring.resources.static-locations=classpath:/
            spring.mvc.static-path-pattern=/**
            ```
            * 第一行配置表示定义资源位置，第二行配置表示定义请求URL规则。
            * 以上文的配置为例，如果我们这样定义了，表示可以将静态资源放在resources目录下的任意地方，我们访问的时候当然也需要写完整的路径，例如在resources/static目录下有一张名为`1.png`的图片，那么访问路径就是`http://localhost:8080/static/1.png`,注意此时的static不能省略。
        * Java代码定义方式和Java配置的SSM比较类似
            ```java
            @Configuration
            public class WebMVCConfig implements WebMvcConfigurer {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry registry) {
                    registry.addResourceHandler("/**").addResourceLocations("classpath:/aaa/");
                }
            }
            ```

## Spring Boot中的自定义异常处理
* 在 Spring Boot项目中 ，异常统一处理，可以使用Spring中`@ControllerAdvice`来统一处理，也可以自己来定义异常处理方案。

### 静态异常页面
* 自定义静态异常页面，又分为两种，第一种是使用HTTP响应码来命名页面，例如404.html、405.html、500.html…，另一种就是直接定义一个4xx.html，表示400-499的状态都显示这个异常页面，5xx.html表示500-599的状态显示这个异常页面。
* 默认是在`classpath:/static/error/`路径下定义相关页面: 
    ```
    src
        |_main
               |_java
                      |_Application.java
               |_resources
                           |_static
                                    |_error
                                            |_404.html
                                            |_500.html
    ```
    * 此时，启动项目，如果项目抛出500请求错误，就会自动展示`500.html`这个页面，发生404就会展示`404.html`页面。如果异常展示页面既存在`5xx.htm`，也存在`500.html`，此时，发生500异常时，优先展示`500.html`页面。

### 动态异常页面
* 动态的异常页面定义方式和静态的基本一致，可以采用的页面模板有jsp、freemarker、thymeleaf。动态异常页面，也支持`404.html`或者`4xx.html`，但是一般来说，由于动态异常页面可以直接展示异常详细信息，所以就没有必要挨个枚举错误了，直接定义`4xx.html`或者`5xx.html`即可。
* 动态页面模板，不需要开发者自己去定义控制器，直接定义异常页面即可 ，Spring Boot中自带的异常处理器会自动查找到异常页面。
    ```
    src
        |_main
               |_java
                      |_Application.java
               |_resources
                           |_templates
                                       |_error
                                               |_5xx.html
    ```
    ```txt
    <!DOCTYPE html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>Title</title>
    </head>
    <body>
    <h1>5xx</h1>
    <table border="1">
        <tr>
            <td>path</td>
            <td th:text="${path}"></td>
        </tr>
        <tr>
            <td>error</td>
            <td th:text="${error}"></td>
        </tr>
        <tr>
            <td>message</td>
            <td th:text="${message}"></td>
        </tr>
        <tr>
            <td>timestamp</td>
            <td th:text="${timestamp}"></td>
        </tr>
        <tr>
            <td>status</td>
            <td th:text="${status}"></td>
        </tr>
    </table>
    </body>
    </html>
    ```
* 如果动态页面和静态页面同时定义了异常处理页面，例如`classpath:/static/error/404.html`和`classpath:/templates/error/404.html`同时存在时，默认使用动态页面。即完整的错误页面查找方式应该是这样: 
    * 发生了500错误 –> 查找动态`500.html` –> 查找静态`500.html` –> 查找动态`5xx.html` –> 查找静态`5xx.html`。

### 自定义异常数据
* 默认情况下，在Spring Boot中，所有的异常数据其实就是path、error、message、timestamp、status这 5条数据，这5条数据定义在`org.springframework.boot.web.reactive.error.DefaultErrorAttributes`类中，具体定义在`getErrorAttributes`方法中: 
    ```java
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
            Map<String, Object> errorAttributes = new LinkedHashMap<>();
            errorAttributes.put("timestamp", new Date());
            errorAttributes.put("path", request.path());
            Throwable error = getError(request);
            HttpStatus errorStatus = determineHttpStatus(error);
            errorAttributes.put("status", errorStatus.value());
            errorAttributes.put("error", errorStatus.getReasonPhrase());
            errorAttributes.put("message", determineMessage(error));
            handleException(errorAttributes, determineException(error), includeStackTrace);
            return errorAttributes;
    }
    ```
* `DefaultErrorAttributes`类本身则是在`org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration`异常自动配置类中定义的，如果开发者没有自己提供一个`ErrorAttributes`的实例的话，那么Spring Boot将自动提供一个`ErrorAttributes`的实例，也就是`DefaultErrorAttributes`。
* 基于此 ，开发者自定义 ErrorAttributes 有两种方式: 
    * 直接实现`ErrorAttributes`接口
    * 继承`DefaultErrorAttributes`(推荐)，因为`DefaultErrorAttributes`中对异常数据的处理已经完成，开发者可以直接使用。
* 具体定义如下:
    ```java
    @Component
    public class MyErrorAttributes extends DefaultErrorAttributes {
        @Override
        public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
            Map<String, Object> map = super.getErrorAttributes(webRequest, includeStackTrace);
            if ((Integer)map.get("status") == 500) {
                map.put("message", "服务器内部错误!");
            }
            return map;
        }
    }
    ```
* 定义好的`ErrorAttributes`一定要注册成一个Bean，这样Spring Boot就不会使用默认的`DefaultErrorAttributes`了。

### 自定义异常视图
* 异常视图默认就是前面所说的静态或者动态页面，这个也是可以自定义的，首先，默认的异常视图加载逻辑在`org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController`类的`errorHtml`方法中，这个方法用来返回异常页面+数据，还有另外一个error方法，这个方法用来返回异常数据(如果是ajax请求，则该方法会被触发)。
    ```java
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
            HttpStatus status = getStatus(request);
            Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
            response.setStatus(status.value());
            ModelAndView modelAndView = resolveErrorView(request, response, status, model);
            return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
    }
    ```
* 在该方法中，首先会通过`getErrorAttributes`方法去获取异常数据(实际上会调用到`ErrorAttributes`的实例的`getErrorAttributes`方法)，然后调用 `resolveErrorView`去创建一个`ModelAndView`，如果这里创建失败，那么用户将会看到默认的错误提示页面。
* 正常情况下，`resolveErrorView`方法会来到`DefaultErrorViewResolver`类的`resolveErrorView`方法中:
    ```java
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
            ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
            if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
                    modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
            }
            return modelAndView;
    }
    ```
    * 在这里，首先以异常响应码作为视图名分别去查找动态页面和静态页面，如果没有查找到，则再以4xx或者5xx作为视图名再去分别查找动态或者静态页面。
* 要自定义异常视图解析，也很容易 ，由于`DefaultErrorViewResolver`是在`ErrorMvcAutoConfiguration`类中提供的实例，即开发者没有提供相关实例时，会使用默认的`DefaultErrorViewResolver`，开发者提供了自己的`ErrorViewResolver`实例后，默认的配置就会失效，因此，自定义异常视图，只需要提供一个`ErrorViewResolver`的实例即可:
    ```java
    @Component
    public class MyErrorViewResolver extends DefaultErrorViewResolver {
        public MyErrorViewResolver(ApplicationContext applicationContext, ResourceProperties resourceProperties) {
            super(applicationContext, resourceProperties);
        }
        @Override
        public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
            return new ModelAndView("/aaa/123", model);
        }
    }
    ```
    * 实际上，开发者也可以在这里定义异常数据(直接在`resolveErrorView`方法重新定义一个model ，将参数中的model数据拷贝过去并修改，注意参数中的model类型为`UnmodifiableMap`，即不可以直接修改)，而不需要自定义`MyErrorAttributes`。