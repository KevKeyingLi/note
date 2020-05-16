- [Spring基础](#Spring基础)
    * [开发Web应用](#开发Web应用)
        * [展示信息](#展示信息)
        * [处理表单提交](#处理表单提交)
        * [校验输入表单](#校验输入表单)
        * [视图控制器](#视图控制器)
        * [模版库](#模版库)
    * [使用数据](#使用数据)
        * [JDBC](#JDBC)
        * [JPA](#JPA)
    * [使用Spring安全](#使用Spring安全)
        * [Spring安全配置](#Spring安全配置)
        * [Web请求安全](#Web请求安全)
        * [获取登陆用户](#获取登陆用户)
    * [使用配置属性](#使用配置属性)
        * [属性来源](#属性来源)
        * [创建自己的配置属性](#创建自己的配置属性)
        * [配置profile](#配置profile)
- [Spring集成](#Spring集成)
    * [创建REST服务](#创建REST服务)
        * [RESTful控制器](#RESTful控制器)
        * [启用超媒体](#启用超媒体)
        * [开启数据支撑服务](#开启数据支撑服务)
    * [消费REST服务](#消费REST服务)
        * [使用RestTemplate消费REST服务](#使用RestTemplate消费REST服务)
        * [使用Traverson导航REST服务](#使用Traverson导航REST服务)
    * [发送异步信息](#发送异步信息)
        * [使用JMS发送信息](#使用JMS发送信息)
        * [使用RabbitMQ和AMQP](#使用RabbitMQ和AMQP)
        * [Kafka消息](#Kafka消息)
    * [集成Spring](#集成Spring)
        * [声明一个简单的集成流](#声明一个简单的集成流)
        * [相信概念](#相信概念)
        * [创建一个email集成流](#创建一个email集成流)
- [Spring Reactive编程](#Spring-Reactive编程)
    * [Reactor介绍](#Reactor介绍)
        * [理解Reactive编程](#理解Reactive编程)
        * [Mono](#Mono)
        * [Flux](#Flux)
        * [Reactive操作](#Reactive操作)
    * [开发Reactive API](#开发Reactive-API)
        * [使用Spring WebFlux](#使用Spring-WebFlux)
        * [定义函数式请求处理器](#定义函数式请求处理器)
        * [测试Reactive控制器](#测试Reactive控制器)
        * [Reactive式的消费REST API](#Reactive式的消费REST-API)
        * [Reactive Web API安全](#Reactive-Web-API安全)
    * [Reactive式的持久化数据](#Reactive式的持久化数据)
        * [Reactive类型和非Reactive类型的转换](#Reactive类型和非Reactive类型的转换)
        * [编写Reactive式的MongoDB存储](#Reactive类型和非Reactive类型的转换)
- [Spring云原生](Spring云原生)
    * [发现服务](#发现服务)
        * [配置eureka服务注册器](#配置eureka服务注册器)
        * [注册和发现服务](#注册和发现服务)
    * [管理配置](#管理配置)
        * [配置服务器](#配置服务器)
        * [配置属性安全](#配置属性安全)
        * [动态的刷新配置](#动态的刷新配置)
    * [处理失败和延迟](#处理失败和延迟)
        * [熔断器模式](#熔断器模式)
        * [@HystrixCommand](#@HystrixCommand)
        * [失败监控](#失败监控)
        * [聚集Hystrix流](#聚集Hystrix流)
- [Spring部署](#Spring部署)
    * [使用Spring Boot Actuator](#使用Spring-Boot-Actuator)
        * [配置Acutator](#配置Acutator)
        * [消费Acutator Endpoint](#消费Acutator-Endpoint)
        * [自定义Acutator](#消费Acutator-Endpoint)
    * [管理Spring](#管理Spring)
        * [Spring Boot Admin](#Spring-Boot-Admin)
        * [使用](#使用)
        * [安全Spring Server](#安全Spring-Server)
    * [使用JMX监控Spring](#使用JMX监控Spring)
        * [使用Actuator MBeans](#使用Actuator-MBeans)
        * [创建自己的MBeans](#创建自己的MBeans)
        * [发送通知](#发送通知)
    * [部署Spring](#部署Spring)
        * [构建和部署WAR](#构建和部署WAR)
        * [部署到Cloud Foundry](#部署到Cloud-Foundry)
        * [使用Docker容器运行Spring Boot应用](#使用Docker容器运行Spring-Boot应用)

---

## Spring基础
### 开发Web应用
#### 展示信息
* 假如我们要做一个taco订餐网站，网站上需提供菜单给顾客浏览，同时提供顾客自选食材的功能，我们需要提供以下组件构建这个网站:
    * 代表食材的domain class
        ```java
        @Data
        @RequiredArgsConstructor
        public class Ingredient {
            private final String id;
            private final String name;
            private final Type type;
            public static enum Type {
                WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
            }
        }
        ```
    * 获取食材数据并将数据传递到视图的MVC controller
        ```java
        @Slf4j
        @Controller
        @RequestMapping("/design")
        public class DesignTacoController {
            @GetMapping
            public String showDesignForm(Model model) {
                List<Ingredient> ingredients = Arrays.asList(
                    new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
                    new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
                    new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
                    new Ingredient("CARN", "Carnitas", Type.PROTEIN),
                    new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
                    new Ingredient("LETC", "Lettuce", Type.VEGGIES),
                    new Ingredient("CHED", "Cheddar", Type.CHEESE),
                    new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
                    new Ingredient("SLSA", "Salsa", Type.SAUCE),
                    new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
                );
                Type[] types = Ingredient.Type.values();
                for (Type type : types) {
                    model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
                }
                model.addAttribute("design", new Taco());
                return "design";
            }
        }
        ```
        * Controller的主要作用是处理HTTP请求、将视图渲染成HTML或直接返回回复的信息(RESTful)
        * `@Controller`用于将类注释成控制器，Spring组件扫描的时候会将该类识别成控制器，并且自动在Spring application contex中生成`DesignTacoController`的实例作为bean
        * `@RequestMapping`定义控制器或方法处理何种请求，在此处则用于表明控制器只处理指向路径开头为/design的请求
        * `@GetMapping`指定`showDesignForm`只处理GET请求，在Spring4.3之前只能用`@RequestMapping(method=RequestMethod.GET)`代替
    * 列举食材的视图模版
        ```html
        <!DOCTYPE html>
        <html xmlns="http://www.w3.org/1999/xhtml"
            xmlns:th="http://www.thymeleaf.org">
            <head>
                <title>Taco Cloud</title>
                <link rel="stylesheet" th:href="@{/styles.css}" />
            </head>
            <body>
                <h1>Design your taco!</h1>
                <img th:src="@{/images/TacoCloud.png}"/>
                <form method="POST" th:object="${design}">
                    <div class="grid">
                        <div class="ingredient-group" id="wraps">
                            <h3>Designate your wrap:</h3>
                            <div th:each="ingredient : ${wrap}">
                                <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                            </div>
                        </div>
                        <div class="ingredient-group" id="proteins">
                            <h3>Pick your protein:</h3>
                            <div th:each="ingredient : ${protein}">
                                <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                            </div>
                        </div>
                        <div class="ingredient-group" id="cheeses">
                            <h3>Choose your cheese:</h3>
                            <div th:each="ingredient : ${cheese}">
                                <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                            </div>
                        </div>
                        <div class="ingredient-group" id="veggies">
                            <h3>Determine your veggies:</h3>
                            <div th:each="ingredient : ${veggies}">
                                <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                            </div>
                        </div>
                        <div class="ingredient-group" id="sauces">
                            <h3>Select your sauce:</h3>
                            <div th:each="ingredient : ${sauce}">
                                <input name="ingredients" type="checkbox" th:value="${ingredient.id}"/>
                                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
                            </div>
                        </div>
                    </div>
                    <div>
                        <h3>Name your taco creation:</h3>
                        <input type="text" th:field="*{name}"/>
                        <br/>
                        <button>Submit your taco</button>
                    </div>
                </form>
            </body>
        </html>
        ```

#### 处理表单提交
* 用HTTP的POST请求处理表单提交
* 处理提交的控制方法
    ```java
    @PostMapping
    public String processDesign(Design design) {
        // Save the taco design...
        // We'll do this in chapter 3
        log.info("Processing design: " + design);
        return "redirect:/orders/current";
    }
    ```
    * `@PostMapping`用于处理POST请求
    * 返回语句中的`redirect`表明执行跳转，这里跳转到/orders/current

#### 校验输入表单
* 从表单提交上来的值可能为空，可能为无效值，所以需要验证
* Spring MVC中的校验:
    * 在类中定义校验的规则
        ```java
        @Data
        public class Taco {
            @NotNull
            @Size(min=5, message="Name must be at least 5 characters long")
            private String name;
            
            @Size(min=1, message="You must choose at least 1 ingredient")
            private List<String> ingredients;
        }
        ```
        * `@NotNull`和`@Size`制定了校验规则
        ```java
        @Data
        public class Order {
            @NotBlank(message="Name is required")
            private String name;

            @NotBlank(message="Street is required")
            private String street;

            @NotBlank(message="City is required")
            private String city;

            @NotBlank(message="State is required")
            private String state;

            @NotBlank(message="Zip code is required")
            private String zip;

            @CreditCardNumber(message="Not a valid credit card number")
            private String ccNumber;

            @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
            message="Must be formatted MM/YY")
            private String ccExpiration;
            
            @Digits(integer=3, fraction=0, message="Invalid CVV")
            private String ccCVV;
        }
        ```
        * `@CreditCardNumber`要求数据满足信用卡号格式
        * `@Pattern`要求数据满足正则的限制
        * `@Digits`要求数据包含三个数字
    * 在控制器中执行校验
        ```java
        @PostMapping
        public String processDesign(@Valid Taco design, Errors errors) {
            if (errors.hasErrors()) {
                return "design";
            }
            // Save the taco design...
            // We'll do this in chapter 3
            log.info("Processing design: " + design);
            return "redirect:/orders/current";
        }
        ```
        * `@Valid`提示Spring在提交表单的时候进行校验
    * 在视图中显示校验错误
        ```html
        <label for="ccNumber">Credit Card #: </label>
        <input type="text" th:field="*{ccNumber}"/>
        <span class="validationError"
            th:if="${#fields.hasErrors('ccNumber')}"
            th:errors="*{ccNumber}">CC Num Error</span>
        ```

#### 视图控制器
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```
* `WebMvcConfigurer`定义了一些配置Spring MVC的方法

#### 模版库

### 使用数据
#### JDBC
* Spring JDBC基于`JdbcTemplate`类
* `JdbcTemplate`提供了进行数据库操作的手段
* Querying a database with `JdbcTemplate`
    ```java
    @Override
    public Ingredient findOne(String id) {
        return jdbc.queryForObject(
            "select id, name, type from Ingredient where id=?",
            this::mapRowToIngredient, 
            id);
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(
            rs.getString("id"),
            rs.getString("name"),
            Ingredient.Type.valueOf(rs.getString("type")));
    }
    ```
    * 比纯JDBC简短很多
    * 无需建立连接
    * 无需clean up
    * 无需处理异常
* 改造domain以适用于数据持久化
    * 每个domain都具有一个具有唯一性的属性，以便区分每一个object，例如:id
        ```java
        @Data
        public class Taco {
            private Long id;
            private Date createdAt;
            // ...
        }

        @Data
        public class Order {
            private Long id;
            private Date placedAt;
            // ...
        }
        ```
* 使用`JdbcTemplate`
    * 定义JDBC repositories
        ```java
        public interface IngredientRepository {
            Iterable<Ingredient> findAll();
            Ingredient findOne(String id);
            Ingredient save(Ingredient ingredient);
        }
        ```
    * 实现repositories
        ```java
        @Repository
        public class JdbcIngredientRepository implements IngredientRepository {

            private JdbcTemplate jdbc;

            @Autowired
            public JdbcIngredientRepository(JdbcTemplate jdbc) {
                this.jdbc = jdbc;
            }
            
            @Override
            public Iterable<Ingredient> findAll() {
                return jdbc.query("select id, name, type from Ingredient", this::mapRowToIngredient);
            }

            @Override
            public Ingredient findOne(String id) {
                return jdbc.queryForObject(
                    "select id, name, type from Ingredient where id=?", 
                    this::mapRowToIngredient, 
                    id);
            }

            @Override
            public Ingredient findOne(String id) {
                return jdbc.queryForObject(
                    "select id, name, type from Ingredient where id=?",
                    new RowMapper<Ingredient>() {
                        public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new Ingredient(
                                rs.getString("id"),
                                rs.getString("name"),
                                Ingredient.Type.valueOf(rs.getString("type")));
                        };
                    }, 
                    id);
            }

            @Override
            public Ingredient save(Ingredient ingredient) {
                jdbc.update(
                    "insert into Ingredient (id, name, type) values (?, ?, ?)",
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getType().toString());
                return ingredient;
            }

            private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
                return new Ingredient(
                    rs.getString("id"),
                    rs.getString("name"),
                    Ingredient.Type.valueOf(rs.getString("type")));
            }
        }
        ```
        * `@Repository`注释使Spring自动发现这个类，并在Spring application context中实例化这个类作为bean
        * `findAll()`JDBC的`query()`方法用来执行SQL命令，并用`RowMapper`把数据库的列和结果集的对象对应起来
        * `findOne()`使用的`queryForObject()`和`query()`类似，只是多要一个`id`
        * 第二个`findOne()`显式使用了`RowMapper`的实现，于第一个除此没差别
    * 在controller注入与使用repository
        ```java
        @Controller
        @RequestMapping("/design")
        @SessionAttributes("order")
        public class DesignTacoController {

            private final IngredientRepository ingredientRepo;

            @Autowired
            public DesignTacoController(IngredientRepository ingredientRepo) {
                this.ingredientRepo = ingredientRepo;
            }

            @GetMapping
            public String showDesignForm(Model model) {
                List<Ingredient> ingredients = new ArrayList<>();
                ingredientRepo.findAll().forEach(i -> ingredients.add(i));
                Type[] types = Ingredient.Type.values();
                for (Type type : types) {
                    model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
                }
                return "design";
            }
            // ...
        }
        ```
* 定义schame和预加载数据
    * ```Taco_Order (1)---(*) Taco_Order_Tacos (*)---(1) Taco (1)---(*) Taco_Ingredients (*)---(1) Ingredient```
        * `Ingredient` holds ingredient information
        * `Taco` holds essential information about a taco design
        * `Taco_Ingredients` contains one or more rows for each row in Taco, mapping the taco to the ingredients for that taco
        * `Taco_Order` holds essential order details
        * `Taco_Order_Tacos` Contains one or more rows for each row in Taco_Order, mapping the order to the tacos in the order
    * 定义schema
        ```sql
        create table if not exists Ingredient (
            id varchar(4) not null,
            name varchar(25) not null,
            type varchar(10) not null
        );

        create table if not exists Taco (
            id identity,
            name varchar(50) not null,
            createdAt timestamp not null
        );

        create table if not exists Taco_Ingredients (
        taco bigint not null,
        ingredient varchar(4) not null
        );

        alter table Taco_Ingredients add foreign key (taco) references Taco(id);
        alter table Taco_Ingredients add foreign key (ingredient) references Ingredient(id);

        create table if not exists Taco_Order (
            id identity,
            deliveryName varchar(50) not null,
            deliveryStreet varchar(50) not null,
            deliveryCity varchar(50) not null,
            deliveryState varchar(2) not null,
            deliveryZip varchar(10) not null,
            ccNumber varchar(16) not null,
            ccExpiration varchar(5) not null,
            ccCVV varchar(3) not null,
            placedAt timestamp not null
        );

        create table if not exists Taco_Order_Tacos (
            tacoOrder bigint not null,
            taco bigint not null
        );

        alter table Taco_Order_Tacos add foreign key (tacoOrder) references Taco_Order(id);
        alter table Taco_Order_Tacos add foreign key (taco) references Taco(id);
        ```
        * 将这个schema存在sql文件中，并将这个sql文件放在resources文件夹中，在应用启动是会自动加载到系统中
    * 预加载数据
        ```sql
        delete from Taco_Order_Tacos;
        delete from Taco_Ingredients;
        delete from Taco;
        delete from Taco_Order;
        delete from Ingredient;
        insert into Ingredient (id, name, type) values ('FLTO', 'Flour Tortilla', 'WRAP');
        insert into Ingredient (id, name, type) values ('COTO', 'Corn Tortilla', 'WRAP');
        insert into Ingredient (id, name, type) values ('GRBF', 'Ground Beef', 'PROTEIN');
        insert into Ingredient (id, name, type) values ('CARN', 'Carnitas', 'PROTEIN');
        insert into Ingredient (id, name, type) values ('TMTO', 'Diced Tomatoes', 'VEGGIES');
        insert into Ingredient (id, name, type) values ('LETC', 'Lettuce', 'VEGGIES');
        insert into Ingredient (id, name, type) values ('CHED', 'Cheddar', 'CHEESE');
        insert into Ingredient (id, name, type) values ('JACK', 'Monterrey Jack', 'CHEESE');
        insert into Ingredient (id, name, type) values ('SLSA', 'Salsa', 'SAUCE');
        insert into Ingredient (id, name, type) values ('SRCR', 'Sour Cream', 'SAUCE');
        ```
    * 将这个schema存在sql文件中，并将这个sql文件放在resources文件夹中，在应用启动是会自动加载到系统中
* 插入数据
```java
import tacos.Taco;
public interface TacoRepository {
    Taco save(Taco design);
}
```
* 实现`TacoRepository`
    ```java
    @Repository
    public class JdbcTacoRepository implements TacoRepository {
        private JdbcTemplate jdbc;
        public JdbcTacoRepository(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        @Override
        public Taco save(Taco taco) {
            long tacoId = saveTacoInfo(taco);
            taco.setId(tacoId);
            for (Ingredient ingredient : taco.getIngredients()) {
                saveIngredientToTaco(ingredient, tacoId);
            }
            return taco;
        }

        private long saveTacoInfo(Taco taco) {
            taco.setCreatedAt(new Date());
            PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
                "insert into Taco (name, createdAt) values (?, ?)",
                Types.VARCHAR, 
                Types.TIMESTAMP
            ).newPreparedStatementCreator(
                Arrays.asList(
                    taco.getName(),
                    new Timestamp(taco.getCreatedAt().getTime())));
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(psc, keyHolder);
            return keyHolder.getKey().longValue();
        }

        private void saveIngredientToTaco(
            Ingredient ingredient, long tacoId) {
            jdbc.update(
                "insert into Taco_Ingredients (taco, ingredient) " +
                "values (?, ?)",
                tacoId, ingredient.getId());
        }
    }
    ```
* 使用`TacoRepository`
    ```java
    @Controller
    @RequestMapping("/design")
    @SessionAttributes("order")
    public class DesignTacoController {
        private final IngredientRepository ingredientRepo;
        private TacoRepository designRepo;

        @Autowired
        public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository designRepo) {
            this.ingredientRepo = ingredientRepo;
            this.designRepo = designRepo;
        }

        @ModelAttribute(name = "order")
        public Order order() {
            return new Order();
        }

        @ModelAttribute(name = "taco")
        public Taco taco() {
            return new Taco();
        }

        @PostMapping
        public String processDesign( @Valid Taco design, Errors errors, @ModelAttribute Order order) {
            if (errors.hasErrors()) {
                return "design";
            }
            Taco saved = designRepo.save(design);
            order.addDesign(saved);
            return "redirect:/orders/current";
        }
    }
    ```
    * 函数名前`@ModelAttribute`确保对象会被创造在model中
    * 函数变量中的`@ModelAttribute`确保参数来自于model
    * `@SessionAttributes`确保这个控制器中任何对象都应该保留在其所对应的session中
* 使用`SimpleJdbcInsert`进行插入
    * 如果添加一个实例需要插入多个数据库表，用`SimpleJdbcInsert`比较方便
        ```java
        @Repository
        public class JdbcOrderRepository implements OrderRepository {
            private SimpleJdbcInsert orderInserter;
            private SimpleJdbcInsert orderTacoInserter;
            private ObjectMapper objectMapper;

            @Autowired
            public JdbcOrderRepository(JdbcTemplate jdbc) {
                this.orderInserter = new SimpleJdbcInsert(jdbc)
                    .withTableName("Taco_Order")
                    .usingGeneratedKeyColumns("id");
                this.orderTacoInserter = new SimpleJdbcInsert(jdbc)
                    .withTableName("Taco_Order_Tacos");
                this.objectMapper = new ObjectMapper();
            }
            
            @Override
            public Order save(Order order) {
                order.setPlacedAt(new Date());
                long orderId = saveOrderDetails(order);
                order.setId(orderId);
                List<Taco> tacos = order.getTacos();
                for (Taco taco : tacos) {
                    saveTacoToOrder(taco, orderId);
                }
                return order;
            }

            private long saveOrderDetails(Order order) {
                @SuppressWarnings("unchecked")
                Map<String, Object> values = objectMapper.convertValue(order, Map.class);
                values.put("placedAt", order.getPlacedAt());
                long orderId = orderInserter
                    .executeAndReturnKey(values)
                    .longValue();
                return orderId;
            }

            private void saveTacoToOrder(Taco taco, long orderId) {
                Map<String, Object> values = new HashMap<>();
                values.put("tacoOrder", orderId);
                values.put("taco", taco.getId());
                orderTacoInserter.execute(values);
            }
        }
        ```
        * `KeyHolder`
        * `PreparedStatementCreator`
        ```java
        @Controller
        @RequestMapping("/orders")
        @SessionAttributes("order")
        public class OrderController {
            private OrderRepository orderRepo;
            public OrderController(OrderRepository orderRepo) {
                this.orderRepo = orderRepo;
            }

            @GetMapping("/current")
            public String orderForm() {
                return "orderForm";
            }

            @PostMapping
            public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus) {
                if (errors.hasErrors()) {
                    return "orderForm";
                }
                orderRepo.save(order);
                sessionStatus.setComplete();
                return "redirect:/";
            }
        }
        ```
        * `setComplete()`用于重置session


#### JPA
* 用`@Entity`将domain注释成entity
    ```java
    @Data
    @RequiredArgsConstructor
    @NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
    @Entity
    public class Ingredient {
        @Id
        private final String id;
        private final String name;
        private final Type type;
        public static enum Type {
            WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
        }
    }
    ```
    * `@Id`用于标记在数据库中元素作为身份识别的属性
    ```java
    @Data
    @Entity
    public class Taco {
        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;

        @NotNull
        @Size(min=5, message="Name must be at least 5 characters long")
        private String name;

        private Date createdAt;

        @ManyToMany(targetEntity=Ingredient.class)
        @Size(min=1, message="You must choose at least 1 ingredient")
        private List<Ingredient> ingredients;

        @PrePersist
        void createdAt() {
            this.createdAt = new Date();
        }
    }
    ```
    * `@GeneratedValue`启用自己生成id
    * `@ManyToMany`指明了不同元素之间的多对多关系，在这里是一个taco可以有多个ingredient，一种ingredient可以用于多个taco
    * `@PrePersist`在这里用于将`createdAt`在taco被持续化之前设定为现在的时间
    ```java
    @Data
    @Entity
    @Table(name="Taco_Order")
    public class Order implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Date placedAt;
        
        @ManyToMany(targetEntity=Taco.class)
        private List<Taco> tacos = new ArrayList<>();
        public void addDesign(Taco design) {
            this.tacos.add(design);
        }
        
        @PrePersist
        void placedAt() {
            this.placedAt = new Date();
        }
    }
    ```
    * `@Table`用于指明存储用的表
* 声明JPA repositories
    * 通过继承`CrudRepository`声明
        ```java
        public interface IngredientRepository
            extends CrudRepository<Ingredient, String> {
        }
        ```
        * `CrudRepository`提供了CRUD需要的方法
        * 第一个参数是要储存的数据类型
        ```java
        public interface OrderRepository
            extends CrudRepository<Order, Long> {
        }
        ```
    * JPA不需要提供实现，spring会在启动的时候自动生成实现的方法
* 使用JPA repositories
    * Spring Data会解析repository中的方法名，根据方法名来决定实际是什么操作
        * 例如`findByDeliveryZip()`可以分解为`find`、`by`和`DeliveryZip`
        * 例如`List<Order> readOrdersByDeliveryZipAndPlacedAtBetween(String deliveryZip, Date startDate, Date endDate)`可以分解为`read`、`by`、`DeliveryZip`、`and`、`PlacedAt`和`between`；即使名字改成`readPuppiesBy...`也不会改变实际功能；方法名中的DeliveryZip和PlacedAt必须和参数中的一致
    * Spring Data提供以下关键字
        * IsAfter, After, IsGreaterThan, GreaterThan
        * IsGreaterThanEqual, GreaterThanEqual
        * IsBefore, Before, IsLessThan, LessThan
        * IsLessThanEqual, LessThanEqual
        * IsBetween, Between
        * IsNull, Null
        * IsNotNull, NotNull
        * IsIn, In
        * IsNotIn, NotIn
        * IsStartingWith, StartingWith, StartsWith
        * IsEndingWith, EndingWith, EndsWith
        * IsContaining, Containing, Contains
        * IsLike, Like
        * IsNotLike, NotLike
        * IsTrue, True
        * IsFalse, False
        * Is, Equals
        * IsNot, Not
        * IgnoringCase, IgnoresCase
    * 在Spring Data命名法则覆盖不到的方法，使用`@Query`来指明使用的SQL操作
        ```java
        @Query("Order o where o.deliveryCity='Seattle'")
 

### 使用Spring安全
* 只要在项目中添加security的starter就可以获得以下安全属性
    * 所有HTTP请求都要进行验证
    * 不需要提供具体的验证方法
    * 无登入页面
    * 验证由基本的HTTP验证实现
    * 只有一个名为`user`的唯一用户
* 如果需要更多的安全属性，需要进一步的配置

#### Spring安全配置
* 启用网络安全
    ```java
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {}
    ```
    * 在启用后，访问时就需要登陆了
* Spring提供几个储存用户的选择，通过重写`WebSecurityConfigurerAdapter`中的`configure()`来配置
    * An in-memory user store
        ```java
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser("buzz")
                .password("infinity")
                .authorities("ROLE_USER")
                .and()
                .withUser("woody")
                .password("bullseye")
                .authorities("ROLE_USER");
        }
        ```
        * `inMemoryAuthentication()`提供了设置用户信息的方法
        * 把用户信息储存在内存适合用于测试，但是不适合用于实际应用，应为对用户的改动不会被保存
    * A JDBC-based user store
        * 将用户数据存在关系型数据库
            ```java
            @Autowired
            DataSource dataSource;

            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.jdbcAuthentication()
                    .dataSource(dataSource);
            }
            ```
        * 重写默认的user queries
            * Spring原有query
                ```java
                public static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,enabled " + "from users " + "where username = ?";
                public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY = "select username,authority " + "from authorities " + "where username = ?";
                public static final String DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY = "select g.id, g.group_name, ga.authority " + "from groups g, group_members gm, group_authorities ga " + "where gm.username = ? " + "and g.id = ga.group_id " + "and g.id = gm.group_id";
                ```
            * 自定义query
                ```java
                @Override
                protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                    auth.jdbcAuthentication()
                        .dataSource(dataSource)
                        .usersByUsernameQuery("select username, password, enabled from Users " + "where username=?")
                        .authoritiesByUsernameQuery("select username, authority from UserAuthorities " + "where username=?");
                        .passwordEncoder(new StandardPasswordEncoder("53cr3t");
                }
                ```
                * `passwordEncoder()`接受任何`PasswordEncoder`接口的实现，用于给密码加密， Spring提供以下几种实现
                    * `BCryptPasswordEncoder`:bcrypt强哈希加密
                    * `NoOpPasswordEncoder`:无加密
                    * `Pbkdf2PasswordEncoder`:PBKDF2加密
                    * `SCryptPasswordEncoder`:scrypt哈希加密
                    * `StandardPasswordEncoder`:SHA-256哈希加密
                    * 如自定义加密器，需实现
                        ```java
                        public interface PasswordEncoder {
                            String encode(CharSequence rawPassword);
                            boolean matches(CharSequence rawPassword, String encodedPassword);
                        }
                        ```
                * 无论用什么加密器，密码在数据库中从不解密
    * An LDAP-backed user store(LDAP (LightweightDirectory Access Protocol)
        ```java
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.ldapAuthentication()
                .userSearchBase("ou=people")
                .userSearchFilter("(uid={0})")
                .groupSearchBase("ou=groups")
                .groupSearchFilter("member={0}")
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("passcode")
                .contextSource()
                    //.url("ldap://tacocloud.com:389/dc=tacocloud,dc=com");
                    .root("dc=tacocloud,dc=com")
                    .ldif("classpath:users.ldif");
        }
        ```
        * `userSearchFilter()`和`groupSearchFilter()`用于提供查找的filter，默认情况是基于root开始查找
        * `userSearchBase()`和`groupSearchBase()`用于修改查找的基准点
        * `passwordCompare()`用于声明用密码比较的方式来进行验证，输入的密码会于LDAP中的`userPassword`进行比较
        * `contextSource()`
            * `url()`用于表明LDAP服务器所在，默认情况会聆听`port 33389`
            * `root()`用于指明一个已经激活的server的root
            * 当LDAP服务器启动是会load所需的LDIF文件，`ldif()`用于指明文件路径
    * A custom user details service
        * 定义user entity
            ```java
            @Entity
            @Data
            @NoArgsConstructor(access=AccessLevel.PRIVATE, force=true)
            @RequiredArgsConstructor
            public class User implements UserDetails {
                private static final long serialVersionUID = 1L;
                @Id
                @GeneratedValue(strategy=GenerationType.AUTO)
                private Long id;
                private final String username;
                private final String password;
                private final String fullname;
                private final String street;
                private final String city;
                private final String state;
                private final String zip;
                private final String phoneNumber;

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return true;
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return true;
                }
                
                @Override
                public boolean isEnabled() {
                    return true;
                }
            }
            ```
            * 实现了`UserDetails`，这给spring提供了信息
            * `getAuthorities()`用于返回已授权的用户
        * 定义user repository
            ```java
            public interface UserRepository extends CrudRepository<User, Long> {
                User findByUsername(String username);
            }
            ```
        * 定义userDetail service
            ```java
            public interface UserDetailsService {
                UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
            }
            ```
        * 实现userDetail service
            ```java
            @Service
            public class UserRepositoryUserDetailsService implements UserDetailsService {
                private UserRepository userRepo;
                @Autowired
                public UserRepositoryUserDetailsService(UserRepository userRepo) {
                    this.userRepo = userRepo;
                }

                @Override
                public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                    User user = userRepo.findByUsername(username);
                    if (user != null) {
                        return user;
                    }
                    throw new UsernameNotFoundException("User '" + username + "' not found");
                }
            }
            ```
            * `loadByUsername()`不能返回null
        * 配置安全属性
            ```java
            @Bean
            public PasswordEncoder encoder() {
                return new StandardPasswordEncoder("53cr3t");
            }

            @Autowired
            private UserDetailsService userDetailsService;

            @Override
            protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService)
                    .passwordEncoder(encoder());
            }
            ```
            * `encoder()`声明加密方法
        * 定义控制器
            ```java
            @Controller
            @RequestMapping("/register")
            public class RegistrationController {
                private UserRepository userRepo;
                private PasswordEncoder passwordEncoder;
                public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
                    this.userRepo = userRepo;
                    this.passwordEncoder = passwordEncoder;
                }

                @GetMapping
                public String registerForm() {
                    return "registration";
                }

                @PostMapping
                public String processRegistration(RegistrationForm form) {
                    userRepo.save(form.toUser(passwordEncoder));
                    return "redirect:/login";
                }
            }
            ```
            ```java
            @Data
            public class RegistrationForm {
                private String username;
                private String password;
                private String fullname;
                private String street;
                private String city;
                private String state;
                private String zip;
                private String phone;
                public User toUser(PasswordEncoder passwordEncoder) {
                    return new User(username, passwordEncoder.encode(password), fullname, street, city, state, zip, phone);
                }
            }
            ```

#### Web请求安全
* `WebSecurityConfigurerAdapter`中的`configure()`
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {}
    ```
    * 其中的`HttpSecurity`用于处理在网络层面的安全事宜

##### 请求安全
* 配置请求访问权限
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/design", "/orders").hasRole("ROLE_USER")
            .antMatchers(“/”, "/**").permitAll();
    }
    ```
    * `/design`和`/orders`只对登陆后的用户开发
    * 其他路径所有人都可访问
    * 安全规则的先后顺序会影响路径的accessiblity
* 用于限定accessibility的方法:
    * `access(String)` Allows access if the given SpEL expression evaluates to true
    * `anonymous()` Allows access to anonymous users
    * `authenticated()` Allows access to authenticated users
    * `denyAll()` Denies access unconditionally
    * `fullyAuthenticated()` Allows access if the user is fully authenticated (not remembered)
    * `hasAnyAuthority(String…)` Allows access if the user has any of the given authorities
    * `hasAnyRole(String…)` Allows access if the user has any of the given roles
    * `hasAuthority(String)` Allows access if the user has the given authority
    * `hasIpAddress(String)` Allows access if the request comes from the given IP address
    * `hasRole(String)` Allows access if the user has the given role
    * `not()` Negates the effect of any of the other access methods
    * `permitAll()` Allows access unconditionally
    * `rememberMe()` Allows access for users who are authenticated via remember-me
* SpEL(Spring Expression Language)
    * `authentication` The user’s authentication object
    * `denyAll` Always evaluates to false
    * `hasAnyRole(list of roles)` true if the user has any of the given roles
    * `hasRole(role)` true if the user has the given role
    * `hasIpAddress(IP address)` true if the request comes from the given IP address
    * `isAnonymous()` true if the user is anonymous
    * `isAuthenticated()` true if the user is authenticated
    * `isFullyAuthenticated()` true if the user is fully authenticated (not authenticated with remember-me)
    * `isRememberMe()` true if the user was authenticated via remember-me
    * `permitAll` Always evaluates to true
    * `principal` The user’s principal object
* `access(String)`例子
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/design", "/orders").access("hasRole('ROLE_USER') && " +
                                                      "T(java.util.Calendar).getInstance().get("+
                                                      "T(java.util.Calendar).DAY_OF_WEEK) == " +
                                                      "T(java.util.Calendar).TUESDAY")
            .antMatchers(“/”, "/**").access("permitAll");
    }
    ```

##### 自定义登陆页面
* 用自定义的登陆页面替换Spring自带的登陆页面
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/design", "/orders").access("hasRole('ROLE_USER')")
                .antMatchers(“/”, "/**").access("permitAll")
            .and()
                .formLogin().loginPage("/login");
    }
    ```
    * `and()`表示已经完成验证的配置
    * `formLogin()`自定义登陆页面的开头
    * `loginPage()`用于标明登陆页面名
* 定义登陆用控制器
    ```java
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/login");
    }
    ```
    * 只涉及到一个简单的视图，所以在`WebConfig`的`addViewControllers()`添加就行了

##### 推出登陆
* 在`configure()`配置等出
    ```java
    .and().logout().logoutSuccessUrl("/");
    ```

##### CSRF(Cross-site request forgery)

#### 获取登陆用户
* 如果我们能知道谁执行了操作，会更好，例如谁下单了taco
    ```java
    @Data
    @Entity
    @Table(name="Taco_Order")
    public class Order implements Serializable {
        // ...
    @ManyToOne
    private User user;
        // ...
    }
    ```
    * `@ManyToOne`指明了多对一的关系，这里是一个用户有多个订单
* 在用户下单的时候，要先查看用户是谁，有以下几种方法:
    * Inject a `Principal` object into the controller method
        ```java
        @PostMapping
        public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Principal principal) {
            // ...
            User user = userRepository.findByUsername(principal.getName());
            order.setUser(user);
            // ...
        }
        ```
    * Inject an `Authentication` object into the controller method
        ```java
        @PostMapping
        public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, Authentication authentication) {
            // ...
            User user = (User) authentication.getPrincipal();
            order.setUser(user);
            // ...
        }
        ```
    * Use `SecurityContextHolder` to get at the security context
    * Use an `@AuthenticationPrincipal` annotated method
        ```java
        @PostMapping
        public String processOrder(@Valid Order order, Errors errors, SessionStatus sessionStatus, @AuthenticationPrincipal User user) {
            if (errors.hasErrors()) {
                return "orderForm";
            }
            order.setUser(user);
            orderRepo.save(order);
            sessionStatus.setComplete();
            return "redirect:/";
        }
        ```

### 使用配置属性
#### 属性来源
* 两种配置方式
    * Bean wiring
        ```java
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDataSourceBuilder()
                .setType(H2)
                .addScript("taco_schema.sql")
                .addScripts("user_data.sql", "ingredient_data.sql")
                .build();
        }
        ```
    * Property injection
* Spring从以下地方获取属性:
    * JVM system properties
    * Operating system environment variables
        ```
        export SERVER_PORT=9090
        ```
    * Command-line arguments
        ```
        java -jar tacocloud-0.0.5-SNAPSHOT.jar --server.port=9090
        ```
    * Application property configuration files
        * 配置文件:`src/main/resources/application.yml`
            ```yml
            server:
                port: 9090
            ```
* 配置数据源
    * `application.yml`
        ```yml
        spring:
            datasource:
                url: jdbc:mysql://localhost/tacocloud
                username: tacodb
                password: tacopassword
                driver-class-name: com.mysql.jdbc.Driver
        ```
* 配置logging
    * `application.yml`
        ```yml
        logging:
            path: /var/logs/
            file: TacoCloud.log
            level:
                root: WARN
                org:
                    springframework:
                        security: DEBUG
        ```
* 使用属性的值
    * 通过占位符`${}`进行引用
        ```yml
        greeting:
            welcome: ${spring.application.name}
        ```


#### 创建自己的配置属性
* Spring提供`@ConfigurationProperties`用于支持配置属性的注入
    ```java
    @Controller
    @RequestMapping("/orders")
    @SessionAttributes("order")
    @ConfigurationProperties(prefix="taco.orders")
    public class OrderController {
        private int pageSize = 20;
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        //...
        @GetMapping
        public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
            Pageable pageable = PageRequest.of(0, pageSize);
            model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
            return "orderList";
        }
    }
    ```
    * `@ConfigurationProperties`告诉Spring能去哪里读属性
    * `Pageable`用于一页一页的返回结果，这里需要设置每页有多少项，由`pageSize`决定
* Defining configuration properties holders
    * `application.yml`
        ```yml
        taco:
            orders:
                pageSize: 10
        ```
    * Extracting `pageSize` to a holder class
        ```java
        @Component
        @ConfigurationProperties(prefix="taco.orders")
        @Data
        @Validated
        public class OrderProps {
            @Min(value=5, message="must be between 5 and 25")
            @Max(value=25, message="must be between 5 and 25")
            private int pageSize = 20;
        }
        ```
        * 因为标记了`@Component`，所以他会被Spring发现，并做成bean
        * `pageSize`的默认值为20，然后从属性文件中读取
    * 注入抽取出来的属性
        ```java
        @Controller
        @RequestMapping("/orders")
        @SessionAttributes("order")
        public class OrderController {
            private OrderRepository orderRepo;
            private OrderProps props;
            public OrderController(OrderRepository orderRepo, OrderProps props) {
                this.orderRepo = orderRepo;
                this.props = props;
            }

            @GetMapping
            public String ordersForUser(@AuthenticationPrincipal User user, Model model) {
                Pageable pageable = PageRequest.of(0, props.getPageSize());
                model.addAttribute("orders", orderRepo.findByUserOrderByPlacedAtDesc(user, pageable));
                return "orderList";
            }
        }
        ```

#### 配置profile
* 不同的运行环境下，项目的配置会有所不同，用不同的profile配置在不同运行环境下的属性，比较方便

##### 定义profile-specific的属性
* 用YAML或properties文件存属性，命名符合`application-{profile name}.yml`和`application-{profile name}.properties`的习惯
    * `application-prod.yml`
        ```yml
        spring:
            datasource:
                url: jdbc:mysql://localhost/tacocloud
                username: tacouser
                password: tacopassword
        logging:
            level:
            tacos: WARN
        ```
* 也可以把profile信息存在`application.yml`，用三个hyphen将profile配置和其他配置分隔开
    ```yml
    logging:
        level:
            tacos: DEBUG
    ---
    spring:
        profiles: prod
        datasource:
            url: jdbc:mysql://localhost/tacocloud
            username: tacouser
            password: tacopassword
    logging:
        level:
            tacos: WARN
    ```
    * 当`prod`被激活时，prod-profile的配置会被使用

##### 激活profiles
* profile-specific的属性在对应profile被激活时才会被使用
* 可以在`application.yml`中标明被激活的profile
    ```yml
    spring:
        profiles:
            active:
                - prod
    ```
    * 但不是一个激活profile的好方式
* 可以在运行环境中的环境变量中设定被激活的profile
    ```shell
    % export SPRING_PROFILES_ACTIVE=prod
    ```
* 用命令行激活
    ```
    java -jar taco-cloud.jar --spring.profiles.active=prod
    ```
* 可以激活多个profile
    ```yml
    spring:
        profiles:
            active:
                - prod
                - audit
                - ha
    ```

##### 根据激活的profile创建bean
* 用`@Profile`来限定在什么profile被激活的时候才可以创建被注释的bean
    ```java
    @Bean
    @Profile("dev")
    public CommandLineRunner dataLoader(IngredientRepository repo, UserRepository userRepo, PasswordEncoder encoder) {}
    ```
    * `CommandLineRunner`用于在程序启东市自动加载数据
    * 这个bean在`dev`被激活的时候才会被创建
* `@Profile`可以接受多个profile
    ```java
    @Bean
    @Profile("dev")
    public CommandLineRunner dataLoader(IngredientRepository repo, UserRepository userRepo, PasswordEncoder encoder) {}
    ```
* 可以在某个profile不激活的时候才创建
    ```java
    @Bean
    @Profile("!prod")
    public CommandLineRunner dataLoader(IngredientRepository repo, UserRepository userRepo, PasswordEncoder encoder) {}
    ```
* 可以注释整个配置类
    ```java
    @Profile({"!prod", "!qa"})
    @Configuration
    public class DevelopmentConfig {
        @Bean
        public CommandLineRunner dataLoader(IngredientRepository repo, UserRepository userRepo, PasswordEncoder encoder) {}
    }
    ```

## Spring集成
### 创建REST服务
#### RESTful控制器
* Spring MVC提供处理HTTP请求的注释
    * `@GetMapping` HTTP GET requests
    * `@PostMapping` HTTP POST requests
    * `@PutMapping` HTTP PUT requests
    * `@PatchMapping` HTTP PATCH requests
    * `@DeleteMapping` HTTP DELETE requests
    * `@RequestMapping` General purpose request handling; HTTP method specified in the method attribute

##### 从服务器获取数据
* 是用angular获取数据
    ```js
    import { Component, OnInit, Injectable } from '@angular/core';
    import { Http } from '@angular/http';
    import { HttpClient } from '@angular/common/http';

    @Component({
        selector: 'recent-tacos',
        templateUrl: 'recents.component.html',
        styleUrls: ['./recents.component.css']
    })

    @Injectable()
    export class RecentTacosComponent implements OnInit {
        recentTacos: any;
        constructor(private httpClient: HttpClient) { }
        ngOnInit() {
            this.httpClient.get('http://localhost:8080/design/recent').subscribe(data => this.recentTacos = data);
        }
    }
    ```
* RESTful controller
    ```java
    @RestController
    @RequestMapping(path="/design", produces="application/json")
    @CrossOrigin(origins="*")
    public class DesignTacoController {
        private TacoRepository tacoRepo;
        @Autowired
        EntityLinks entityLinks;
        public DesignTacoController(TacoRepository tacoRepo) {
            this.tacoRepo = tacoRepo;
        }

        @GetMapping("/recent")
        public Iterable<Taco> recentTacos() {
            PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
            return tacoRepo.findAll(page).getContent();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
            Optional<Taco> optTaco = tacoRepo.findById(id);
            if (optTaco.isPresent()) {
                return new ResponseEntity<>(optTaco.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    ```
    * `@RestController`一是让Spring发现，二是让控制器只返回值
    * `@RequestMapping(path="/design", produces="application/json")`限定了只返回JSON格式个数据
    * 由于angular前端和服务器不在同一个host，浏览器会禁止前端从服务器获取数据，但是用类`@CrossOrigin(origins="*")`注释就可以
    * `PageRequest`用于分页获取数据，根据创建时间降排序，每页12个，返回第0页；其实`PageRequest`调用了`TacoRepository`的`findAll()`
    * taco的id未必存在，用`Optional<Taco>`合适
    * `ResponseEntity<Taco>`除了返回数据还会返回状态码

##### 发送数据到服务器
* 是用angular提交数据
    ```js
    onSubmit() {
        this.httpClient
            .post(
                'http://localhost:8080/design', 
                this.model, 
                {
                    headers: new HttpHeaders().set('Content-type', 'application/json'),
                })
            .subscribe(taco => this.cart.addToCart(taco));
        this.router.navigate(['/cart']);
    }
    ```
* RESTful controller
    ```java
    @PostMapping(consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Taco postTaco(@RequestBody Taco taco) {
        return tacoRepo.save(taco);
    }
    ```
    * `consumes`限定了输入数据的格式，这里的要求是json
    * `@RequestBody`表示被注释的变量从request body中转换而来
    * `@ResponseStatus`用于返回状态码

##### 更新服务器的数据
* 用HTTP的PUT来更新数据
    * 用PUT更新数据的话是更新整个实例
        ```java
        @PutMapping("/{orderId}")
        public Order putOrder(@RequestBody Order order) {
            return repo.save(order);
        }
        ```
* 用HTTP的PATCH来更新数据
    * 用PATCH更新数据的话是更新实例需要修改的部分
        ```java
        @PatchMapping(path="/{orderId}", consumes="application/json")
        public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody Order patch) {
            Order order = repo.findById(orderId).get();
            if (patch.getDeliveryName() != null) { order.setDeliveryName(patch.getDeliveryName()); }
            if (patch.getDeliveryStreet() != null) { order.setDeliveryStreet(patch.getDeliveryStreet()); }
            if (patch.getDeliveryCity() != null) { order.setDeliveryCity(patch.getDeliveryCity()); }
            if (patch.getDeliveryState() != null) { order.setDeliveryState(patch.getDeliveryState()); }
            if (patch.getDeliveryZip() != null) { order.setDeliveryZip(patch.getDeliveryState()); }
            if (patch.getCcNumber() != null) { order.setCcNumber(patch.getCcNumber()); }
            if (patch.getCcExpiration() != null) { order.setCcExpiration(patch.getCcExpiration()); }
            if (patch.getCcCVV() != null) { order.setCcCVV(patch.getCcCVV()); }
            return repo.save(order);
        }
        ```

##### 更新服务器的数据
* 用HTTP的DELETE来删除数据
    ```java
    @DeleteMapping("/{orderId}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
        repo.deleteById(orderId);
        } catch (EmptyResultDataAccessException e) {}
    }
    ```

#### 启用超媒体
* HATEOAS(Hypermedia as the Engine of Application State) is a means of creating self-describing APIs wherein resources returned from an API contain links to related resources. This enables clients to navigate an API with minimal understanding of the API’s URLs. Instead, it understands relationships between the resources served by the API and uses its understanding of those relationships to discover the API’s URLs as it traverses those relationships.
    * 例如我们获取了一些taco的数据，没有使用超媒体:
        ```json
        [
            {
                "id": 4,
                "name": "Veg-Out",
                "createdAt": "2018-01-31T20:15:53.219+0000",
                "ingredients": [
                    {"id": "FLTO", "name": "Flour Tortilla", "type": "WRAP"},
                    {"id": "COTO", "name": "Corn Tortilla", "type": "WRAP"},
                    {"id": "TMTO", "name": "Diced Tomatoes", "type": "VEGGIES"},
                    {"id": "LETC", "name": "Lettuce", "type": "VEGGIES"},
                    {"id": "SLSA", "name": "Salsa", "type": "SAUCE"}
                ]
            },
            // ...
        ]
        ```
    * 使用超媒体:
        ```json
        {
            "_embedded": {
                "tacoResourceList": [
                    {
                        "name": "Veg-Out",
                        "createdAt": "2018-01-31T20:15:53.219+0000",
                        "ingredients": [
                            {
                                "name": "Flour Tortilla", "type": "WRAP",
                                "_links": {
                                    "self": { "href": "http://localhost:8080/ingredients/FLTO" }
                                }
                            },
                            {
                                "name": "Corn Tortilla", "type": "WRAP",
                                "_links": {
                                    "self": { "href": "http://localhost:8080/ingredients/COTO" }
                                }
                            },
                            {
                                "name": "Diced Tomatoes", "type": "VEGGIES",
                                "_links": {
                                    "self": { "href": "http://localhost:8080/ingredients/TMTO" }
                                }
                            },
                            {
                                "name": "Lettuce", "type": "VEGGIES",
                                "_links": {
                                    "self": { "href": "http://localhost:8080/ingredients/LETC" }
                                }
                            },
                            {
                                "name": "Salsa", "type": "SAUCE",
                                "_links": {
                                    "self": { "href": "http://localhost:8080/ingredients/SLSA" }
                                }
                            }
                        ],
                        "_links": {
                            "self": { "href": "http://localhost:8080/design/4" }
                        }
                    },
                    // ...
                ]
            },
            "_links": {
                "recents": {
                    "href": "http://localhost:8080/design/recent"
                }
            }
        }
        ```

##### 添加hyperlinks
* Spring HATEOAS provides two primary types that represent hyperlinked resources: 
    * `Resource`是单个resource
    * `Resources`是`Resource`的集合
    ```java
    @GetMapping("/recent")
    public Resources<Resource<Taco>> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepo.findAll(page).getContent();
        Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
        recentResources.add(new Link("http://localhost:8080/design/recent", "recents"));
        return recentResources;
    }
    ```
    * use `Resources.wrap()` to wrap the list of tacos as an instance of `Resources<Resource<Taco>>`
* `ControllerLinkBuilder`时最常用的链接关系建造器
    ```java
    Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
    recentResources.add(
        ControllerLinkBuilder.linkTo(DesignTacoController.class)
            .slash("recent")
            .withRel("recents"));
    ```
    * 以控制器的URL为基础进行链接建造
    * `slash()`用于在链接中加斜线
    ```java
    Resources<Resource<Taco>> recentResources = Resources.wrap(tacos);
    recentResources.add(
        linkTo(methodOn(DesignTacoController.class).recentTacos())
        .withRel("recents"));
    ```
    * `linkTo()`
    * `methodOn()`用于链接到具体方法的URL

##### 创建resource assemble
* `Resources`中存的多个`Resource<Taco>`，一个个的添加比较麻烦，可以把`Taco`转换成`TacoResource`，然后使用resource assembler可以避免这个情况
    ```java
    public class TacoResource extends ResourceSupport {
        @Getter
        private final String name;
        @Getter
        private final Date createdAt;
        @Getter
        private final List<Ingredient> ingredients;
        public TacoResource(Taco taco) {
            this.name = taco.getName();
            this.createdAt = taco.getCreatedAt();
            this.ingredients = taco.getIngredients();
        }
    }
    ```
* `Taco`和`TacoResource`属性相同，但是`TacoResource`继承了`ResourceSupport`，`ResourceSupport`有一个存链接的List
* 用resource assembler组装resources
    ```java
    public class TacoResourceAssembler extends ResourceAssemblerSupport<Taco, TacoResource> {
        public TacoResourceAssembler() {
            super(DesignTacoController.class, TacoResource.class);
        }

        @Override
        protected TacoResource instantiateResource(Taco taco) {
            return new TacoResource(taco);
        }

        @Override
        public TacoResource toResource(Taco taco) {
            return createResourceWithId(taco.getId(), taco);
        }
    }
    ```
    * `ResourceAssemblerSupport`会读取控制器的URL作为创造Resource的Link的基础
    * `toResource()` is intended not only to create the Resource object, but also to populate it with links. Under the covers, `toResource()` will call `instantiateResource()`.
* 控制器中使用resource assembler
    ```java
    @GetMapping("/recent")
    public Resources<TacoResource> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepo.findAll(page).getContent();
        List<TacoResource> tacoResources = new TacoResourceAssembler().toResources(tacos);
        Resources<TacoResource> recentResources = new Resources<TacoResource>(tacoResources);
        recentResources.add(linkTo(methodOn(DesignTacoController.class).recentTacos()).withRel("recents"));
        return recentResources;
    }
    ```

#### 开启数据支撑服务
* Spring Data REST is another member of the Spring Data family that automatically creates REST APIs for repositories created by Spring Data.
* That’s all that’s required to expose a REST API in a project that’s already using Spring Data for automatic repositories.

##### Adjusting resource paths and relation names
```java
@Data
@Entity
@RestResource(rel="tacos", path="tacos")
public class Taco {
// ...
}
```
* `@RestResource` annotation lets you give the entity any relation name and path you want

##### Adding custom endpoints
* Spring Data REST is great at creating endpoints for performing CRUD operations against Spring Data repositories. But sometimes you need to break away from the default CRUD API and create an endpoint that gets to the core of the problem.
* Spring Data REST includes `@RepositoryRestController`, a new annotation for annotating controller classes whose mappings should assume a base path that’s the same as the one configured for Spring Data REST endpoints. Put simply, all mappings in a `@RepositoryRestController` annotated controller will have their path prefixed with the value of the `spring.data.rest.base-path` property (which you’ve configured as /api).
    ```java
    @RepositoryRestController
    public class RecentTacosController {
        private TacoRepository tacoRepo;
        public RecentTacosController(TacoRepository tacoRepo) {
            this.tacoRepo = tacoRepo;
        }

        @GetMapping(path="/tacos/recent", produces="application/hal+json")
        public ResponseEntity<Resources<TacoResource>> recentTacos() {
            PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
            List<Taco> tacos = tacoRepo.findAll(page).getContent();
            List<TacoResource> tacoResources = new TacoResourceAssembler().toResources(tacos);
            Resources<TacoResource> recentResources = new Resources<TacoResource>(tacoResources);
            recentResources.add(linkTo(methodOn(RecentTacosController.class).recentTacos()).withRel("recents"));
            return new ResponseEntity<>(recentResources, HttpStatus.OK);
        }
    }
    ```
    * 尽管控制器给的路径是`/tacos/recent`，但是`@RepositoryRestController`还是会在路径前添加Spring Data REST的基础，变成`/api/tacos/recent`

### 消费REST服务
* A Spring application can consume a REST API with
    * RestTemplate
    * Traverson
    * WebClient

#### 使用RestTemplate消费REST服务
* Working with low-level HTTP libraries involves a lot of repeated boilerplate code. To avoid such boilerplate code, Spring provides `RestTemplate`. Just as `JDBCTemplate` handles the ugly parts of working with JDBC, `RestTemplate` frees you from dealing with the tedium of consuming REST resources.
* `RestTemplate` defines 12 unique operations, each of which is overloaded, providing a total of 41 methods.
    * `delete(…)` Performs an HTTP DELETE request on a resource at a specified URL
    * `exchange(…)` Executes a specified HTTP method against a URL, returning a `ResponseEntity` containing an object mapped from the response body
    * `execute(…)` Executes a specified HTTP method against a URL, returning an object mapped from the response body
    * `getForEntity(…)` Sends an HTTP GET request, returning a `ResponseEntity` containing an object mapped from the response body
    * `getForObject(…)` Sends an HTTP GET request, returning an object mapped from a response body
    * `headForHeaders(…)` Sends an HTTP HEAD request, returning the HTTP headers for the specified resource URL
    * `optionsForAllow(…)` Sends an HTTP OPTIONS request, returning the Allow header for the specified URL
    * `patchForObject(…)` Sends an HTTP PATCH request, returning the resulting object mapped from the response body
    * `postForEntity(…)` POSTs data to a URL, returning a `ResponseEntity` containing an object mapped from the response body
    * `postForLocation(…)` POSTs data to a URL, returning the URL of the newly created resource
    * `postForObject(…)` POSTs data to a URL, returning an object mapped from the response body
    * `put(…)` PUTs resource data to the specified URL
* uses `RestTemplate` to fetch an Ingredient object by its ID
    ```java
    public Ingredient getIngredientById(String ingredientId) {
        return rest.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, ingredientId);
    }
    ```
    * `getForObject()`的第二个参数是指定回复会绑定的类
* you can use a Map to specify the URL variables
    ```java
    public Ingredient getIngredientById(String ingredientId) {
        Map<String,String> urlVariables = new HashMap<>();
        urlVariables.put("id", ingredientId);
        return rest.getForObject("http://localhost:8080/ingredients/{id}", Ingredient.class, urlVariables);
    }
    ```
* Using a URI parameter to call `getForObject()`

#### 使用Traverson导航REST服务
* Spring通过称为`JmsTemplate`的基于模板的抽象来支持JMS。
* 使用`JmsTemplate`，很容易从生产者端跨队列和主题发送消息，并在消费者端接收这些消息。
* Spring还支持消息驱动POJO的概念:简单的Java对象以异步方式对队列或主题上到达的消息做出响应。

### 发送异步信息
#### 使用JMS发送信息
##### 设置JMS
* JMS有两个依赖可以选择:
    * Apache ActiveMQ
    * Apache ActiveMQ Artemis Broker
* Artemis是ActiveMQ的下一代重新实现，实际上这让ActiveMQ成为一个遗留选项，唯一显著的区别在于如何配置Spring来创建与Broker的连接。
* Artemis需配置的属性:
    * `spring.artemis.host` broker主机
    * `spring.artemis.portbroker` 端口
    * `spring.artemis.user` 用于访问broker的用户(可选)
    * `spring.artemis.password` 用于访问broker的密码(可选)
* 使用application.yml配置Artemis的属性
    ```yml
    spring:
        artemis:
            host: artemis.tacocloud.com
            port: 61617
            user: tacoweb
            password: 13tm31n
    ```
* ActiveMQ需配置的属性
    * `spring.activemq.broker-url` Broker的URL
    * `spring.activemq.user` 用于访问Broker的用户(可选)
    * `spring.activemq.password` 用于访问Broker的密码(可选)
    * `spring.activemq.in-memory` 是否启动内存Broker(默认:true)
* 使用application.yml配置ActiveMQ的属性
    ```yml
    spring:
        activemq:
            broker-url: tcp://activemq.tacocloud.com
            user: tacoweb
            password: 13tm31n
    ```
* 无论选择Artemis还是ActiveMQ，当Broker在本地运行时，都不需要为开发环境配置这些属性。

##### 使用JmsTemplate发送消息
* `JmsTemplate`有几个发送消息的有用方法
    ```java
    // 发送原始消息
    void send(MessageCreator messageCreator) throws JmsException;
    void send(Destination destination, MessageCreator messageCreator) throws JmsException;
    void send(String destinationName, MessageCreator messageCreator) throws JmsException;
    // 发送转换自对象的消息
    void convertAndSend(Object message) throws JmsException;
    void convertAndSend(Destination destination, Object message) throws JmsException;
    void convertAndSend(String destinationName, Object message) throws JmsException;
    // 发送经过处理后从对象转换而来的消息
    void convertAndSend(Object message, MessagePostProcessor postProcessor) throws JmsException;
    void convertAndSend(Destination destination, Object message, MessagePostProcessor postProcessor) throws JmsException;
    void convertAndSend(String destinationName, Object message, MessagePostProcessor postProcessor) throws JmsException;
    ```
    * 实际上只有两个方法，`send()`和`convertAndSend()`，每个方法都被重载以支持不同的参数。
* 使用`send()`方法的最基本形式
    ```java
    @Service
    public class JmsOrderMessagingService implements OrderMessagingService {
        private JmsTemplate jms;
        @Autowired
        public JmsOrderMessagingService(JmsTemplate jms) {
            this.jms = jms;
        }
        
        @Override
        public void sendOrder(Order order) {
            jms.send(new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(order);
                }
            });
        }
    }
    ```
    * `jms.send()`没有指定目的地，还必须使用`spring.jms.template.default-destination`属性指定一个默认的目的地名称
        ```yml
        spring:
            jms:
                template:
                    default-destination: tacocloud.order.queue
        ```
        * 如果需要发送到默认目的地之外的地方，需要在`send()`指明目的地
* 有一种方法是将一个`Destination`传给`send()`，最简单的方法是声明一个`Destination`bean，然后将其注入执行消息传递的bean
* 下面的bean声明了Taco Cloud订单队列`Destination`
    ```java
    public Destination orderQueue() {
        return new ActiveMQQueue("tacocloud.order.queue");
    }
    ```
    * 这是Artemis的`ActiveMQQueue`
* 将`Destination`bean注入`JmsOrderMessagingService`，`send()`就可以为获取目的地
    ```java
    private Destination orderQueue;
    ​
    @Autowired
    public JmsOrderMessagingService(JmsTemplate jms, Destination orderQueue) {
        this.jms = jms;
        this.orderQueue = orderQueue;
    }
    ​
    @Override
    public void sendOrder(Order order) {
        jms.send(
            orderQueue,
            session -> session.createObjectMessage(order));
    }
    ```

###### 在发送前转换消息
* `JmsTemplates`的`convertAndSend()`方法不需要提供`MessageCreator`，从而简化了消息发布。
* `sendOrder()`的以下重新实现使用`convertAndSend()`将Order发送到指定的目的地:
    ```java
    @Override
    public void sendOrder(Order order) {
        jms.convertAndSend("tacocloud.order.queue", order);
    }
    ```
    * `convertAndSend()`用`MessageConverter`将对象转换为消息的复杂工作

###### 配置消息转换器
* MessageConverter 是 Spring 定义的接口，它只有两个用于实现的方法:
    ```java
    public interface MessageConverter {
        Message toMessage(Object object, Session session) throws JMSException, MessageConversionException;
        Object fromMessage(Message message);
    }
    ```
* 这个接口的实现很简单，都不需要创建自定义实现，Spring 已经提供了一些有用的实现:
    * `MappingJackson2MessageConverter`
    * `MarshallingMessageConverter`
    * `MessagingMessageConverter`
    * `SimpleMessageConverter`
        * 将String转换为TextMessage
        * 将字节数组转换为BytesMessage
        * 将Map转换为MapMessage
        * 将Serializable转换为ObjectMessage
* `SimpleMessageConverter`是默认的消息转换器，但是它要求发送的对象实现`Serializable`接口。
* 应用不同的消息转换器，需要做的是将选择的转换器声明为一个bean
    ```java
    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_typeId");
        return messageConverter;
    }
    ```
    * `setTypeIdPropertyName()`使接收者知道要将传入消息转换成什么类型，默认情况下，它将包含被转换类型的完全限定类名。但这有点不灵活，要求接收方也具有相同的类型，具有相同的完全限定类名。
* 为了实现更大的灵活性，可以通过调用消息转换器上的`setTypeIdMappings()`将合成类型名称映射到实际类型。
    ```java
    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_typeId");
        
        Map<String, Class<?>> typeIdMappings = new HashMap<String, Class<?>>();
        typeIdMappings.put("order", Order.class);
        messageConverter.setTypeIdMappings(typeIdMappings);
        
        return messageConverter;
    }
    ```
    * 与在消息的`_typeId`属性中发送完全限定的类名不同，将发送值`order`。在接收应用程序中，将配置类似的消息转换器，将`order`映射到它自己对order的理解。

###### 后期处理消息
* Taco Cloud还决定开几家实体Taco连锁店，任何一家餐馆也可以成为web业务的执行中心，需要一种方法来将订单的来源传达给餐馆的厨房。这将使厨房工作人员能够对商店订单采用与网络订单不同的流程。
* 在`Order`对象中添加一个新的`source`属性来携带此信息是合理的，可以用WEB来填充在线订单，用STORE来填充商店中的订单。但这将需要更改网站的`Order`类和厨房应用程序的`Order`类，而实际上，这些信息只需要为taco准备人员提供。
* 更简单的解决方案是在消息中添加一个自定义头信息，以承载订单的源。如果正在使用`send()`方法发送taco订单，这可以通过调用消息对象上的`setStringProperty()`轻松实现:
    ```java
    jms.send("tacocloud.order.queue",
            session -> {
                Message message = session.createObjectMessage(order);
                message.setStringProperty("X_ORDER_SOURCE", "WEB");
            });
    ```
    * 但是`Message`对象是在幕后创建的，并且不能访问它
* 使用`convertAndSend()`
    ```java
    jms.convertAndSend("tacocloud.order.queue", order, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message)
                throws JMSException {
                message.setStringProperty("X_ORDER_SOURCE", "WEB");
                return message;
            }
    });
    ```
    * `MessagePostProcessor`可以在消息创建之后对其进行任何操作，在消息发送之前添加`X_ORDER_SOURCE`头信息

##### 接收JMS消息
* 可以选择`拉模型`(代码请求消息并等待消息到达)或`推模型`(消息可用时将消息传递给代码)。
* `JmsTemplate`提供了几种接收消息的方法，但它们都使用拉模型。
* 还可以选择使用推模型，在该模型中，定义了一个消息监听器，它在消息可用时被调用。

###### 使用JmsTemplate接收
* `JmsTemplate`提供多个用于拉模式的方法
    ```java
    Message receive() throws JmsException;
    Message receive(Destination destination) throws JmsException;
    Message receive(String destinationName) throws JmsException;

    Object receiveAndConvert() throws JmsException;
    Object receiveAndConvert(Destination destination) throws JmsException;
    Object receiveAndConvert(String destinationName) throws JmsException;
    ```
* 要查看这些操作，需要编写一些代码来从`tacocloud.order.queue`的目的地拉取`Order`。下面的程序清单显示了`OrderReceiver`，这是一个使用`JmsTemplate.receive()`接收`Order`数据的服务组件。
    ```java
    @Component
    public class JmsOrderReceiver implements OrderReceiver {
        private JmsTemplate jms;
        private MessageConverter converter;
        @Autowired
        public JmsOrderReceiver(JmsTemplate jms, MessageConverter converter) {
            this.jms = jms;
            this.converter = converter;
        }
        
        public Order receiveOrder() {
            Message message = jms.receive("tacocloud.order.queue");
            return (Order) converter.fromMessage(message);
        }
    }
    ```
    * Here you’ve used a String to specify the destination to pull an order from.
* `receiveAndConvert()`要简单得多
    ```java
    @Component
    public class JmsOrderReceiver implements OrderReceiver {
        private JmsTemplate jms;
        @Autowired
        public JmsOrderReceiver(JmsTemplate jms) {
            this.jms = jms;
        }
        
        public Order receiveOrder() {
            return (Order) jms.receiveAndConvert("tacocloud.order.queue");
        }
    }
    ```

###### 声明消息监听器
* 与拉模型不同，消息监听器是一个被动组件，在消息到达之前是空闲的。
* 要创建对JMS消息作出响应的消息监听器，只需使用`@JmsListener`对组件中的方法进行注解。
    ```java
    @Component
    public class OrderListener {
        private KitchenUI ui;
        @Autowired
        public OrderListener(KitchenUI ui) {
            this.ui = ui;
        }
        
        @JmsListener(destination = "tacocloud.order.queue")
        public void receiveOrder(Order order) {
            ui.displayOrder(order);
        }
    }
    ```
    * `receiveOrder()`方法由`JmsListener`注解，以监听`tacocloud.order.queue`目的地的消息。
    * 当消息到达时，`receiveOrder()`方法将自动调用，并将消息的`Order`有效负载作为参数。
    * `@JmsListener`注解类似于Spring MVC的请求映射注释，对指定路径的请求做出响应。

#### 使用RabbitMQ和AMQP
* JMS消息使用接收方将从中检索它们的目的地的名称来寻址，而AMQP消息使用交换器的名称和路由键来寻址，它们与接收方正在监听的队列解耦。
    ```
            +---------------------------------+
            | RabbitMQ Broker                 |
    Sender -+-> Exchange -> Binding -> Queue -+-> sender
            +---------------------------------+
    ```
* 当消息到达RabbitMQ broker时，它将转到它所寻址的交换器。交换器负责将其路由到一个或多个队列，具体取决于交换器的类型、交换器与队列之间的绑定以及消息的路由键的值。
* 有几种不同的交换方式，包括以下几种:
    * Default: 一种特殊的交换器，通过broker自动创建。它将消息路由到与消息的路由键的值同名的队列中。所有的队列将会自动地与交换器绑定。
    * Direct: 路由消息到消息路由键的值与绑定值相同的队列。
    * Topic: 将消息路由到一个或多个队列，其中绑定键(可能包含通配符)与消息的路由键匹配。
    * Fanout: 将消息路由到所有绑定队列，而不考虑绑定键或路由键。
    * Headers: 与topic交换器类似，只是路由基于消息头值而不是路由键。
    * Dead letter: 对无法交付的消息(意味着它们不匹配任何已定义的交换器与队列的绑定)的全部捕获。

##### 添加RabbitMQ到Spring中
* 需要将Spring Boot的AMQP starter依赖项添加到构建中
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>
    ```
    * 将AMQP starter添加到构建中将触发自动配置，该配置将创建AMQP 连接工厂和`RabbitTemplate`bean，以及其他支持组件。只需添加此依赖项，就可以开始使用Spring从RabbitMQ broker发送和接收消息
* 配置RabbitMQ broker的位置和凭据的属性
    * `spring.rabbitmq.addresses` 一个逗号分隔的RabbitMQ Broker地址列表
    * `spring.rabbitmq.host` Broker主机（默认为 localhost）
    * `spring.rabbitmq.port` Broker端口（默认为 5672）
    * `spring.rabbitmq.username` 访问Broker的用户名（可选）
    * `spring.rabbitmq.password` 访问Broker的密码（可选）
* 假设在进入生产环境时，RabbitMQ Broker位于一个名为`rabbit.tacocloud.com`的服务器上，监听端口5673，application.yml的配置:
    ```yml
    spring:
        profiles: prod
        rabbitmq:
            host: rabbit.tacocloud.com
            port: 5673
            username: tacoweb
            password: l3tm31n
    ```

##### 使用RabbitTemplate发送消息
* Spring对于RabbitMQ消息支持的核心就是`RabbitTemplate`。
* `RabbitTemplate`发送消息的最有用的方法
    ```java
    // 发送原始消息
    void send(Message message) throws AmqpException;
    void send(String routingKey, Message message) throws AmqpException;
    void send(String exchange, String routingKey, Message message) throws AmqpException;
    ​
    // 发送从对象转换过来的消息
    void convertAndSend(Object message) throws AmqpException;
    void convertAndSend(String routingKey, Object message) throws AmqpException;
    void convertAndSend(String exchange, String routingKey, Object message) throws AmqpException;
    ​
    // 发送经过处理后从对象转换过来的消息
    void convertAndSend(Object message, MessagePostProcessor mPP) throws AmqpException;
    void convertAndSend(String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException;
    void convertAndSend(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor) throws AmqpException;
    ```
* 用`RabbitTemplate`发送taco订单
    * 使用`send()`方法
        ```java
        @Service
        public class RabbitOrderMessagingService implements OrderMessagingService {
            
            private RabbitTemplate rabbit;
            
            @Autowired
            public RabbitOrderMessagingService(RabbitTemplate rabbit) {
                this.rabbit = rabbit;
            }
            
            public void sendOrder(Order order) {
                MessageConverter converter = rabbit.getMessageConverter();
                MessageProperties props = new MessageProperties();
                Message message = converter.toMessage(order, props);
                rabbit.send("tacocloud.order", message);
            }
        }
        ```
        * 调用`send()`之前，将`Order`对象转换为消息
        * `getMessageConverter()`用于消息转换
        * `MessageProperties`用于设置属性，无属性的话可以缺省
        * 只指定了与消息一起的路由键`tacocloud.order`
    * 在application.yml设置默认交换器与路由键
        ```yml
        spring:
            rabbitmq:
                template:
                    exchange: tacocloud.orders
                    routing-key: kitchens.central
        ```

###### 配置消息转换器
* 默认情况下，使用`SimpleMessageConverter`执行消息转换
* Spring为`RabbitTemplate`提供了几个消息转换器
    * `Jackson2JsonMessageConverter`
    * `MarshallingMessageConverter`
    * `SimpleMessageConverter`
    * `ContentTypeDelegatingMessageConverter`
    * `MessagingMessageConverter`
* 如果需要修改消息转换器，需要做的是配置`MessageConverter`bean
    ```java
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    ```

###### 设置消息属性
* 通过提供给消息转换器的`MessageProperties`实例设置消息头
    ```java
    public void sendOrder(Order order) {
        MessageConverter converter = rabbit.getMessageConverter();
        MessageProperties props = new MessageProperties();
        props.setHeader("X_ORDER_SOURCE", "WEB");
        Message message = converter.toMessage(order, props);
        rabbit.send("tacocloud.order", message);
    }
    ```
* 在使用`convertAndSend()`时，不能快速访问`MessageProperties`对象。不过，`MessagePostProcessor`可以做到这一点:
    ```java
    @Override
    public void sendOrder(Order order) {
        rabbit.convertAndSend("tacocloud.order.queue", order,
            new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties props = message.getMessageProperties();
                    props.setHeader("X_ORDER_SOURCE", "WEB");
                    return message;
                }
            });
    }
    ```

##### 从RabbitMQ接收消息
* 有两个选择:
    * 使用`RabbitTemplate`从队列中拉取消息
    * 获取被推送到`@RabbitListener`注解的方法中的消息
* 使用`RabbitTemplate`接收消息
* `RabbitTemplate`有多个从队列中拉取消息的方法
    ```java
    // 接收消息
    Message receive() throws AmqpException;
    Message receive(String queueName) throws AmqpException;
    Message receive(long timeoutMillis) throws AmqpException;
    Message receive(String queueName, long timeoutMillis) throws AmqpException;
    ​
    // 接收从消息转换过来的对象
    Object receiveAndConvert() throws AmqpException;
    Object receiveAndConvert(String queueName) throws AmqpException;
    Object receiveAndConvert(long timeoutMillis) throws AmqpException;
    Object receiveAndConvert(String queueName, long timeoutMillis) throws AmqpException;
    ​
    // 接收从消息转换过来的类型安全的对象
    <T> T receiveAndConvert(ParameterizedTypeReference<T> type) throws AmqpException;
    <T> T receiveAndConvert(String queueName, ParameterizedTypeReference<T> type) throws AmqpException;
    <T> T receiveAndConvert(long timeoutMillis, ParameterizedTypeReference<T> type) throws AmqpException;
    <T> T receiveAndConvert(String queueName, long timeoutMillis, ParameterizedTypeReference<T> type) throws AmqpException;
    ```
    * 许多方法接受一个long参数来表示接收消息的超时。默认情况下，接收超时为0毫秒。也就是说，对receive()的调用将立即返回，如果没有可用的消息，则可能返回空值。通过传入超时值，可以让`receive()`和 `receiveAndConvert()`方法阻塞，直到消息到达或超时过期。
*  使用`RabbitTemplate`从`RabbitMQ`拉取订单
    ```java
    @Component
    public class RabbitOrderReceiver {
        private RabbitTemplate rabbit;
        private MessageConverter converter;
        @Autowired
        public RabbitOrderReceiver(RabbitTemplate rabbit) {
            this.rabbit = rabbit;
            this.converter = rabbit.getMessageConverter();
        }
        
        public Order receiveOrder() {
            Message message = rabbit.receive("tacocloud.orders");
            return message != null
                ? (Order) converter.fromMessage(message)
                : null;
        }
    }
    ```
    * 调用所注入的`RabbitTemplate`上的`receive()`方法来从`tacocloud.queue`中获取订单。
* 改为传递一个30000毫秒的延迟后再调用`receive()`
    ```java
    Message message = rabbit.receive("tacocloud.order.queue", 30000);
    ```

###### 使用监听器处理RabbitMQ消息
* Spring提供了`RabbitListene`
    ```java
    @Component
    public class OrderListener {
        private KitchenUI ui;
        @Autowired
        public OrderListener(KitchenUI ui) {
            this.ui = ui;
        }
        
        @RabbitListener(queues = "tacocloud.order.queue")
        public void receiveOrder(Order order) {
            ui.displayOrder(order);
        }
    }
    ```

#### Kafka消息
* Kafka被设计为在集群中运行，提供了巨大的可伸缩性。通过将其topic划分到集群中的所有实例中，它具有很强的弹性。RabbitMQ主要处理exchange中的队列，而Kafka仅利用topic来提供消息的发布/订阅。
* Kafka topic被复制到集群中的所有broker中。集群中的每个节点充当一个或多个topic的leader，负责该topic的数据并将其复制到集群中的其他节点。
* 更进一步说，每个topic可以分成多个分区。在这种情况下，集群中的每个节点都是一个topic的一个或多个分区的leader，但不是整个topic的leader。该topic的职责由所有节点分担。

### 集成Spring
#### 声明一个简单的集成流
#### 相信概念
#### 创建一个email集成流

## Spring Reactive编程
### Reactor介绍
#### 理解Reactive编程
#### Mono
#### Flux
#### Reactive操作

### 开发Reactive API
#### 使用Spring WebFlux
#### 定义函数式请求处理器
#### 测试Reactive控制器
#### Reactive式的消费REST API
#### Reactive Web API安全

### Reactive式的持久化数据
#### Reactive类型和非Reactive类型的转换
#### 编写Reactive式的MongoDB存储

## Spring云原生
### 发现服务
#### 配置eureka服务注册器
#### 注册和发现服务

### 管理配置
#### 配置服务器
#### 配置属性安全
#### 动态的刷新配置

### 处理失败和延迟
#### 熔断器模式
#### @HystrixCommand
#### 失败监控
#### 聚集Hystrix流

## Spring部署
### 使用Spring Boot Actuator
#### 配置Acutator
#### 消费Acutator Endpoint
#### 自定义Acutator

### 管理Spring
#### Spring Boot Admin
#### 使用
#### 安全Spring Server

### 使用JMX监控Spring
#### 使用Actuator MBeans
#### 创建自己的MBeans
#### 发送通知

### 部署Spring
#### 构建和部署WAR
#### 部署到Cloud Foundry
#### 使用Docker容器运行Spring Boot应用