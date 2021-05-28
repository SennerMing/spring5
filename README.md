# Spring Framework 5

## 相关概念

Spring是轻量级的开源的JavaEE框架，Spring可以解决企业应用开发的复杂性

核心内容IOC和AOP

- IOC：控制反转，把差创建对象过程交给Spring进行管理
- AOP：面向切面进行编程，不修改源代码进行功能的增强

Spring特点

1. 方便解耦，简化开发
2. AOP编程的支持
3. 方便程序的测试
4. 方便和其他框架进行整合
5. 方便进行事务管理
6. 降低API开发的难度

## 项目搭建

### 开发前准备

导入基础包，beans、context、core、expression还有一个额外的commons-logging

### XML方式

1. 创建Spring的配置文件，在配置文件配置创建的对象

   ```xml
   <!-- 创建一个user bean -->
   <bean id="user" class="basic.User"></bean>
   ```

2. 进行测试类的编写

   ```java
   @Test
   public void testAdd() {
     //1.加载我们写的Spring的xml配置文件
     ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
     //2.获取通过配置创建的对象
     User user = context.getBean("user", User.class);
     //获取到对象并使用
     System.out.println(user);
     user.add();
   }
   ```

针对以上操作进行一个简单地讲解

最主要的就是上面的ApplicationContext了，那么他就是一个IOC的容器，那么就对IOC进行一些理解

IOC（Inversion of Control）降低代码之间的耦合度，最常见的方式就是DI-依赖查找（Dependency Injection），还有一种常见的方式是DL-依赖注入（Dependency Lookup），上面的方式就是通过进行XML的配置，让ApplicationContext对Classpath下的XML进行解析，将Beans交由Spring框架进行管理

#### 底层原理

- IOC底层原理
  - XML解析、工厂模式、反射技术

#### 初识Bean工厂

- IOC接口（BeanFactory）

  - IOC思想就是基于IOC容器完成的，IOC容器底层就是对象工厂

  - Spring提供的IOC容器的实现方式（两个接口）

    - BeanFactory

    ```java
    //也就是说上面的ApplicationContext可以换成BeanFactory，但是咱么这边不推荐亲使用呢，因为这个玩意是Spring IOC容器的最基本的实现方式，Spring的内置IOC容器，它不面向咱们programer呢
    @Test
    public void testAdd() {
      //1.加载我们写的Spring的xml配置文件，这样的方式加载配置文件，他并不会创建对象，只有在获取或使用的时候才会去创建对象，这可能就是我们常说的懒加载吧
      BeanFactory context = new ClassPathXmlApplicationContext("beans.xml");
      //2.获取通过配置创建的对象
      User user = context.getBean("user", User.class);
      //获取到对象并使用
      System.out.println(user);
      user.add();
    }
    ```

    - ApplicationContext：他是BeanFactory的一个子接口，功能更为强大，面向咱们程序猿👨🏻‍💻的，他和BeanFactory就不一样了，他一加载XML方式，他就把对象创建了，不同的实现类
      - FileSystemXmlApplicationContext：对应的系统盘资源
      - ClassPathXmlApplicationContext：对应的src下的资源

#### Bean的XML配置

- IOC操作Bean管理
  - Bean管理是由Spring完成对象的创建，并对属性进行装配
  - IOC操作Bean管理（基于Xml）
    - 基于Xml方式进行对象创建
      - id：给创建的对象起一个别名，是一个唯一的标识
      - name：也可以作为一个唯一标识（为struts搭配使用），与id不同可以加一些特殊符号
      - class：创建对象的类的全路径名
      - 默认使用的是Java对象的无参构造

##### 简单注入

- 基于Xml方式进行属性的注入

  - DI：依赖注入，就是注入属性

    - 使用set方式进行属性注入

    ```java
    public class Book {
    
        private String name;
        private String author;
    
        public Book() {
        }
    
        public Book(String name, String author) {
            this.name = name;
            this.author = author;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getAuthor() {
            return author;
        }
    
        public void setAuthor(String author) {
            this.author = author;
        }
    
        @Override
        public String toString() {
            return "Book{" +
                    "name='" + name + '\'' +
                    '}';
        }
        public static void main(String[] args) {
            Book book = new Book();
            book.setName("书的名字");
            book.setAuthor("书的作者");
            System.out.println(book);
        }
    }
    ```

    ```xml
    <!--set方法进行属性的注入-->
    <bean id="book" class="basic.Book">
      <!--  使用Property完成属性的注入-->
      <!--  name:类里面的属性名称-->
      <!--  value:向属性中注入的值-->
      <property name="name" value="九阳真经"></property>
      <property name="author" value="欧阳锋"></property>
    </bean>
    ```

    ```java
    @Test
    public void testBook() {
      //1.加载我们写的Spring的xml配置文件
      ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
      //2.获取通过配置创建的对象
      Book book = context.getBean("book", Book.class);
      //获取到对象并使用
      System.out.println(book);
    }
    ```

##### 有参构造器

- 使用有参构造器进行属性注入

```java
public class Order {

    private String name;
    private String address;

    public Order(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
```

```xml
<bean id="order" class="basic.Order">
  <constructor-arg name="name" value="订单名称"></constructor-arg>
  <constructor-arg name="address" value="订单地址"></constructor-arg>
<!--        <constructor-arg index="0" value="订单名称"></constructor-arg>-->
<!--        <constructor-arg index="1" value="订单地址"></constructor-arg>-->
</bean>
```

```java
@Test
public void testOrder() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
  //2.获取通过配置创建的对象
  Order order = context.getBean("order", Order.class);
  //获取到对象并使用
  System.out.println(order);
}
```

##### p标签使用

**p名称空间的注入，可以简化基于xml的配置方式**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
  
	<bean id="book" class="basic.Book" p:name="易筋经" p:author="纪晓岚"></bean>
  
</beans>
```

##### 特殊符号注入

- 其他类型的属性注入

  - 字面量的注入

    - null值

    ```xml
    <!--    set方法进行属性的注入-->
    <bean id="book" class="basic.Book">
      <!--  使用Property完成属性的注入-->
      <!--        name:类里面的属性名称-->
      <!--        value:向属性中注入的值-->
      <property name="name" value="九阳真经"></property>
      <property name="author" value="欧阳锋"></property>
      <property name="address">	<!-- 使用null标签进行空值的设置 -->
        <null></null>
      </property>
    </bean>
    ```

    - 属性值包含一些特殊符号

    ```xml
     <!-- 属性值包含特殊符号
           1.把<>进行转义 &lt; &gt;
           2.把带特殊符号的内容写到CDATA
     -->
    <property name="address">
      <value><![CDATA[<<南京南>>]]></value>
    </property>
    ```

##### 引用类型注入

- 输入属性

  - 外部bean
    - 创建service类和dao类

    ```java
    //创建类可以参照 service.UserService、dao.UserDao、dao.UserDaoImpl
    ```

    ```xml
    <!--创建Dao对象-->
    <bean id="userDao" class="dao.UserDaoImpl"></bean>
    
    <!--创建Service对象-->
    <bean id="userService" class="service.UserService">
      <!-- name 类里面属性名称 -->
      <!-- ref 引用类型的值，userDao在Spring容器内id的值-->
      <property name="userDao" ref="userDao"/>
    </bean>
    ```

    ```java
    @Test
    public void testRef() {
      //1.加载我们写的Spring的xml配置文件
      ApplicationContext context = new ClassPathXmlApplicationContext("beans1.xml");
      //2.获取通过配置创建的对象
      UserService userService = context.getBean("userService", UserService.class);
      //获取到对象并使用
      userService.add();
    }
    ```

  - 内部bean

    一对多的关系：一个部门下面可以有多个员工，一个员工属于某一个部门

    ```java
    //参照 bean.Department、bean.Employee
    ```

    ```xml
     <!-- 外部bean -->
    <!--<bean id="department" class="bean.Department"></bean>-->
    
    <bean id="employee" class="bean.Employee">
      <property name="name" value="Senner Ming"/>
      <property name="gender" value="Man"/>
      <property name="department">
        <!-- 内部bean -->
        <bean id="department" class="bean.Department">
          <property name="name" value="安保部"/>
        </bean>
      </property>
    </bean>
    ```

    ```java
    @Test
    public void testInnerBean() {
      //1.加载我们写的Spring的xml配置文件
      ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
      //2.获取通过配置创建的对象
      Employee employee = context.getBean("employee", Employee.class);
      //获取到对象并使用
      System.out.println(employee);
    }
    ```

  - 级联赋值

    ```xml
    <!-- 外部bean -->
    <bean id="department" class="bean.Department">
      <property name="name" value="舆情业务部"/>
    </bean>
    
    <bean id="employee" class="bean.Employee">
      <property name="name" value="Senner Ming"/>
      <property name="gender" value="Man"/>
      <property name="department" ref="department">
        <!-- 内部bean -->
        <!--            <bean id="department" class="bean.Department">-->
        <!--                <property name="name" value="安保部"/>-->
        <!--            </bean>-->
      </property>
      <property name="department.name" value="情报部"></property>
    </bean>
    ```

##### 集合类型注入

- 注入集合属性

  - 注入数组类型属性

  - 注入List集合类型属性

  - 注入Map集合类型属性

```java
//参照代码collection.Student
```

```xml
<!-- 集合类型属性的注入 -->
<bean id="student" class="collection.Student">
  <!--数组类型的属性注入-->
  <property name="courses">
    <array value-type="java.lang.String">
      <value>英语</value>
      <value>数学</value>
      <value>语文</value>
    </array>
  </property>
  <!-- List类型的属性注入 -->
  <property name="list">
    <list>
      <value>张三</value>
      <value>三弟</value>
      <value>三儿</value>
    </list>
  </property>
  <property name="map">
    <map key-type="java.lang.String" value-type="java.lang.String">
      <entry key="key1" value="value1"></entry>
      <entry key="key2" value="value2"></entry>
      <entry key="key3" value="value3"></entry>
    </map>
  </property>
  <property name="set">
    <set value-type="java.lang.String">
      <value>Mysql</value>
      <value>Redis</value>
    </set>
  </property>
```

```java
@Test
public void testCollection() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("collection.xml");
  //2.获取通过配置创建的对象
  Student student = context.getBean("student", Student.class);
  //获取到对象并使用
  System.out.println(student);
}
```

**为集合注入其他引用类型元素**

```xml
 <!-- 集合类型属性的注入 -->
<bean id="student" class="collection.Student">
<!-- 注入List<Course>，值是对象-->
  <property name="courseList">
    <list value-type="collection.Course">
      <ref bean="course1"></ref>
      <ref bean="course2"/>
    </list>
  </property>
</bean>

<!-- 创建英语课程对象 -->
<bean id="course1" class="collection.Course">
    <property name="name" value="Spring5框架"></property>
</bean>

<bean id="course2" class="collection.Course">
    <property name="name" value="Spring Boot框架"></property>
</bean>
```

```java
//还是上面的testCollection()
```

**将创建集合的功能提取出来**

##### util标签使用

使用util工具进行配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">

    <!-- 提取List集合类型属性注入 -->
    <util:list id="units">
        <value>九阴真经</value>
        <value>九阳神功</value>
        <value>九九皮炎平</value>
    </util:list>

    <bean id="district" class="collection.District">
        <property name="units" ref="units"></property>
    </bean>

</beans>
```

```java
@Test
public void testDistrict() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("collection1.xml");
  //2.获取通过配置创建的对象
  District district = context.getBean("district", District.class);
  //获取到对象并使用
  System.out.println(district);
}
```

##### FactoryBean

- IOC操作Bean管理（FactoryBean）

  一种Bean使我们自己创建的普通Bean，还有一种是工厂Bean（FactoryBean）

  普通Bean：在配置文件中定义的类型就是返回的类型

  工厂Bean：在配置文件中定义的bean类型可以和返回类型它不一样

  第一步：创建类，让这个类作为工厂Bean，实现接口FactoryBean

  第二步：实现接口中的方法，在实现的方法中定义返回的Bean类型

```xml
<bean id="production" class="factory.ProductionFactory"></bean>
```

```java
public class Production {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Production{" +
                "name='" + name + '\'' +
                '}';
    }
}
//===============================================================
public class ProductionFactory implements FactoryBean<Production> {
    @Override
    public Production getObject() throws Exception {
        Production production = new Production();
        production.setName("桂格燕麦");
        return production;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
```

```java
@Test
public void testFactory() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");
  //2.获取通过配置创建的对象
  Production production = context.getBean("production", Production.class);
  //获取到对象并使用
  System.out.println(production);
}
```

##### Bean作用域

- IOC操作Bean管理（Bean的作用域）

1、在Spring里面，设置创建Bean实例是单例还是多例

2、在Spring里面，默认的情况下创建的Bean是一个单实例对象

```java
//运行以下代码
@Test
public void testDistrict() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("collection1.xml");
  //2.获取通过配置创建的对象
  District district1 = context.getBean("district", District.class);
  District district2 = context.getBean("district", District.class);
  //获取到对象并使用
  System.out.println(district1);
  System.out.println(district2);
}
//运行结果,地址一致的，相同的对象
/**
	collection.District@5b7a5baa
	collection.District@5b7a5baa
*/
```

可以通过Spring配置文件bean标签里面的属性（scope）的设置进行Bean作用域的修改

第一个值，**singleton**，表示单实例对象，默认值，当Spring在**加载**配置文件的时候，就会**创建**单实例对象

第二个值，**prototype**，表示的是多实例对象，当Spring在加载配置文件的时候并不会进行对象的创建，在**调用**getBean的时候才去**创建**多实例的对象

```xml
<bean id="district" class="collection.District" scope="prototype">
  <property name="units" ref="units"></property>
</bean>
```

```java
//再次运行上面的testDistrict()方法，可以看到，两次返回的对象地址不同了
/**
	collection.District@776aec5c
	collection.District@1d296da
*/
```

还有request和session作用域，request：每次请求；session：一次会话

### Bean的注解配置

- IOC操作Bean管理（基于注解）

  

### Bean的生命周期

Bean的生命周期是描述从对象创建到对象销毁的过程

Bean的生命周期

1. 通过构造器创建bean实例（无参构造器）
2. 为Bean的属性设置值和对其他Bean的引用（调用set方法）
3. 调用Bean的初始化的方法（需要进行相应的配置）
4. Bean可以进行的使用（获取对象）
5. 当容器关闭,调用Bean的销毁方法（需要进行配置销毁的方法）

### 基本初始化流程

```xml
<!-- lifecycle.xml -->
<bean id="person" class="lifecycle.Person" init-method="initMethod" destroy-method="destroyMethod">
  <property name="name" value="Senner Ming"></property>
  <property name="age" value="18"></property>
</bean>
```

```java
package lifecycle;

public class Person {
    private String name;
    private int age;

    //Person的初始化方法
    public void initMethod() {
        //这时Person的初始化方法
        System.out.println("Person的initMethod().....");
    }

    //Person的销毁方法
    public void destroyMethod() {
        //这时Person的初始化方法
        System.out.println("Person的destroyMethod().....");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("进行Person的name的设置");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        System.out.println("进行Person的age的设置");
        this.age = age;
    }

    public Person() {
        System.out.println("执行了无参数的构造器.....");
    }

    public Person(String name, int age) {
        System.out.println("执行了有参数的构造器.....");
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

```

```java
@Test
public void testLifeCycle() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("lifecycle.xml");
  //2.获取通过配置创建的对象
  Person person = context.getBean("production", Person.class);
  //获取到对象并使用
  System.out.println(person);

  //手动让Bean实例销毁
  ((ClassPathXmlApplicationContext)context).close();
}
/**
	执行的结果：
			执行了无参数的构造器.....
			进行Person的name的设置
			进行Person的age的设置
			Person的initMethod().....
			Person{name='Senner Ming', age=18}
			Person的destroyMethod().....
*/
```

### 后置处理

其实还有两步的操作，下面的这个生命周期就全了

1. 通过构造器创建bean实例（无参构造器）
2. 为Bean的属性设置值和对其他Bean的引用（调用set方法）
3. **把Bean实例传递Bean后置处理器的方法** postProcessBeforeInitialization
4. 调用Bean的初始化的方法（需要进行相应的配置）
5. **把Bean实例传递Bean后置处理器的方法** postProcessAfterInitialization
6. Bean可以进行的使用（获取对象）
7. 当容器关闭,调用Bean的销毁方法（需要进行配置销毁的方法）

```xml
<bean id="person" class="lifecycle.Person" init-method="initMethod" destroy-method="destroyMethod">
  <property name="name" value="Senner Ming"></property>
  <property name="age" value="18"></property>
</bean>
<bean id="myBeanPost" class="lifecycle.MyBeanPost"></bean>
```

```java
public class MyBeanPost implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("在Bean initMethod()之前执行的方法.....");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("在Bean initMethod()之后执行的方法.....");
        return bean;
    }
}
```

```java
//最终打印结果
执行了无参数的构造器.....
进行Person的name的设置
进行Person的age的设置
在Bean initMethod()之前执行的方法.....
Person的initMethod().....
在Bean initMethod()之后执行的方法.....
Person{name='Senner Ming', age=18}
Person的destroyMethod().....
```

### 自动装配

根据指定装配规则（属性名称或者属性类型），Spring会自动将匹配的属性值进行注入

```xml
<!-- 实现自动装配
        1.bean标签属性autowire，配置自动装配
        2.autowire属性常用的两个值：
              byName：根据属性名称注入
              byType：根据属性类型注入
-->
<!--    <bean id="employee" class="autowire.Employee">-->
<!--        <property name="department" ref="department"></property>-->
<!--    </bean>-->

<!--    <bean id="employee" class="autowire.Employee" autowire="byName">-->
<!--    </bean>-->

<bean id="employee" class="autowire.Employee" autowire="byType">
</bean>

<bean id="department" class="autowire.Department"></bean>
```

```java
@Test
public void testAutowire() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("autowire.xml");
  //2.获取通过配置创建的对象
  autowire.Employee employee = context.getBean("employee", autowire.Employee.class);
  //获取到对象并使用
  System.out.println(employee);

  //手动让Bean实例销毁
  ((ClassPathXmlApplicationContext)context).close();
}
```

### 外部属性文件

以数据库配置信息为例，配置数据库连接池Druid，引入外部属性文件配置数据库连接池

引入Druid的jar包

```xml
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
  <version>1.2.5</version>
</dependency>
```

jdbc.properties

```properties
prop.driverClass=com.mysql.jdbc.Driver
prop.url=jdbc:mysql://localhost:3306/musicianclub
prop.userName=root
prop.password=*********
```

将properties引入到Spring的配置文件当中，首先加入context标签



