# Spring Framework 5 学习

## 1.相关概念

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

### 1.1 手写工厂

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

### 1.2 小结

Spring本质：工厂ApplicationContext（application-context.xml）

## 2.项目搭建

### 2.1 开发前准备

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

### 2.2 Spring的配置文件

```markdown
1.配置文件存放的位置：任意位置，没有硬性的要求
2.配置文件的命名：没有硬性要求，Spring建议的名字是 applicationContext.xml
思考：日后应用Spring框架时，需要进行配置文件路径的设置
```

### 2.3 Spring的核心API

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



## 3 程序开发

```markdown
1.创建类型
2.配置问价的配置 - applicationContext.xml
3.通过工厂类，获得对象
	ApplicationContext
			|- ClassPathXmlApplicationContext
```

applicationContext.xml

```xml
<!--
        1.id属性名字（唯一）
        2.class需要全限定名
     -->
<bean id="animal" class="concept.Animal"></bean>
```

通过工厂类获取对象

```java
@Test
public void testConcept() {
  //1.获得Spring ApplicationContext 工厂
  ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
  //2.获取通过配置创建的对象，和我们自己实现的BeanFactory很像
  Animal animal = context.getBean("animal", Animal.class);
  //获取到对象并使用
  System.out.println(animal);

  //手动让Bean实例销毁
  ((ClassPathXmlApplicationContext)context).close();
}
```

### 3.1 细节解析

- 名词解释

  ```markdown
  Spring工厂创建的对象，叫做Bean或者组件（Component）
  ```

- Spring提供的一些工厂方法

  ```java
  @Test
  public void testFactoryMethod() {
    //1.获得Spring ApplicationContext 工厂
    ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
    //2.获取通过配置创建的对象，和我们自己实现的BeanFactory很像
    Animal animal = (Animal) context.getBean("animal");
  
    /**
    * 此时在Spring的Bean工厂里面只有一个Bean标签的类型是Animal的，如果有两个id不同但是class都是Animal的
    * 声明的话，此时运行这个代码会报excepted single matching bean but found 2 : Animal
    * 想使用这个方法的话，必须要确保此时的配置文件中只能有一个Bean的class是Animal类型的
    */
    //        Animal animal1 = context.getBean(Animal.class);
  
    Animal animal2 = context.getBean("animal", Animal.class);
  
    /**
    * 像是applicationContext.xml中的一些Bean标签就是SpringContext so-called BeanDefinitions
    * 实际上就是那些个id值
    */
    String[] beanDefinitionNames = context.getBeanDefinitionNames();
    for (String definitionName : beanDefinitionNames) {
    	System.out.println(definitionName);
    }
  
    /**
    * 根据类型获得其所有的id属性，别名
    */
    String[] beanNames = context.getBeanNamesForType(Animal.class);
    for (String beanName : beanNames) {
    	System.out.println(beanName);
    }
  
    /**
    * 用于判断是否具有id值得BeanDefinition
    */
    boolean exist = context.containsBeanDefinition("animal");
    System.out.println(exist);
  
    /**
    * 用于判断是否存在指定id的bean
    */
    boolean exist1 = context.containsBean("animal");
    System.out.println(exist1);
  
    //获取到对象并使用
    //        System.out.println(animal);
  
    //手动让Bean实例销毁
    ((ClassPathXmlApplicationContext)context).close();
  }
  ```

### 3.2 配置相关细节

#### 3.2.1 ID属性

能不能不写id呢？

```xml
<bean class="concept.Animal"></bean>
```

测试使用ApplicationContext进行Bean的获取，完全是可以获取到的

那么，Spring的Context到底有没有为这个Bean进行name的设置呢？

```java
@Test
public void testAnimal() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
  Animal animal = applicationContext.getBean(Animal.class);

  String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
  for (String name : beanDefinitionNames) {
    System.out.println(name);
  }
  System.out.println(animal);
}
//打印结果：
//	concept.Animal#0
//	Animal{name='null', age=0}
```

```markdown
那么上面这种配置 
没有id值，那么Spring会通过某种算法为我们自动给他取了个名字 concept.Animal#0
应用场景：
	|- 如果这个bean只需要使用一次，那么就可以省略id的值
	|- 如果这个bean会使用多次，或者被其他Bean进行引用，则需要设置id的值
```

#### 3.2.2 Name属性

作用：用于在Spring配置文件中，为Bean进行别名的定义，id如现实生活中的人的大名，那么Name就是现实生活中的小名

```xml
<bean id="animal" name="ani" class="concept.Animal"></bean>
```

```java
@Test
public void testName() {
  ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
  Animal animal = (Animal) context.getBean("ani");
  System.out.println(animal);
}
```

```markdown
通过上述实验，都可以进行Bean的获取
	相同：
			1.ctx.getBean("id|name");都可以进行获取
			2.<bean id="" class=""></bean> <bean name="" class=""></bean>这样定义，是等效的
	区别：
  		1.name可以定义多个<bean name="ani1,ani2"></bean>，id只能有一个值
  		2.Xml中，作为XML语言，在历史的长河中，对于id属性的值，命名是有要求的：必须以字母开头，后面可以跟字					母、数字、下划线、	连字符，不能以特殊字符开头（/animal什么的），而name属性值，可以随便写（/animal				是可以的），这样的话name会应用到特殊命名的场景下，在Struts中，name必须就以反斜线开头。
  			
  			现在这个问题不存在了，我们的id也可以随便写了，优先使用id
  		3.代码上的区别：
  			ctx.containsBeanDefinition();//他只能判断id不能判断name
  			ctx.containsBean();	//他既能判断id又能判断name
```

#### 3.2.3 Spring框架浅析

1. Spring框架通过ClassPathXmlApplicationContext工厂读取配置文件applicationContext.xml
2. Spring框架获得Bean标签的相关信息 id的值 animal class的值 concept.Animal
3. 通过反射创建对象，Class<?> clazz = Class.forName(class的值);id的值 = clazz.newInstance();
4. 反射穿件对象底层也是会调用对象自己的构造器
   - Class<?> clazz = Class.forName(class的值); id的值 = clazz.newInstance();
   - 上面的代码其实等效于Animal animal = new Animal()；

那么我们试着把Animial的构造器设置为私有的，private，看看Spring会不会报错啊？

运行的结果，但是还是可以运行成功的！那么人家的反射肯定是用了setAccessible=true啦~

##### 3.2.3.1 相关问题

```markdown
1.未来在开发的过程中，是不是所有的对象，都会交由spring工厂来创建的呢？
答：理论上是的，但是有特例：实体对象（Entity），这类的对象，是不会交由spring创建的，是由持久层框架进行创建的，因为其需求被填充数据，而数据是在“数据库”中存在的
```

## 4 注入（Injection）

### 4.1 什么是注入

通过Spring工厂及配置文件，为所创建的对象或成员变量进行赋值

### 4.2 为什么需要注入

通过编码的方式，为成员变量进行赋值，存在耦合！

如何进行注入【开发步骤】

1. 要为成员变量创建setter and getter方法
2. 配置Spring的配置文件

### 4.3 Bean注入的学习

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

#### 4.3.1 注入的底层原理

IOC底层原理

- XML解析、工厂模式、反射技术

Spring注入的工作原

```markdown
1.<bean id="animal" class="xxx.Animal"></bean>等效于Animal animal = new Animal();
2.<property name="name" value="SennerMing"></proeprty>等效于animal.setName("SennerMing");
3.<property name="password" value="123456"></property>等效于animal.setPassword("123456");
通过配置文件使用反射技术进行属性值的注入
```

#### 4.3.2 Set注入的分析

set注入涉及的成员变量有很多种，甭管你什么类型那都需要调用setter方法，也就是要嵌套到property标签里面！

- JDK类型的还成员变量
  - 8种基本类型+String
  - 数组类型
  - set集合
  - list集合
  - map集合
  - properties集合
- 用户自定义类型

#### 4.3.3 初识Spring的Bean工厂

IOC接口（BeanFactory）

IOC思想就是基于IOC容器完成的，IOC容器底层就是对象工厂

Spring提供的IOC容器的实现方式（两个接口）

- BeanFactory

```java
//也就是说上面的ApplicationContext可以换成BeanFactory，但是咱么这边不推荐亲使用呢，因为这个玩意是Spring IOC容器的最基本的实现方式，Spring的内置IOC容器，它不面向咱们programer呢
@Testpublic void testAdd() {
  //1.加载我们写的Spring的xml配置文件，这样的方式加载配置文件，他并不会创建对象，只有在获取或使用的时候才会去创建对象，这可能就是我们常说的懒加载吧  
  BeanFactory context = new ClassPathXmlApplicationContext("beans.xml");
  //2.获取通过配置创建的对象  User user = context.getBean("user", User.class); 
  //获取到对象并使用  
  System.out.println(user);
  user.add();
}
```

- ApplicationContext：他是BeanFactory的一个子接口，功能更为强大，面向咱们程序猿👨🏻‍💻的，他和BeanFactory就不一样了，他一加载XML方式，他就把对象创建了，不同的实现类
  - FileSystemXmlApplicationContext：对应的系统盘资源
  - ClassPathXmlApplicationContext：对应的src下的资源

### 4.4 Bean的XML配置

IOC操作Bean管理

Bean管理是由Spring完成对象的创建，并对属性进行装配

IOC操作Bean管理（基于Xml）

- 基于Xml方式进行对象创建
  - id：给创建的对象起一个别名，是一个唯一的标识
  - name：也可以作为一个唯一标识（为struts搭配使用），与id不同可以加一些特殊符号
  - class：创建对象的类的全路径名
  - 默认使用的是Java对象的无参构造

#### 4.4.1 简单注入

基于Xml方式进行属性的注入

DI：依赖注入，就是注入属性

使用set方式进行属性注入

```java
package basic;

public class Book {

    private String name;
    private String author;
    private String address;

    public Book() {
    }

    public Book(String name, String author, String address) {
        this.name = name;
        this.author = author;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", address='" + address + '\'' +
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
  <!--这种简化的写法，只应用于8种基本类型+String-->  
  <property name="author" value="欧阳锋"></property>
</bean>
```

```java
@Testpublic void testBook() {  
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");  
  //2.获取通过配置创建的对象  
  Book book = context.getBean("book", Book.class);  
  //获取到对象并使用  
  System.out.println(book);
}
```

#### 4.4.2 构造器注入

```markdown
注入：通过Spring的配置文件，为成员变量赋值Set注入：Spring调用Set方法，通过配置文件为成员变量赋值构造注入：Spring调用构造方法，通过配置文件为成员变量赋值	开发步骤：			|- 同样的我们得提供有参构造器			|- 提供Spring的配置文件
```

使用有参构造器进行属性注入

```java
package basic;

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
@Testpublic void testOrder() {  
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml"); 
  //2.获取通过配置创建的对象  
  Order order = context.getBean("order", Order.class);  
  //获取到对象并使用  
  System.out.println(order);
}
```

##### 4.4.2.1 构造注入的重载

```markdown
构造参数个数不同的时候，我们可以通过控制<constructor-arg>标签的数量进行匹配
```

```java
//但是如果构造器有多个，我想使用第二个构造器的时候
private String name;
private int age;
public Order(String name) {
  this.name = name;
}
public Order(int age) {
  this.age = age;
}
public Order(String name, int age)
{
  this.name = name;
  this.age = age;
}
```

```markdown
//可以通过在<constructor-arg type="int">，进行指定构造参数的调用
```

#### 4.4.3 p标签使用

**p名称空间的注入，可以简化基于xml的配置方式**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       xmlns:p="http://www.springframework.org/schema/p"       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
<bean id="book" class="basic.Book" p:name="易筋经" p:author="纪晓岚"></bean> 
  <!-- 其他写法 -->  
<bean id="userService" class="annotation.service.UserService" p:userDao-ref="userDao"></bean>
</beans>
```

#### 4.4.4 特殊符号注入

其他类型的属性注入

字面量的注入

##### 4.4.4.1 null值

```xml
<!--    set方法进行属性的注入-->
<bean id="book" class="basic.Book">
 <!--  使用Property完成属性的注入-->
  <!--        name:类里面的属性名称-->
  <!--        value:向属性中注入的值-->
  <property name="name" value="九阳真经"></property>
  <property name="author" value="欧阳锋"></property>
  <property name="address">
   <!-- 使用null标签进行空值的设置 --> 
    <null></null>
  </property>
</bean>
```

##### 4.4.4.2 属性值包含一些特殊符号

```xml
 <!-- 属性值包含特殊符号
       1.把<>进行转义 &lt; &gt;
       2.把带特殊符号的内容写到CDATA
 -->
<property name="address">
  <value><![CDATA[<<南京南>>]]></value>
</property>
```

#### 4.4.5 引用类型注入

输入属性

##### 4.4.5.1 外部bean

- 创建service类和dao类

```java
//创建类可以参照 service.UserService、dao.UserDao、dao.UserDaoImpl
```

```xml
<!--创建Dao对象，写不写这个id，就看有没有别人引用它-->
<bean id="userDao" class="dao.UserDaoImpl"></bean>
<!--创建Service对象-->
<bean id="userService" class="service.UserService">
  <!-- name 类里面属性名称 --> 
  <!-- ref 引用类型的值，userDao在Spring容器内id的值-->
  <property name="userDao" ref="userDao"/>
</bean>
```

```java
@Testpublic void testRef() { 
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("beans1.xml");  
  //2.获取通过配置创建的对象  
  UserService userService = context.getBean("userService", UserService.class);
  //获取到对象并使用  
  userService.add();
}
```

##### 4.4.5.2 内部bean

一对多的关系：一个部门下面可以有多个员工，一个员工属于某一个部门

```java
//参照 bean.Department、bean.Employee
```

```xml
 <!-- 外部bean --><!--<bean id="department" class="bean.Department"></bean>-->
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
@Testpublic void testInnerBean() {
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");  
  //2.获取通过配置创建的对象  
  Employee employee = context.getBean("employee", Employee.class);
  //获取到对象并使用  
  System.out.println(employee);
}
```

##### 4.4.5.3 级联赋值

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

```markdown
#Spring4.x就废除了 <ref local="本配置文件的bean对象"/> 基本等效于 <ref bean="本容器和父容器的bean对象"/>
```

#### 4.4.6 集合类型注入

注入集合属性

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
      <!-- <entry>-->
      <!--      <key><ref bean=""></ref></key>--><!--      <ref bean=""></ref>--><!-- </entry>-->
    </map>  
  </property>  
  <property name="set">
    <set value-type="java.lang.String">
      <value>Mysql</value>
      <value>Redis</value>
    </set>  
 </property>
</bean>
```

```java
@Testpublic void testCollection() {  
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

#### 4.4.7 util标签使用

使用util工具进行配置

```xml
<?xml version="1.0" encoding="UTF-8"?><beans xmlns="http://www.springframework.org/schema/beans"       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       xmlns:util="http://www.springframework.org/schema/util"       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd       http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd">    
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
@Testpublic void testDistrict() {  
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("collection1.xml");  
  //2.获取通过配置创建的对象  
  District district = context.getBean("district", District.class);
  //获取到对象并使用  
  System.out.println(district);
}
```

#### 4.4.8 FactoryBean

IOC操作Bean管理（FactoryBean）

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
public class ProductionFactory implements FactoryBean<Production> 
{    
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
@Testpublic void testFactory() {  
  //1.加载我们写的Spring的xml配置文件
  ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");  
  //2.获取通过配置创建的对象  
  Production production = context.getBean("production", Production.class);
  //获取到对象并使用  
  System.out.println(production);
}
```

#### 4.4.9 Properties注入

```xml
<property name="properties">
  <props>    
    <prop key="pk1">pv1</prop> 
    <prop key="pk2">pv2</prop> 
    <prop key="pk3">pv3</prop>  
  </props>
</property>
```

#### 4.4.10 复杂JDK类型（Date）

留到后续进行处理

### 4.5 XML注入小结

```markdown
在未来的实际工作当中，是应用set注入还是构造注入呢？答案：应用set注入会更多		|- 构造注入比较麻烦（重载）		|- Spring框架的底层，大量应用了set注入
```

| 注入类型   | 对应类中   | 对应配置                      | 字段类型            | 类型                                                       |
| ---------- | ---------- | ----------------------------- | ------------------- | ---------------------------------------------------------- |
| 构造注入   |            |                               |                     | 8种基本类型+Spring                                         |
|            | 构造方法   |                               |                     | 数组类型                                                   |
|            |            | <constructor-arg>             | JDK内置类型         | Set集合、List集合、Map集合、Properties集合                 |
| ---------- | ---------- | ----------------------------- | ------------------- | ---------------------------------------------------------- |
|            |            | <property name="">            | 用户自定义类型      | 自定义类型                                                 |
|            | Set方法    |                               |                     |                                                            |
| Set注入    |            |                               |                     |                                                            |

## 5 Bean注解

IOC操作Bean管理（Bean的作用域）

1、在Spring里面，设置创建Bean实例是单例还是多例

2、在Spring里面，默认的情况下创建的Bean是一个单实例对象

```java
//运行以下代码
@Testpublic void testDistrict() {
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("collection1.xml");  
  //2.获取通过配置创建的对象  
  District district1 = context.getBean("district", District.class);  
  District district2 = context.getBean("district", District.class);  
  //获取到对象并使用  
  System.out.println(district1);  
  System.out.println(district2);}
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
<bean id="district" class="collection.District" scope="prototype">  <property name="units" ref="units"></property></bean>
```

```java
//再次运行上面的testDistrict()方法，可以看到，两次返回的对象地址不同了
/**	
collection.District@776aec5c
collection.District@1d296da
*/
```

还有request和session作用域，request：每次请求；session：一次会话

### 5.1 Bean的注解配置

- 注解的格式，@注解名称(属性名称=属性值,属性名称=属性值....)
- 使用注解，注解作用在类上面、方法上面、属性上面
- 使用注解的目的：简化Xml的配置，更加优雅

### 5.2 四种注解

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
<?xml version="1.0" encoding="UTF-8"?><beans xmlns="http://www.springframework.org/schema/beans"       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       xmlns:context="http://www.springframework.org/schema/context"       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd       http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">
  <!-- 开启组件扫描 -->
  <!--    <context:component-scan base-package="annotation.dao annotation.service"></context:component-scan>-->
  <!--    <context:component-scan base-package="annotation.dao,annotation.service"></context:component-scan>-->
  <!--        可以写较上层的目录     -->    
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
@Testpublic void testAnnotation() {
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
</context:component-scan><!-- 跟上面的正好相反，除了@Controller其他的都进行扫描 -->

<context:component-scan base-package="annotation">
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

### 5.3 属性注入

#### 5.3.1 Autowired

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

#### 5.3.2 Qualifier

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
     @Qualifier 
     //我们在这增加注解
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

#### 5.3.3 Resource

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

#### 5.3.4 value

1. @Value：注入普通类型属性

   ```java
   @Value(value = "SennerMing")
   private String name;
   ```

### 5.4 完全注解开发

第一步，创建配置类，意思就是替换那个Xml配置文件

```java
//作为配置类，用以替代Xml配置文件，之前在annotation.xml中不是加了一个包扫描嘛？//这个就不用了，直接写在配置类上就行了！
@Configuration
@ComponentScan(basePackages = {"annotation"})
public class SpringConfig {}
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

## 6 Bean的生命周期

Bean的生命周期是描述从对象创建到对象销毁的过程

Bean的生命周期

1. 通过构造器创建bean实例（无参构造器）
2. 为Bean的属性设置值和对其他Bean的引用（调用set方法）
3. 调用Bean的初始化的方法（需要进行相应的配置）
4. Bean可以进行的使用（获取对象）
5. 当容器关闭,调用Bean的销毁方法（需要进行配置销毁的方法）

### 6.1 基本初始化流程

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
  ((ClassPathXmlApplicationContext)context).close();}
/**	执行的结果：
执行了无参数的构造器.....
进行Person的name的设置
进行Person的age的设置			
Person的initMethod().....
Person{name='Senner Ming', age=18}
Person的destroyMethod().....
*/
```

### 6.2 后置处理

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
ß}
```



```java
/**
最终打印结果:

执行了无参数的构造器.....
进行Person的name的设置
进行Person的age的设置
在Bean initMethod()之前执行的方法.....
Person的initMethod().....
在Bean initMethod()之后执行的方法.....
Person{name='Senner Ming', age=18}
Person的destroyMethod().....
*/
```

### 6.3 自动装配

根据指定装配规则（属性名称或者属性类型），Spring会自动将匹配的属性值进行注入

```xml
<!-- 实现自动装配
        1.bean标签属性autowire，配置自动装配
        2.autowire属性常用的两个值：
              byName：根据属性名称注入
              byType：根据属性类型注入
-->
<!--    
<bean id="employee" class="autowire.Employee">  
<property name="department" ref="department"></property>
</bean>
-->
<!--<bean id="employee" class="autowire.Employee" autowire="byName"></bean>-->
<bean id="employee" class="autowire.Employee" autowire="byType"></bean>
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



## 7 AOP相关

面向切面编程，利用AOP可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各个部分各个部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。



## 8 反转控制 与 依赖注入

### 8.1 IOC

```markdown
1.反转（转移）控制（IOC Inverse Of Controller）
|- 控制：对于成员变量赋值的控制权
之前是直接代码中完成对成员变量的赋值，那么队成员变量赋值的控制权 = 代码 （显而易见耦合度高）
现在，对于成员变量赋值的控制权 = Spring配置文件 + Spring工厂 （显而易见的解耦合）    
|- 好处： 解耦合了    
|- 底层实现：工厂设计模式
```

### 8.2 DI

```markdown
1.注入：通过Spring的工厂及配置文件,为对象(Bean，组件)的成员变量赋值
2.依赖：理念，当一个类需要另一个类时，就产生了对其的依赖，就可以将另个一类，作为成员变量，最终通过Spring的配置文件进行注入（赋值）目的还是为了解耦合
```

## 9 Spring工厂的复杂对象

有复杂对象那么就有简单对象，简单对象可以暂时定义为，可以通过new构造方法进行创建的对象，复杂对象则相反

```markdown
1.简单对象(直接new构造）：
类似于我们之前创建的XxxService、XxxDao、XxxEntity
2.复杂对象(需要一些标准配置的对象)：
像是我们之前创建的数据库Connection啊，SqlSessionFactory
Class.forName("com.mysql.jdbc.Driver")
conn = DriverManager.getConnection();
InputStream inputStream = Resource.getResourceAsStream()		
new SqlSessionFactoryBuilder().build(inputStream)
```

### 9.1 FactoryBean接口

#### 9.1.1 开发步骤

- 实现FactoryBean的接口

  ```markdown
  1.getObject() 
  //用于书写创建复杂对象的代码，并把复杂对象作为方法的返回值返回
  Connection				
  Class.forName("com.mysql.jdbc.Driver");
  Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SM","root","password")
  return conn;		
  
  SqlSessionFactory
  InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");
  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
  
  2.getObjectType() 
  //返回所创建复杂对象的Class对象	
  Connection.class		SqlSessionFactory.class
  
  3.isSingleton() 
  //返回true的话，只需要创建一次，返回false的话，那么每一次调用，都需要创建一个新的复杂对象
  ```

  参考代码factory.ConnectionFactoryBean

- Spring配置文件的配置

  ```xml
  <bean id="conn" class="factory.ConnectionFactoryBean"></bean>
  ```

  先对简单对象的再次理解

  ```markdown
  简单对象		
  <bean id="user" class="xxx.xx.User"></bean>		
  我们通过applicationContext.getBean("user")获得的就是这个User的类对象那么复杂对象（FactoryBean）
  <bean id="conn" class="factory.ConnectionFactoryBean"></bean>		
  错误的认知：				
  applicationContext.getBean("conn")获得的是ConnectionFactoryBean这个类对象
  正确的认知：
  applicationContext.getBean("conn")获得的是ConnectionFactoryBean创建的复杂对象Connection
  ```
  
  参考resources/factory.xml
  
  ```markdown
  1.注意：那么我们就是想获得这个ConnectionFactoryBean的这个类对象呢？
  |- applicationContext.getBean("&conn");
  
  2.isSingleton:		
  返回true的时候只会创建一个复杂对象		
  返回false的时候每次都会创建新的对象		
  
  3.那上面我们的代码中isSingleton(){return false;}		
  1).我们在测试代码中连续获得两次这个conn对象				
  TestSpring.testFactory();				
  //打印结果
  com.mysql.cj.jdbc.ConnectionImpl@6f19ac19
  com.mysql.cj.jdbc.ConnectionImpl@119cbf96				
  
  2).那我们如何决定这个isSingleton()是返回true或者返回false呢？
  答：如果能被大家共用的话，我们就返回true，如果不能被共用，那么我们就设置为false，以本案例来看，那我
  们的数据库连接能不能被共用呢？那肯定不行嘛！
  那么对于SqlSessionFactory作为单例就很合适了。
  
  4.运行时，报SSL warning，在数据库连接串后面加上?useSSL=false		
  jdbc:mysql://localhost:3306/DBname?useSSL=false						
  ```

依赖注入的体会（DI）
		像是Connection这些个驱动名，连接串，用户名密码都是创建数据库连接的必备参数
		factory.ConnectionBeanFactory改：

```java
public class ConnectionFactoryBean implements FactoryBean<Connection> {
  private String driverClassName;    
  private String url;    
  private String userName;    
  private String password;
  //....
}
```

factory.xml改成介个亚子：

```xml
<bean id="conn" class="factory.ConnectionFactoryBean">  
  <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"></property>  
  <property name="url" value="jdbc:mysql://localhost:3306/musicianclub"></property>
  <property name="userName" value="root"/>  
  <property name="password" value="xxxxxxx"/>
</bean>
```



```markdown
你看看，把ConnectionFactoryBean中依赖的四个字符串信息，进行配置文件的注入还是为了解耦合啊！
```



#### 9.1.2 FactoryBean的实现原理[简易版] 

```markdown
1.为什么Spring要规定一个FactoryBean接口，让我们去实现他，调用getObject()方法去获得Bean对象呢？
2.为什么applicationcontext.getBean("conn");获得的是复杂对象Connection而不是获得ConnectionFactoryBean(&)呢？
接口加反射，唉，是什么都能做~
```

如何解释呢？

```markdown
问题：
一、那么这行代码：Connection conn = ctx.getBean("conn");		
其实Spring内部是做了三个步骤		
1.根据conn获得<bean>标签相关的信息，并判断 instanceOf(FactoryBean) 是不是FactoryBean的子类啊

2.那么判断出来是的话，根据人家的接口规范，那肯定是返回对象的话，人家Spring肯定就调用了getObject()调用    

3.那么调用完成，按照你的代码逻辑，进行复杂对象的创建，进行返回了！
```

#### 9.1.3 FactoryBean小结

Spring中用于创建复杂对象的方式之一，也是Spring原生提供的，后续讲解Spring整合其他框架，其中也大量应用FactoryBean

### 9.2 实例工厂

使用实例工厂的原因

```markdown
1.避免Spring框架的侵入		
像是使用FactoryBean的话我们必须要implements FactoryBean

2.整合遗留的系统		
那么我们之前的一些老的系统的话，系统中已经存在了一个ConnectionFactory的这个类，其中有getConnection()方法来获取我们的数据库连接，
（像是只有.class文件）这种情况的下，我们就得使用我们的实例工厂了
```

针对整合遗留系统的开发步骤

1.创建ConnectionFactory

```java
public class ConnectionFactory {
  public Connection getConnection() {
    Connection connection = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost/musicianclub",                    
                                               "root", "******");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }        
    return connection;
  }
}
```

2.Spring配置文件

```xml
<bean id="connectionFactory" class="factory.ConnectionFactory"></bean>
<bean id="conn1" factory-bean="connectionFactory" factory-method="getConnection"></bean>
```

3.测试方法

```java
@Test
public void testFactory() {
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");  
  //2.获取通过配置创建的对象  
  Connection conn = context.getBean("conn1", Connection.class);  
  //获取到对象并使用  
  System.out.println(conn);
}
```



### 9.3 静态工厂

静态工厂，这个创建对象的方法和实例工厂的区别就是，创建对象的方法他是静态的。

```java
package factory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class StaticConnectionFactory {
  public static Connection getConnection() {
    Connection connection = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicianclub",
                                               "root", "*****");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();        
    } catch (SQLException throwables) {
      throwables.printStackTrace();        
    }        
    return connection;
  }
}
```

Spring配置文件

```xml
<!-- 静态工厂 -->
<bean id="staticConn" class="factory.StaticConnectionFactory" factory-method="getConnection"></bean>
```

测试

```java
@Test
public void testFactory() {
  //1.加载我们写的Spring的xml配置文件  
  ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");  
  //2.获取通过配置创建的对象  
  Connection conn = context.getBean("staticConn", Connection.class);  
  //获取到对象并使用  
  System.out.println(conn);
}
```

### 9.4 Spring工厂对象创建小结

```markdown
Spring工厂中存在两种对象
|- 简单对象 ---- <bean id="order">
|- 复杂对象 ---- FactoryBean
实例工厂
静态工厂
====================================	
====================================													   											
↓创建的方式↓
|- 构造注入 
---- <constructor-arg>       
----   JDK内置类型
----  8中基本类型+String 构造也可以ref用户自定义类型
----   数组类型
----		Set集合																															 
----		List集合
----		Map集合																															 
----		Properties集合

|- Set方法
---- <property name="pname"> 
----	用户自定义类型	
----	自定义类型
属性当然也可以使用JDK内置类型
```

## 10 控制Spring工厂创建对象次数

之前FactoryBean中有一个isSingleton()返回的true或者false，可以帮助我们控制通过FactoryBean创建的复杂对象的创建次数，那么我们如何对简单对象的创建次数加以控制呢？

### 10.1 简单对象

Spring的配置

```xml
 <!-- 用于简单对象的scope -->
<bean id="account" scope="singleton" class="scope.Account"></bean>
<bean id="account" scope="prototype" class="scope.Account"></bean>
```

那么我们指定了 scope为 singleton，那么Spring工厂创建的对象的次数就为1，如果prototype，那么每次进行获取，都会创建新的Bean对象，singleton是默认的值。

### 10.2 复杂对象

isSingleton()的方法，实例工厂、静态工厂它没有实现了isSingleton()，那么还是通过scope进行对象创建次数的控制

### 10.3 控制原因

```markdown
有些对象是可以被大家共用的，有的则不，优点就是节省不必要的内存空间的浪费。那么什么样的对象只需要创建一次？
|- SqlSessionFactory		
|- Dao对象		
|- Service

大量都是无状态的哪些对象每次都要创建新的？		
|- 连接对象 关乎事务的		
|- sqlSession 
| Session 不可能被共用		
|- Struts2 Action
```

## 11 对象的生命周期

```markdown
1.什么是生命周期啊？
指的是一个对象的创建、存活、消亡的一个完整的过程
2.为什么要学习对象的生命周期？
在我们传统的java学习当中，new构造，然后对象没有再被引用，那么久等这个被GC掉
那在我们学习框架的过程中，Bean对象是由Spring框架进行控制的，学习对象在其中的生命周期，那么有利于我们学习好Spring框架
```

### 11.1 Bean生命周期的三个阶段

1. 创建阶段

   - scope="singleton"的情况下，那么Spring工厂创建的同时，就会进行对象的创建

   - scope="prototype"的情况下，那么我们从Spring工厂获取对象时，才会进行对象的创建

     代码印证，lifecycle.xml进行配置

     ```xml
     <!-- singleton Spring工厂创建时，Bean对象就会被创建 -->
     <bean id="product" scope="singleton" class="lifecycle.Product"></bean>
     <bean id="product" scope="prototype" class="lifecycle.Product"></bean>
     ```
     
     

     ```java
     //测试代码
     @Test
     public void testProductLife() {  
       ApplicationContext context = new ClassPathXmlApplicationContext("lifecycle.xml");  
       Product product = context.getBean("product", Product.class);  
       System.out.println(product);
     }
     ```
     
     
     
     ```markdown
     如果我的Scope="singleton"，但是我们想他也是在我们获取的时候再进行创建！那么，我们可以对spring配置进行修改
     <bean id="account" scope="singleton" class="scope.Account" lazy-init="true"></bean>
     lazy-init=true的情况下时，那么Bean对象就在获取的时候，才进行创建！
     ```

2. 初始化阶段

   方式一：实现InitializingBean接口

   ```markdown
   Q：什么是初始化阶段？
   A：Spring工厂在创建完对象之后，调用对象的初始化方法，完成对初始化操作		
   1.初始化方法的提供：Programer根据需求，提供初始化方法，最终完成初始化操作		
   2.初始化方法的调用：Spring工厂进行调用
   ```

   我们将我们的Bean进行InitializingBean接口的实现

   afterPropertiesSet()

   ```java
   package lifecycle;
   import org.springframework.beans.factory.InitializingBean;
   public class Product implements InitializingBean {
     public Product() {
       System.out.println("Product().....");    
     }    
     @Override
     public void afterPropertiesSet() throws Exception {
       System.out.println("afterPropertiesSet()......");
     }
   }
   ```

   Spring的配置

   ```xml
   <!-- 用于简单对象的scope --><bean id="account" scope="singleton" class="scope.Account"></bean>
   ```

   测试方法

   ```java
   @Test
   public void testProductLife() {
     ApplicationContext context = new ClassPathXmlApplicationContext("lifecycle.xml");          
     Product product = context.getBean("product", Product.class);  
     System.out.println(product);}
   //执行结果
   //		Product().....
   //		afterPropertiesSet()......
   ```

   这个方式一虽然除了实现InitializingBean的方法就不要做额外的配置了，但是和Spring框架的耦合度太高了

   

   方式二：对象提供的普通方法

   ```java
   //Person的初始化方法
   public void initMethod() {  
     //这时Person的初始化方法  
     System.out.println("Person的initMethod().....");
   }
   ```

   Spring配置

   ```xml
   <bean id="person" class="lifecycle.Person" init-method="initMethod">  
     <property name="name" value="Senner Ming"></property>  
     <property name="age" value="18"></property>
   </bean>
   ```

   就普通的测试方法

   ```markdown
   问题来了		
   1.如果一个对象实现了InitializingBean的接口，同时又提供了自定义的初始化方法，那么执行的顺序是怎样的？				
   答案：先执行了InitializingBean的接口实现，然后再实现了自定义的初始化方法，参考lifecycle.xml
   
   2.Spring在创建完对象，是先进行注入，还是先进行初始化？
   答案：先进行注入
   Product().....
   setName()......
   afterPropertiesSet()......
   myInit....
   
   3.什么是初始化操作？
   答案：主要是一些对资源的初始化，数据库 IO 网络 ....
   Servlet{
   init(ServletConfig){}
   service()
   }
   ```

   

3. 销毁阶段

   ```markdown
   Spring销毁对象前，会调用对象的销毁方法，完成销毁操作
   1.Spring什么时候销毁所创建的对象呢？
   答：applicationContext.close();也就是说是应用上下文关闭之前
   
   2.销毁方法：程序猿根据自己的需求，定义销毁方法， 完成销毁操作
   答：调用：Spring工厂完成调用
   ```
   
   那么如何定义自己的销毁方法呢？
   
   方式一：实现DisposableBean，还是一样，和初始化方式一样，这种方式和Spring的都和比较强
   
   ```java
   package lifecycle;
   import org.springframework.beans.factory.DisposableBean;
   import org.springframework.beans.factory.InitializingBean;
   public class Product implements InitializingBean, DisposableBean {
     private String name;    
     public String getName() {
       return name;    
     }    
     public void setName(String name) {
       System.out.println("setName()......");
       this.name = name;    
     }    
     public Product() {
       System.out.println("Product().....");
     }    
     public void myInit() {
       System.out.println("myInit....");
     }    
   	@Override
     public void afterPropertiesSet() throws Exception {
       System.out.println("afterPropertiesSet()......");
     }    
     @Override
     public void destroy() throws Exception {
       System.out.println("destroy().....");
     }
   }
   ```
   
   测试方法
   
   ```java
   @Test
   public void testProductLife() {
     ApplicationContext context = new ClassPathXmlApplicationContext("lifecycle.xml");  
     Product product = context.getBean("product", Product.class);  
     System.out.println(product);  
     ((ClassPathXmlApplicationContext) context).close();
   }
   ```
   
   
   
   方式二：自己定义，并在Spring配置文件中进行配置
   
   ```java
   package lifecycle;
   import org.springframework.beans.factory.DisposableBean;
   import org.springframework.beans.factory.InitializingBean;
   public class Product implements InitializingBean, DisposableBean {
     private String name;
     public String getName() {
       return name;
     }    
     public void setName(String name) {
       System.out.println("setName()......");
       this.name = name;    
     }    
     public Product() {
       System.out.println("Product().....");
     }    
     public void myInit() {
       System.out.println("myInit....");
     }    
     @Override
     public void afterPropertiesSet() throws Exception {
       System.out.println("afterPropertiesSet()......");
     }    
     public void myDestroy() {
       System.out.println("Destroy()....");
     }    
     @Override
     public void destroy() throws Exception {
       System.out.println("destroy().....");
     }
   }
   ```
   
   这样的话就要在Spring配置文件中进行配置了
   
   ```xml
   <!-- singleton Spring工厂创建时，Bean对象就会被创建 -->
   <bean id="product" scope="singleton" class="lifecycle.Product" init-method="myInit" destroy-method="myDestroy">  
     <property name="name" value="SennerMing"/> 
     <!-- 注入一定发生在初始化之前 -->
   </bean>
   ```
   
   ```markdown
   注意：		
   那么问题来了，实现DisposableBean和自定义销毁方法，同时存在，执行的顺序是！
   1.DisposableBean
   2.自定义的销毁方法
   ```
   
   
   
   ```markdown
   细节：		
   销毁方法的操作，只使用于scope=singleton的情况下，初始化操作的话，scope=singleton和prototype都适用
   1.什么叫销毁操作？		
   答：销毁操作，主要就是资源的释放操作，像是io.close()、connection.close()；		
   ```

### 11.2 生命周期小结

| Class类(InitializingBean,DisposableBean) | Spring工厂                   | 工厂方法调用时机                                             |
| ---------------------------------------- | ---------------------------- | ------------------------------------------------------------ |
| constructor()                            | 1.调用构造方法               | 1.scope=singleton,new ApplicationContext();2.还有虽然scope=singleton但设置了lazy-init=true那么构造方法的调用时机等同于3.scope=prototype,ctx.getBean("") |
| afterPropertiesSet()                     | 2.InitializingBean初始化     | 在对象构造完毕之后（属性都已经注入完成）                     |
| myInit()                                 | 3.init-method="myInit"       | 和上一步在一个阶段，但是如果2存在，则排在2之后               |
| destroy()                                | 4.DisposableBean销毁处理     | scope=singleton,ctx.close()                                  |
| myDestroy()                              | 5.destroy-method="myDestroy" | scope=singleton,ctx.close()                                  |

## 12 配置文件参数化

```markdown
配置文件参数化指的就是，把Spring配置文件中需要经常修改的字符串信息，转移到一个更小的配置文件当中
1.Spring的配置文件中存在需要经常修改的字符串？		
存在的，以数据库连接相关的参数作为代表		

2.为什么要转移？
那么这么多的配置信息，放在Spring的配置文件中，是能实现功能，但是对于维护来说，这就不一定是个好点子了！
不利于项目的维护（修改），运维大哥去xml中几千行代码里面去找这个数据库连接，进行修改，啧啧啧		

3.更小的配置文件到底是个什么形式？
配置文件参数化，k-v键值对的方式，一般都是像properties文件，那么这个运维大哥修改的地方就局限到这个小小的
文件当中了，即使修改错了，也就改了这些东西，很好定位，很好解决。
配置文件参数化：利于Spring配置文件的维护（修改）
```

### 12.1 配置文件参数开发

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

jdbc.properties，文件的名字和放置的位置都随便

```properties
prop.driverClass=com.mysql.jdbc.Driverprop.url=jdbc:mysql://localhost:3306/musicianclubprop.userName=rootprop.password=*********
```

将properties引入到Spring的配置文件当中，首先加入context标签

```xml
<!-- resources/下面的文件 可以参照target/classes/的路径 内容相同 -->
<context:property-placeholder location="classpath:jdbc.properties"></context:property-placeholder>
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
  <property name="driverClassName" value="${prop.driverClass}"></property>
  <property name="url" value = "${prop.url}"></property>
  <property name="username" value="${prop.userName}"></property>
  <property name="password" value="${prop.password}"></property>
</bean>
```

## 13 类型转换器

### 13.1 内置类型转换器的理解

```markdown
了解个大概：
像我们之前的    
<bean id="p" class="xxx.xxx.Person">
<property name="id" value="1"></property>
<property name="name" value="SennerMing"></property>
</bean>
为什么能进行正确的赋值呢？    
答：那么Spring在进行Bean的属性主语的时候，是先过了一遍Spring工厂的内置类型转换器
过程就是：
Converter
|- StringToNumber
|- NumberUtils.parseNumber()
String -------->   Number    

作用：
Spring通过类型转换器，把配置文件中的字符串类型的数据，转换成了对象中成员变量对应类型的数据，从而进行赋值操作
```

### 13.2 自定义类型转换器

```markdown
为什么需要我们自定义的类型转换器呢？
```

现实案例：

```xml
<bean id="alienware" class="converter.Alienware">
  <property name="name" value="SennerMingAli"/>
  <property name="birthday" value="2021-05-30"/>
</bean>
```

Bean对象

```java
package converter;
import java.io.Serializable;
import java.util.Date;
public class Alienware implements Serializable {
  private String name;
  private Date birthday;
  public String getName() {
    return name;    
  }    
  public void setName(String name) {
    this.name = name;
  }    
  public Date getBirthday() {
    return birthday;
  }    
  public void setBirthday(Date birthday) {
    this.birthday = birthday;    
  }
}
```

测试代码

```java
@Test
public void testConverter() {
  ApplicationContext context = new ClassPathXmlApplicationContext("converter.xml");
  Alienware alienware = context.getBean("alienware", Alienware.class);
  System.out.println(alienware);
}
//然后就报错了
//Failed to instantiate [java.util.Date]: Constructor threw exception;
```

那么这样，Spring它没有提供日期类型的转换器，各个国家显示的时间格式他是不同的，所以他并不内置日期转换器喔

原因：Spring没有内置对应类型的转换器，而我们现实生产中又需要

#### 13.2.1 实现

1.实现Converter接口

```java
package converter;
import org.springframework.core.convert.converter.Converter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//Converter<原始类型，目标类型>
public class MyDateConverter implements Converter<String, Date> {
  /**     
  * 核心的作用就是     
  *      String ---> Date
  *      将我们配置文件中的String类型转换成我们要的Date类型
  * 那么问题来了:     
  * 1.我们怎么才能从配置文件中获得我们的日期格式的字符串呢？     
  * 答：这个@param s，就是配置文件中的日期格式的字符串了，Spring框架会为我们提供的     
  *     
  * 2.那么我们怎么才能将根据日期格式的字符串用我们的代码转换好的日期类型，填到对应Bean的属性值中呢？     
  * 答：这个Spring也帮我们处理好了，我们将转换好的日期类型，作为返回值进行返回，那么Spring就会自动的进行属性值得注入操作
  *      其实呢就是，接口方法的回调！     
  * @param s     
  * @return     
  */    
  @Override
  public Date convert(String s) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
        try {
          date = sdf.parse(s);
        } catch (ParseException e) {
          e.printStackTrace();        
        }        
    return date;    
  }
}
```

那么这个Converter我们是写好了，但是我们得让Spring框架得知道我们写的这个转换器啊

2.Spring配置文件进行配置

```markdown
第一步：让Spring将MyDateConverter对象创建出来
<bean id="myDateConverter" class="converter.MyDateConverter"></bean>

第二步：告诉Spring框架，我们所创建的这个MyDateConverter是一个类型转换器
Spring为我们准备了yigeConversionServiceFactoryBean来处理我们自定义的这些转换器
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
<property name="converters">
	<set>
	<ref bean="myDateConverter"></ref>
	</set>
</property>
</bean>
```

自定义转换器

```java
package converter;
import org.springframework.core.convert.converter.Converter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//Converter<原始类型，目标类型>
public class MyDateConverter implements Converter<String, Date> {
  //进行改进  private String pattern;
  /**     
  * 核心的作用就是     
  *      String ---> Date     
  *      将我们配置文件中的String类型转换成我们要的Date类型     
  * 那么问题来了:     
  * 1.我们怎么才能从配置文件中获得我们的日期格式的字符串呢？     
  * 答：这个@param s，就是配置文件中的日期格式的字符串了，Spring框架会为我们提供的     
  *     
  * 2.那么我们怎么才能将根据日期格式的字符串用我们的代码转换好的日期类型，填到对应Bean的属性值中呢？     
  * 答：这个Spring也帮我们处理好了，我们将转换好的日期类型，作为返回值进行返回，那么Spring就会自动的进行属性值得注入操作     
  *      其实呢就是，接口方法的回调！     
  * @param s     
  * @return     
  */    
  @Override    
  public Date convert(String s) {
    SimpleDateFormat sdf = new SimpleDateFormat(pattern);
    Date date = null;
    try {
      date = sdf.parse(s);
    } catch (ParseException e) {
      e.printStackTrace();
    }        
    return date;
  }
}
```

Spring配置

```xml
<!-- 完整配置 -->
<!-- 第一步，我们先将我们自己写的日期格式转换器在咱们的这个Spring框架中构造出来 -->
<!-- <bean id="myDateConverter" class="converter.MyDateConverter"></bean> -->
<!-- 这种方式耦合度太高了，直接写在了程序里面了，这个可不好啊 -->
<bean id="myDateConverter" class="converter.MyDateConverter">	
  <property name="pattern" value="yyyy-MM-dd"></property>
</bean>
<!-- 第二步，我们要将我们的自定义转换器赋给我们的这个Spring的转换器工厂，这个ID必须是这个 -->
<bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">  
  <property name="converters">
    <set>
      <ref bean="myDateConverter"></ref>
    </set>  
  </property>
</bean>

<bean id="alienware" class="converter.Alienware">
  <property name="name" value="SennerMingAli"/>
  <property name="birthday" value="2021-05-30"/>
</bean>
```

```markdown
需要注意的一些细节：	
我们其他的一些Bean的类型，id的值我们都是随便写的，但是其实这个ConversionServiceFactoryBean的话，就不行，这个类的id的值，必须是conversionService，不能是其他的！
```

其实啊，Spring中已经内置了一个日期类型的转换器了

```markdown
Spring自带的字符串日期类型转换器：yyyy/MM/dd，是这个样子的！
```

## 14 后置处理再理解

```markdown
BeanPostProcessor作用：对Spring工厂所创建的对象，进行再加工Spring AOP的一些底层实现
```

Spring工厂对Bean的构造，我们再来复习一遍

```markdown
1. 构造的步骤：
答： ApplicatinContext中的配置文件		
<bean id="bean" class="xxx.xx.Bean"></bean>
1. Spring调用构造方法	
2. InitialzingBean	
3. initMethod="myInit"	
4. ctx.getBean("bean")

2. 那么我们怎么理解这个后置处理器呢？
答：Bean就像果园里面的苹果，在果园把苹果养成，创建好了，我们果农还要对苹果进行打蜡什么的处理，让苹果看上去更好，或者做一些处理，让苹果更可口，那么我们的后置处理器在Spring工厂中，就扮演着果农的角色
```

那我们将BeanPostProcessor放到Spring工厂中进行理解

```markdown
1. 加入BeanPostProcessor后，Bean的构造的步骤
ApplicationContext		
<bean id="bean" class="xxx.xx.Bean"></bean>		
1.Spring调用构造方法

2.那么我们在构造好了Bean之后，我们并不直接交给Spring进行初始化的一些操作了，而是交给BeanPostProcessor，会调用该processor中的Object postProcessBeforeInitialization(Object bean,String beanName);进行初始化完成之前的Bean的加工操作，bean就是这个构造好的Bean，beanName就是<bean>标签中的id的值，返回的Object就是我们加工完成的Bean对象		

3.接着postProcessBeforeInitialization返回的Bean对象就交到了咱么的这个InitializingBean进行初始化操作		

4.然后才到initMethod="myInit"		

5.初始化完成之后，又将完成初始化操作的Bean交到咱们的BeanPostProcessor的Object postProcessAfterInitialization(Object bean,String beanName);进行初始化完成后的Bean的加工操作
```

我们可爱的程序猿需要实现BeanPostProcessor的这个接口：

- Object postProcessBeforeInitialization(Object bean,String beanName);

  ```markdown
  作用：Spring创建完对象，并进行注入后，可以运行Before方法进行加工获得Spring创建好的Bean对象：通过方法的参数进行获得最终通过返回值，将Bean对象交还给Spring进行后续的处理
  ```

  

- Object postProcessAfterInitialization(Object bean,String beanName);

  ```markdown
  作用：Spring执行完Bean对象的初始化操作后，可以运行After方法进行加工获得Spring初始化完成的Bean对象：通过方法的参数最终通过返回值再将Bean交还给Spring供用户使用
  ```

**实战中**：很少处理Spring的初始化操作，没有必要区分Before After。只需要实现其中一个方法即可，通常只实现那个After的方法，因为它是交给用户使用的最后一步

**注意**：在我们只实现了一个After方法的时候，但是需要注意的是Before的操作，必须将Object bean进行返回，不然处理链就断掉了！

**开发步骤**

1.实现BeanPostProcessor这个接口

```java
package beanpost;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
//实现了BeanPostProcessor，且没有实现任何方法，他竟然没有任何报错，你受得了吗
//这是JDK1.8之后的新特性 default函数
public class MyBeanPostProcessor implements BeanPostProcessor {    
  /**     
  * 这个还是要注意，一定要把这个Bean进行返回
  * @param bean
  * @param beanName
  * @return     
  * @throws BeansException
  */    
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;    
  }    
  /**     
  * 我们就实现一个就得了
  * @param bean
  * @param beanName
  * @return     
  * @throws BeansException
  */    
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof Category) {
      Category category = (Category) bean;
      category.setName("小王");
    }        
    return bean;
  }
}
```



2.需要在Spring的配置文件中进行配置

```xml
<bean id="myBeanPostProcessor" class="beanpost.MyBeanPostProcessor"></bean>
<bean id="category" class="beanpost.Category">
  <property name="id" value="10"/>
  <property name="name" value="SennerMing"/>
</bean>
```

3.进行测试

```java
@Test
public void testMyBeanPostProcessor() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beanpost.xml");
  Category category = applicationContext.getBean("category", Category.class);
  System.out.println(category.getName());
}
```

在开发中需要注意的小细节

```markdown
BeanPostProcessor会对Spring工厂创建的所有对象进行加工
```

## Spring与日志框架的整合

Spring与日志框架进行整合，日志框架就可以在控制台中，输出Spring框架运行过程中的一些重要信息。

好处就是便于我们了解Spring框架的运行过程，有利于调试解决运行过程中出现的问题。

Spring如何整合日志框架？

```mark
默认：
早期的Spring1.2.3都是与commons-loggin.jar进行整合的
Spring5.x默认整合的日志框架有 logback log4j2

整合Log4j:		
1.引入相关jar包
2.引入log4j.properties的配置文件
```

依赖

```xml
<!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12 为了覆盖默认的-->
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-log4j12</artifactId>
  <version>1.8.0-alpha0</version>
  <scope>test</scope>
</dependency>
<!-- https://mvnrepository.com/artifact/log4j/log4j --><dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```

log4j.properties

```properties
#resource文件夹根目录下### 配置根log4j.rootLogger=debug,console### 日志输出到控制台显示log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Target=System.out
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

