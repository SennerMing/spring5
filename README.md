

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

使用new关键词进行对象的创建，假如以后要换一种实现的方式，那么就得去new新的实现对象进行使用，这样的话对代码的维护，会特别难，那么使用工厂模式，将所有的实现交由工厂进行创建、管理与分配，那么就将使用者与业务逻辑进行了解耦，从而降低了代码维护的成本

### 手写工厂

对象创建的方式：

1. 直接调用构造方法，创建对象

2. 通过反射的形式，创建对象

   Class clazz = Class.forName("club.musician.basic.User");

   User user = (User)clazz.newInstance();

从上面的反射的形式，进行对象的获取，就可以看出，我们已经可以将耦合降低到一个”全限定名“的字符串了，那么我们完全可以将这些”全限定名“的字符串放入一个文件中，进行单独的管理

```java
package concept;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BeanFactory {

    //那么，我们要将properties中的内容读取到我们的这个Properties中
    //对资源的读取我们一般都是用IO流进行读取，像这种静态的资源，我们一般都在类初始化的时候进行内容的读取
    private static Properties env = new Properties();

    static{
        /**
         * 那么将耦合统一转移到了我们这个 资源文件夹，resources下面的my-application.properties中了
         * 第一步，获取IO输入流
         * 第二步，将properties中的内容封装到Properties中，供我们的后续使用
         */
        InputStream inputStream = TestAnimal.class.getResourceAsStream("/my-application.properties");
        try {
            env.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Animal getAnimal() {
        /**
         * 我们将耦合，转移到一个小小的"全限定名"的字符串上了
         * 那，我们完全可以将这些个"全限定名"的字符串转移到一个单独的资源文件中进行统一的管理
         */
        Class clazz = null;
        try {
            clazz = Class.forName(env.getProperty("animal"));
            Animal animal = (Animal) clazz.newInstance();
            animal.setAge(1);
            animal.setName("SennerMing");
            System.out.println(animal);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }
}
```

my-application.properties

```properties
#耦合已经统一转移到了这个小小的配置文件中了
#我们这个properties就相当于Map 形式[{key:key1,value:value1},{key:key2,value:value2}...]
#后续，我们就可以通过properties内置方法通过key进行value的获取，也就是对"全限定名的读取"
animal=concept.Animal
```

那么以后我们的实现类他换了实现方式（代码逻辑变了），后续我怎么弄？我们只需要改我们配置文件中对应可key-value（全限定名）就行了！目的就是一个，解耦！

但是问题来了，我们代码中还有别的代码需要进行同样的修改：

```java
package concept;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BeanFactory {

    //那么，我们要将properties中的内容读取到我们的这个Properties中
    //对资源的读取我们一般都是用IO流进行读取，像这种静态的资源，我们一般都在类初始化的时候进行内容的读取
    private static Properties env = new Properties();

    static{
        /**
         * 那么将耦合统一转移到了我们这个 资源文件夹，resources下面的my-application.properties中了
         * 第一步，获取IO输入流
         * 第二步，将properties中的内容封装到Properties中，供我们的后续使用
         */
        InputStream inputStream = BeanFactory.class.getResourceAsStream("/my-application.properties");
        try {
            env.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Animal getAnimal() {
        /**
         * 我们将耦合，转移到一个小小的"全限定名"的字符串上了
         * 那，我们完全可以将这些个"全限定名"的字符串转移到一个单独的资源文件中进行统一的管理
         */
        Animal animal = null;
        Class clazz = null;
        try {
            clazz = Class.forName(env.getProperty("animal"));
            animal = (Animal) clazz.newInstance();
            animal.setAge(1);
            animal.setName("SennerMing");
            System.out.println(animal);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return animal;
    }

    public static AnimalDao getAnimalDao() {
        AnimalDao animalDao = null;

        try {
            Class clazz = Class.forName(env.getProperty("animalDao"));
            animalDao = (AnimalDao) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return animalDao;
    }

    public static void main(String[] args) {
        AnimalDao animalDao = BeanFactory.getAnimalDao();
        System.out.println(animalDao);
    }
}
```

这个问题就来了啊，获得Animal我们写了一个函数，获取AnimalDao我们又写了一个方法，这个代码也太冗余了

那么我们可以对代码进行改进

```java
public static <T> T getBean(String name, Class clazz) {
  T obj = null;
  try {
    Class clazz1 = Class.forName(env.getProperty(name));
    obj = (T) clazz1.newInstance();
  } catch (ClassNotFoundException e) {
    e.printStackTrace();
  } catch (IllegalAccessException e) {
    e.printStackTrace();
  } catch (InstantiationException e) {
    e.printStackTrace();
  }
  return obj;
}
```

测试代码

```java
public static void main(String[] args) {
  Animal animal = BeanFactory.getBean("animal",Animal.class);
  System.out.println(animal);
}
```

这样我们就对通用的工厂进行改进完毕了，Spring框架已经为我们准备好了这样的工厂，我们直接用就行了，很nice！

### 小结

Spring本质：工厂ApplicationContext（application-context.xml）

## 项目搭建

### 开发前准备

导入基础包，context、额外的commons-logging

```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-context -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-context</artifactId>
  <version>5.3.6</version>
</dependency>
<!-- https://mvnrepository.com/artifact/commons-logging/commons-logging -->
<dependency>
  <groupId>commons-logging</groupId>
  <artifactId>commons-logging</artifactId>
  <version>1.2</version>
</dependency>
```

### Spring的配置文件

```markdown
1.配置文件存放的位置：任意位置，没有硬性的要求
2.配置文件的命名：没有硬性要求，Spring建议的名字是 applicationContext.xml
思考：日后应用Spring框架时，需要进行配置文件路径的设置
```

### Spring的核心API

ApplicationContext

```markdown
作用：Spring提供的ApplicationContext这个工厂，用于对象的创建
好处：解耦合
```

- ApplicationContext是接口类型

  ```markd
  接口：屏蔽实现的差异
  场景：
  	1.非Web环境-ClassPathXmlApplicationContext；FileSystemXmlApplicationContext
  			main函数中，Junit测试当中
  	2.Web环境：XmlWebApplicationContext
  ```

- ApplicationContext是一个重量级的资源

  ```markdown
  ApplicationContext的工厂会占用大量的内存
  不会频繁的创建对象：一个应用只会创建一个工厂对象
  ApplicationContext工厂：会出现多用户多线程并发的访问，是线程安全的
  ```

  

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

- 注解的格式，@注解名称(属性名称=属性值,属性名称=属性值....)
- 使用注解，注解作用在类上面、方法上面、属性上面
- 使用注解的目的：简化Xml的配置，更加优雅

#### 创建Bean对象

Spring针对Bean管理中创建对象提供的注解，四种注解：

1. @Component
2. @Service
3. @Controller
4. @Repository

*上面四个注解的功能是一样的，都可以用来创建Bean的实例，分成四种，主要是让Programer更好的区分当前Bean在程序中所扮演的角色

示例：

1.首先引入AOP的依赖

```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-aop -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aop</artifactId>
    <version>5.3.6</version>
</dependency>
```

2.开启组件的扫描

现在Spring配置文件中加入context标签，配置包扫描

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!-- 开启组件扫描 -->
<!--    <context:component-scan base-package="annotation.dao annotation.service"></context:component-scan>-->
<!--    <context:component-scan base-package="annotation.dao,annotation.service"></context:component-scan>-->

    <!--
        可以写较上层的目录
     -->
    <context:component-scan base-package="annotation"></context:component-scan>
</beans>
```

```java
package annotation.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

//等价于<bean id="userService" class="annotion.service.UserService"></bean>
//这个value可以不写，那么默认的就是首字母小写的 userService 驼峰命名法
//下面几个注解都可以进行Bean的创建
//@Component(value="userService")
//@Service(value="userService")
//@Controller(value="userService")
@Repository(value="userService")
public class UserService {
    public void add() {
        System.out.println("UserService add()....");
    }
}

```

进行测试

```java
@Test
public void testAnnotation() {
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("annotation.xml");
  //2.获取通过配置创建的对象
  annotation.service.UserService userService = context.getBean("userService", annotation.service.UserService.class);
  //获取到对象并使用
  userService.add();

  //手动让Bean实例销毁
  ((ClassPathXmlApplicationContext)context).close();
}
```

高阶用法

```xml
<!-- 示例
        use-default-filters="false"，表示现在不在使用默认的filter，自己配置filter
        context:include-filter,设置扫描那些内容

        下面一大串xml的意思是：
        在annotation包下，并不是去扫描所有的注解的类，而是去扫描带@Controller的注解进行扫描
    -->
<context:component-scan base-package="annotation" use-default-filters="false">
  <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>

<!-- 跟上面的正好相反，除了@Controller其他的都进行扫描 -->
<context:component-scan base-package="annotation">
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

#### 属性注入

##### Autowired

1. @Autowired：根据属性类型进行自动装配

   ```java
   public class UserService {
   
       //定义Dao类型的属性
       //不需要添加set的方法
       //直接添加属性注解就行
       @Autowired
       private UserDao userDao;
   
       public void add() {
           System.out.println("UserService add()....");
           userDao.add();
       }
   }
   ```

   ```java
   package annotation.dao;
   
   import org.springframework.stereotype.Repository;
   
   @Repository
   public class UserDaoImpl implements UserDao{
       @Override
       public void add() {
           System.out.println("UserDao add() ......");
       }
   }
   ```

   ```java
   @Test
   public void testAnnotation() {
     //1.加载我们写的Spring的xml配置文件
     ApplicationContext context = new ClassPathXmlApplicationContext("annotation.xml");
     //2.获取通过配置创建的对象
     annotation.service.UserService userService = context.getBean("userService", annotation.service.UserService.class);
     //获取到对象并使用
     userService.add();
   
     //手动让Bean实例销毁
     ((ClassPathXmlApplicationContext)context).close();
   }
   ```

##### Qualifier

1. @Qualifier：根据属性名称进行注入

   （1）在使用@Autowire自动注入的时候，加上@Qualifier(“test”)可以指定注入哪个对象；
   （2）可以作为筛选的限定符，我们在做自定义注解时可以在其定义上增加@Qualifier，用来筛选需要的对象

   对第一条的理解：

   ```java
   //我们定义了两个TestClass对象，分别是testClass1和testClass2
   //我们如果在另外一个对象中直接使用@Autowire去注入的话，spring肯定不知道使用哪个对象
   //会排除异常 required a single bean, but 2 were found
   @Configuration
   public class TestConfiguration {
      @Bean("testClass1")
      TestClass testClass1(){
          return new TestClass("TestClass1");
      }
      @Bean("testClass2")
      TestClass testClass2(){
          return new TestClass("TestClass2");
      }
   }
   ```

   ```java
   @RestController
   public class TestController {
   
       //此时这两个注解的连用就类似 @Resource(name="testClass1")
       @Autowired
       @Qualifier("testClass1")
       private TestClass testClass;
   
       @GetMapping("/test")
       public Object test(){
           return testClassList;
       }
   
   }
   ```

   @Autowired和@Qualifier这两个注解的连用在这个位置就类似 @Resource(name=“testClass1”)

   对第二条的理解：

   ```java
   @Configuration
   public class TestConfiguration {
       //我们调整下在testClass1上增加@Qualifier注解
     	@Qualifier
       @Bean("testClass1")
       TestClass testClass1(){
           return new TestClass("TestClass1");
       }
   
       @Bean("testClass2")
       TestClass testClass2(){
           return new TestClass("TestClass2");
       }
   }
   ```

   ```java
   @RestController
   public class TestController {
       //我们这里使用一个list去接收testClass的对象
       @Autowired
       List<TestClass> testClassList= Collections.emptyList();
       
       @GetMapping("/test")
       public Object test(){
           return testClassList;
       }
   }
   ```

   ```json
   我们调用得到的结果是
   [
        {
           "name": "TestClass1"
        },
       {
          "name": "TestClass2"
       }
   ]
   ```

   在Controller的List中增加注解

   ```java
   @RestController
   public class TestController {
   
       @Qualifier //我们在这增加注解
       @Autowired
       List<TestClass> testClassList= Collections.emptyList();
   
       @GetMapping("/test")
       public Object test(){
           return testClassList;
       }
   }
   ```

   和上面代码对比就是在接收参数上增加了@Qualifier注解，这样看是有什么区别，我们调用下，结果如下：

   ```json
   [
        {
           "name": "TestClass1"
        }
   ]
   ```

   返回结果只剩下增加了@Qualifier注解的TestClass对象，这样我们就可以理解官方说的标记筛选是什么意思了。
   另外，@Qualifier注解是可以指定value的，这样我们可以通过values来分类筛选想要的对象了，这里不列举代码了，感兴趣的同学自己试试。

##### Resource

1. @Resource：可以根据类型注入，也可以根据名称进行注入

   ```java
   public class UserService {
       //定义Dao类型的属性
       //不需要添加set的方法
       //直接添加属性注解就行
   //    @Autowired
   //    @Qualifier(value = "userDaoImpl1")
   //    private UserDao userDao;
     
     	//都可以进行注入 他是javax包里面的注解
   		//@Resource 
       @Resource(name = "userDaoImpl1")
       private UserDao userDao;
   
       public void add() {
           System.out.println("UserService add()....");
           userDao.add();
       }
   }
   ```

##### value

1. @Value：注入普通类型属性

   ```java
   @Value(value = "SennerMing")
   private String name;
   ```

#### 完全注解开发

第一步，创建配置类，意思就是替换那个Xml配置文件

```java
//作为配置类，用以替代Xml配置文件，之前在annotation.xml中不是加了一个包扫描嘛？
//这个就不用了，直接写在配置类上就行了！
@Configuration
@ComponentScan(basePackages = {"annotation"})
public class SpringConfig {
}
```

第二步，测试的方式要改变了

```java
@Test
public void testFullAnnotation() {
  //1.加载我们写的SpringConfig.class配置类
  ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
  //2.获取通过配置创建的对象
  annotation.service.UserService userService = context.getBean("userService", annotation.service.UserService.class);
  //获取到对象并使用
  userService.add();

  //手动让Bean实例销毁
  ((AnnotationConfigApplicationContext)context).close();
}
```

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

## AOP相关

面向切面编程，利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各个部分各个部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。