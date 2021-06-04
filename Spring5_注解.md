# Spring 5 Annotation 学习

## 1 Spring注解基础概念

```mark
一、什么是注解编程
答：指的是在类或者方法上加入特定的注解(@Xxxxx)，完成特定功能的开发。
例如:@Component public class XxxComponent{}

二、为什么要进行注解开发？
1. 进行注解开发会特别的方便，代码也变得非常简洁，开发速度的大大提高
2. Spring开发的一个潮流，从Spring2.x引入注解，Spring3.x进行了完善，现如今SpringBoot的普及，推广注解编程!

三、注解有哪些作用？
1. 替换XML这种配置形式，简化配置
2. 替换接口，实现调用双方的契约性
```

问题三展开

```markdown
1. 替换XML这种配置形式，简化配置
早年在我老师公司的时候，进行的SSM的开发，我们要将一个Java类交由Spring进行管理我们得进行application.xml配置文件的编写，例如：有个User类，那么我们就要在配置文件中写：
<bean id="user" class="club.musician.entity.User"></bean>，这样Spring就可以通过这个配置，为我们提供Bean，那么通过注解的方式怎么写呢？

@Component
public class User{}

我们只需要在类上加一个@Component注解就能完成Bean对象的注册
```

```markdown
2. 替换接口，实现调用双方的契约性
怎么理解呢？假设我们现在有两个角色
一个是功能的调用者：
public class Consumer{
	public void invoke(){}
}
一个是功能的提供者：
public class Provider{
	//方法名字随便写的
	public void m1(){}
}
那么Provider写好了这个方法后，Consumer在invoke中进行了方法的调用，这个流程看上去一点问题也没有，这两个东西是同一个人写的还好，那这两个人互相都不认识，功能提供者写好的方法，调用者怎么去调用啊？他都不知道有这个方法，那要解决这样的问题，我们就需要做出一个约定，Spring在其中就扮演着这个契约的制定者，用以保证契约的执行。

#### 契约性
1. 定义接口
Spring定义了一个Contract接口，接口中顶一个了一个method1(),提供者需要对Contract进行实现，按照接口规范进行方法的编写，那功能调用者，就可以使用Spring的Contract来调用提供者编写的方法，那么这个Contract就让素昧谋面的两个人，完成了交互。

2. 有时候定义接口也并不是很方便
像是Servlet中定义了一些方法像是
 |- init(ServletConfig):void
 |- service(ServletRequest,ServletResponse):void
 |- destory():void
这些方法就是Tomcat和我们程序猿之间的约定，说我程序猿代码写在哪，就能完美的进行组合调用啊？那人家Tomcat规定了这个Servlet接口进行了定义，我们针对Servlet进行功能的开发，人家Tomcat针对Servlet进行调用，那，这样，我们程序员和Tomcat之间就达成了这么一个契约精神，但是在我们开发的过程中，你就会发现了这样一个问题了，其实啊，在整个Servlet接口中，我们最关注的是这个service(ServletRequest,ServletResponse):void方法,但是我们需要实现这个Servlet接口，连带性的我们还要对其他的方法进行实现，这个时候，作为我们程序员就显得很麻烦了，当时我们是怎么解决的啊？我们是通过适配器的这样一个设计模式，就很好的解决了这个问题---HttpServlet，其实这个适配器，也还是显得比较的繁琐的，后来的后来，注解也可以让我们达成这种契约，而且注解的控制更加精准，我们想实现哪个方法，就实现哪个方法！真的算是一种生产力的解放。
```

注解替换接口实现契约性

```markdown
我们看看注解方式契约性的实现思路
还是有功能的调用者:
pulic class Consumer{
		public void invoke(){
			//通过反射扫描注解，获得方法的信息
		}
}
然后有功能的提供者:
public class Provider{
		@Contract
		public void m1(){
				//功能实现
		}
}
通过注解，实现起来就很简洁，也不要实现接口，也不要实现其他杂七杂八的方法
其实我们之前的学习的过程当中，我们的额外功能就必须去实现Methodinterceptor，然后它里面有个invoke(MethodInvocation):Object方法，我们在里面实现我们的日志额外功能的开发；
那后面我们又学习了注解编程后，额外功能就不需要实现任何借口，对于规定的方法命名也没有了要求，只需要在额外功能的方法上增加一个@Around("execution(* *(..))"")这个注解就行了，是不是就非常之方便了啊，虽然二者的表象不太一致，但是实现的功能并无差别，但后者看上去就更为优雅与简化。
```

通过注解的方式，在功能的调用者与功能的提供者之间打成约定，进而完成功能的调用。因为注解应用更为方便灵活，所以在现在的开发中，更推荐通过注解的形式完成。

### 1.1 Spring注解的发展历程

```markdown
1. Spring2.x开始支持注解编程：@Component @Service @Scope....
	目的：提供的这些注解只是为了在某些情况下简化XML的配置，作为XML开发的有益补充
	
2. Spring3.x又在Spring2.x的版本上进行了补充：@Configuraion @Bean
	目的：想彻底替换XML的配置，基于纯注解的编程

3. Spring4.x的时候又衍生出了SpringBoot
	目的：提倡咱们基于注解进行常见的开发
```

### 1.2 Spring开发的一个问题

```markdown
那Spring基于注解进行配置后，还能否解耦合呢？
答：我们配置直接卸载了代码里面，也没有进行配置，我对注解不满意了，我修改了一个注解，那我修改的是代码，这不就发生了耦合了嘛？原来Spring不就是为了解耦合嘛？
在Spring框架应用注解时，如果我们对注解配置的内容不满意了，可以通过Spring配置文件进行覆盖的！
```

## 2 注解的理解

```markdown
关于注解首先引入官方文档的一句话：Java 注解用于为 Java 代码提供元数据。作为元数据，注解不直接影响你的代码执行，但也有一些类型的注解实际上可以用于这一目的。Java 注解是从 Java5 开始添加到 Java 的。看完这句话也许你还是一脸懵逼，接下我将从注解的定义、元注解、注解属性、自定义注解、注解解析JDK 提供的注解这几个方面再次了解注解（Annotation）
```

### 2.1 注解的定义

```markdown
日常开发中新建Java类，我们使用class、interface比较多，而注解和它们一样，也是一种类的类型，他是用的修饰符为 @interface
```

### 2.2 注解的写法

首先创建一个注解：

```java
package annotation.basic;

public @interface MyAnnotation {

}
```

使用我们创建的注解：

```java
package annotation.basic;

@MyAnnotation
public class TestMyAnnotation {
    @MyAnnotation
    public static void main(String[] args) {

    }
}
```

```markdown
了解了最基本的写法，那我们这个注解毫无意义，对咱么的程序执行，没有产生丝毫的影响
现在这个注解毫无意义，要如何使注解工作呢？接下来我们接着了解元注解。
```

### 2.3 元注解

```markdown
元注解顾名思义我们可以理解为注解的注解，它是作用在注解中，方便我们使用注解实现想要的功能。元注解分别有@Retention、 @Target、 @Document、 @Inherited和@Repeatable（JDK1.8加入）五种。
```

#### 2.3.1 @Retention

```markdown
Retention:
	英文意思有保留、保持的意思，它表示注解存在阶段是保留在源码（编译期），字节码（类加载）或者运行期（JVM中运行）。在@Retention注解中使用枚举RetentionPolicy来表示注解保留时期

@Retention(RetentionPolicy.SOURCE):注解仅存在于源码中，在class字节码文件中不包含
@Retention(RetentionPolicy.CLASS): 默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得
@Retention(RetentionPolicy.RUNTIME): 注解会在class字节码文件中存在，在运行时可以通过反射获取到

如果我们是自定义注解，则通过前面分析，我们自定义注解如果只存着源码中或者字节码文件中就无法发挥作用，而在运行期间能获取到注解才能实现我们目的，所以自定义注解中肯定是使用 @Retention(RetentionPolicy.RUNTIME)
```

那么就对我们刚才的MyAnnotation进行一个@Retention的修改

```java
package annotation.basic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface MyAnnotation {

}
```

#### 2.3.2 @Target

```markdown
Target的英文意思是目标，这也很容易理解，使用@Target元注解表示我们的注解作用的范围就比较具体了，可以是类，方法，方法参数变量等，同样也是通过枚举类ElementType表达作用类型

@Target(ElementType.TYPE):作用接口、类、枚举、注解
@Target(ElementType.FIELD):作用属性字段、枚举的常量
@Target(ElementType.METHOD):作用方法
@Target(ElementType.PARAMETER):作用方法参数
@Target(ElementType.CONSTRUCTOR):作用构造函数
@Target(ElementType.LOCAL_VARIABLE):作用局部变量
@Target(ElementType.ANNOTATION_TYPE):作用于注解（@Retention注解中就使用该属性）
@Target(ElementType.PACKAGE):作用于包
@Target(ElementType.TYPE_PARAMETER):作用于类型泛型，即泛型方法、泛型类、泛型接口 （jdk1.8加入）
@Target(ElementType.TYPE_USE):类型使用，可以用于标注任意类型除了 class （jdk1.8加入）

一般比较常用的是ElementType.TYPE类型
```

为我们的MyAnnotation添加上目标

```java
package annotation.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface MyAnnotation {

}
```

#### 2.3.3 @Documented

```markdown
Document的英文意思是文档。它的作用是能够将注解中的元素包含到 Javadoc 中去。
```

#### 2.3.4 @Inherited

```markdown
Inherited的英文意思是继承，但是这个继承和我们平时理解的继承大同小异，一个被@Inherited注解了的注解修饰了一个父类，如果他的子类没有被其他注解修饰，则它的子类也继承了父类的注解。
```

小栗子：

```java
/**自定义注解*/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyTestAnnotation {
}
/**父类标注自定义注解*/
@MyTestAnnotation
public class Father {
}
/**子类*/
public class Son extends Father {
}
/**测试子类获取父类自定义注解*/
public class test {
   public static void main(String[] args){

      //获取Son的class对象
       Class<Son> sonClass = Son.class;
      // 获取Son类上的注解MyTestAnnotation可以执行成功
      MyTestAnnotation annotation = sonClass.getAnnotation(MyTestAnnotation.class);
   }
}
```

#### 2.3.5 @Repeatable

```markdown
Repeatable的英文意思是可重复的。顾名思义说明被这个元注解修饰的注解可以同时作用一个对象多次，但是每次作用注解又可以代表不同的含义。
```

小栗子：

```java
/**一个人喜欢玩游戏，他喜欢玩英雄联盟，绝地求生，极品飞车，尘埃4等，则我们需要定义一个人的注解，他属性代表喜欢玩游戏集合，一个游戏注解，游戏属性代表游戏名称*/
/**玩家注解*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface People {
    Game[] value() ;
}
/**游戏注解*/
@Repeatable(People.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Game {
    String value() default "";
}
/**玩游戏类*/
@Game(value = "LOL")
@Game(value = "PUBG")
@Game(value = "NFS")
@Game(value = "Dirt4")
public class PlayGame {
}
```

```markdown
那么你不禁要问了：
游戏注解中括号的变量是啥，其实这和游戏注解中定义的属性对应。接下来我们继续学习注解的属性。
```

### 2.4 注解属性

```markdown
通过上一小节@Repeatable注解的例子，我们说到注解的属性。注解的属性其实和类中定义的变量有异曲同工之处，只是注解中的变量都是成员变量（属性），并且注解中是没有方法的，只有成员变量，变量名就是使用注解括号中对应的参数名，变量返回值注解括号中对应参数类型。相信这会你应该会对上面的例子有一个更深的认识。而@Repeatable注解中的变量则类型则是对应Annotation（接口）的泛型Class。
```

上代码

```java
/**注解Repeatable源码*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Repeatable {
    /**
     * Indicates the <em>containing annotation type</em> for the
     * repeatable annotation type.
     * @return the containing annotation type
     */
    Class<? extends Annotation> value();
}
```

### 2.5 注解的本质

```markdown
注解的本质就是一个Annotation接口
```

```java
/**Annotation接口源码*/
public interface Annotation {

    boolean equals(Object obj);

    int hashCode();

    Class<? extends Annotation> annotationType();
}
```

```markdown
通过以上源码，我们知道注解本身就是Annotation接口的子接口，也就是说注解中其实是可以有属性和方法，但是接口中的属性都是static final的，对于注解来说没什么意义，而我们定义接口的方法就相当于注解的属性，也就对应了前面说的为什么注解只有属性成员变量，其实他就是接口的方法，这就是为什么成员变量会有括号，不同于接口我们可以在注解的括号中给成员变量赋值。
```

### 2.6 注解属性类型

```markdown
注解属性类型可以有以下列出的类型
1.基本数据类型
2.String
3.枚举类型
4.注解类型
5.Class类型
6.以上类型的一维数组类型
```

### 2.7 注解成员变量赋值

```markdown
如果注解有多个属性，则可以在注解括号中用“，”号隔开分别给对应的属性赋值，如下例子，注解在父类中赋值属性
```

```java
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyTestAnnotation {
    String name() default "mao";
    int age() default 18;
}

@MyTestAnnotation(name = "father",age = 50)
public class Father {
}
```

### 2.8 获取注解属性

```markdown
前面我们说了很多注解如何定义，放在哪，现在我们可以开始学习注解属性的提取了，这才是使用注解的关键，获取属性的值才是使用注解的目的。
如果获取注解属性，当然是反射啦，主要有三个基本的方法
```

```java
/**是否存在对应 Annotation 对象*/
public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
  return GenericDeclaration.super.isAnnotationPresent(annotationClass);
}
/**获取 Annotation 对象*/
public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
  Objects.requireNonNull(annotationClass);
  return (A) annotationData().annotations.get(annotationClass);
}
/**获取所有 Annotation 对象数组*/
public Annotation[] getAnnotations() {
  return AnnotationParser.toArray(annotationData().annotations);
}    
```

```markdown
下面结合前面的例子，我们来获取一下注解属性，在获取之前我们自定义的注解必须使用元注解
@Retention(RetentionPolicy.RUNTIME)
```

```java
public class test {
   public static void main(String[] args) throws NoSuchMethodException {

        /**
         * 获取类注解属性
         */
        Class<Father> fatherClass = Father.class;
        boolean annotationPresent = fatherClass.isAnnotationPresent(MyTestAnnotation.class);
        if(annotationPresent){
            MyTestAnnotation annotation = fatherClass.getAnnotation(MyTestAnnotation.class);
            System.out.println(annotation.name());
            System.out.println(annotation.age());
        }

        /**
         * 获取方法注解属性
         */
        try {
            Field age = fatherClass.getDeclaredField("age");
            boolean annotationPresent1 = age.isAnnotationPresent(Age.class);
            if(annotationPresent1){
                Age annotation = age.getAnnotation(Age.class);
                System.out.println(annotation.value());
            }

            Method play = PlayGame.class.getDeclaredMethod("play");
            if (play!=null){
                People annotation2 = play.getAnnotation(People.class);
                Game[] value = annotation2.value();
                for (Game game : value) {
                    System.out.println(game.value());
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
```

### 2.9 JDK 提供的注解

| 注解              | 作用                                                         | 备注                                                         |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| @Override         | 它是用来描述一个方法是一个重写的方法，在编译阶段对方法进行检查 | jdk1.5中它只能描述继承中的重写，jdk1.6中它可以描述接口实现的重写,也能描述类的继承的重写 |
| @Deprecated       | @Deprecated                                                  | 无                                                           |
| @SuppressWarnings | 对程序中的警告去除                                           | 无                                                           |

### 2.10 注解作用与应用

```markdown
现在我们再次回头看看开头官方文档的那句描述
Java 注解用于为 Java 代码提供元数据。作为元数据，注解不直接影响你的代码执行，但也有一些类型的注解实际上可以用于这一目的。

经过我们前面的了解，注解其实是个很方便的东西，它存活的时间，作用的区域都可以由你方便设置，只是你用注解来干嘛的问题
```

### 2.11 使用注解进行参数配置

```markdown
下面我们看一个银行转账的例子，假设银行有个转账业务，转账的限额可能会根据汇率的变化而变化，我们可以利用注解灵活配置转账的限额，而不用每次都去修改我们的业务代码。
```

```java
/**定义限额注解*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BankTransferMoney {
    double maxMoney() default 10000;
}
/**转账处理业务类*/
public class BankService {
    /**
     * @param money 转账金额
     */
    @BankTransferMoney(maxMoney = 15000)
    public static void TransferMoney(double money){
        System.out.println(processAnnotationMoney(money));

    }
    private static String processAnnotationMoney(double money) {
        try {
            Method transferMoney = BankService.class.getDeclaredMethod("TransferMoney",double.class);
            boolean annotationPresent = transferMoney.isAnnotationPresent(BankTransferMoney.class);
            if(annotationPresent){
                BankTransferMoney annotation = transferMoney.getAnnotation(BankTransferMoney.class);
                double l = annotation.maxMoney();
                if(money>l){
                   return "转账金额大于限额，转账失败";
                }else {
                    return"转账金额为:"+money+"，转账成功";
                }
            }
        } catch ( NoSuchMethodException e) {
            e.printStackTrace();
        }
        return "转账处理失败";
    }
    public static void main(String[] args){
        TransferMoney(10000);
    }
}
```

```markdown
通过上面的例子，只要汇率变化，我们就改变注解的配置值就可以直接改变当前最大限额。
```

### 2.12 注解的作用

```markdown
提供信息给编译器： 编译器可以利用注解来检测出错误或者警告信息，打印出日志。
编译阶段时的处理： 软件工具可以用来利用注解信息来自动生成代码、文档或者做其它相应的自动处理。
运行时处理： 某些注解可以在程序运行的时候接受代码的提取，自动做相应的操作。
正如官方文档的那句话所说，注解能够提供元数据，转账例子中处理获取注解值的过程是我们开发者直接写的注解提取逻辑，处理提取和处理 Annotation 的代码统称为 APT（Annotation Processing Tool)。上面转账例子中的processAnnotationMoney方法就可以理解为APT工具类。
```



## 3 Spring的基础注解(Spring2.x)

仅仅是简化了Xml的配置，并不能取代

### 3.1 对象创建相关注解

搭建开发环境

```markdown
<context:component-scan base-package=""/>

问：我们为什么要添加这个标签呢？他的作用是什么呢？
答：我们将来要写的代码，Spring他知道嘛？不知道，添加这个的目的就是让Spring去扫描这个base-package包及其子包中，去扫描这些个组件
```

#### 3.1.1 @Component

```markdown
作用：用于替换原有Spring配置中的bean标签

问：那之前的bean标签中有id属性还是class属性，那我们这个@Component注解又是怎么体现这两个属性的呢？
答：他有一个默认的id的名称，就是这个类名的首单词首字母小写，class完全可以通过反射进行获取
```

示例(spring-annotation.xml)：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="annotation.deeplearning.bean"/>
</beans>
```

代码(TestAnnotation)：

```java
@Test
public void testComponent() {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-annotation.xml");
    User user = (User) applicationContext.getBean("user");
    System.out.println(user);
}
```

```markdown
@Component细节:
1. 如何显示指定工厂创建对象的id值
@Component("componentName")

2. Spring配置文件覆盖注解配置内容
<bean id="user" class="annotation.deeplearning.User"></bean>
注意点：这个id值必须要和@Component中指定的bean的名称相同
如果名称不一样会发生什么呢？
日志信息
2021-06-03 14:38:31 DEBUG DefaultListableBeanFactory:225 - Creating shared instance of singleton bean 'user'
2021-06-03 14:38:31 DEBUG DefaultListableBeanFactory:225 - Creating shared instance of singleton bean 'user1'
可以发现Spring工厂为我们创建了两个User类的Bean对象
```

#### 3.1.2 @Component衍生注解

```markdown
问：@Component有哪些衍生注解呢？
答： @Repository、@Service、@Controller
本质上这些衍生注解，就是@Component，他们的作用、细节乃至用法都是完全一致的，就是等同于bean标签

那么问题就来了，这都一样的，为啥还有存在的必要呢？
答：为了更加准确地表达一个类型的作用
@Repository ---> DAO、@Service ---> Service、@Controller ---> Controller/Action

注意：在整合Spring与Mybatis整合的过程中，不使用@Repository这个注解，DAO的对象，是我们MapperScannerConfigure通过代码的方式帮我们自动创建的
```

#### 3.2.3 @Scope

```markdown
@Scope作用：
控制简单对象创建的次数，<bean id="user" class="..." scope="singleton|prototype"></bean>
默认值也是singleton
```

```java
package annotation.deeplearning.bean.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Consumer {
}
```

#### 3.2.4 @Lazy

```markdown
@Lazy作用：
用于延迟创建singleton对象，<bean id="" class="" lazy="false"></bean>
会在使用的时候，才进行单例类的创建
```

```java
package annotation.deeplearning.bean.lazy;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = true)
public class Account {
    public Account() {
        System.out.println("Account无参构造.....");
    }
}
```

### 3.2 声明周期相关注解

```markdown
一.初始化相关方法，之前
1. 初始化的实现	
实现InitializingBean，或者在bean标签中配置init-method="myInit"

2. 销毁方法的实现
实现DisposableBean，或者在bean标签中配置destroy-method="myDestroy"

现在使用注解的方式
1. 初始化的注解
@PostConstruct

2. 销毁的注解
@PreDestroy

注意：
1. 这两个注解，并不是Spring提供的，是JSR-520，JSR是JavaEE规范的，Spring做了兼容
2. 再一次验证了，通过注解实现了接口的契约性
3. prototype的时候，销毁不受Spring工厂的控制
```

```java
package annotation.deeplearning.bean.life;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Scope(value = "singleton")
//@Scope(value = "prototype")
public class Product {

    @PostConstruct
    public void myInit() {
        System.out.println("Product myInit().....");
    }

    @PreDestroy
    public void myDestroy() {
        System.out.println("Product myDestroy()......");
    }
}
```

```java
@Test
public void testLife() {
  ApplicationContext context = new ClassPathXmlApplicationContext("spring-annotation.xml");
  Product product = (Product) context.getBean("product");
  ((ClassPathXmlApplicationContext)context).close();
}
```

### 3.3 注入相关注解

#### 3.3.1 用户自定义类型注入

##### 3.3.1.1 @Autowired

```markdown
一、基于标签的方式
之前我们是创建了UserServiceImpl在其中依赖UserDAO与数据库进行交互
<bean id="userDAO" class="xx.xx.dao.UserDAO"></bean>
<bean id="userService" class="xx.xx.service.impl.UserServiceImpl">
	<property name="userDAO" ref="userDAO"></property>
</bean>
这样基于配置文件，我们就完成了DAO的注入

二、基于注解的方式
那首先得完成上面的UserServiceImpl与UserDAO的创建，分别添加@Service和@Repository注解
然后再在UserServiceImpl中的userDAO的属性上添加@Autowire的注解
```

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

@Autowired一些细节

```mark
一、@Autowired是如何区分不同的DAO的？
我们还可以将@Autowired放置在咱们属性的set方法上，例如：
public class XxxService{
	private XxxDao xxxDao;
	@Autowired
	public void setXxxDao(XxxDao xxxDao){
		this.xxxDao = xxxDao;
	}
}
基于类型区分：那么我们的@Autowired是成员变量的相同类型、子类或者是实现类

二、@Autowired还支持bean的name进行注入的，但是需要配合@Qualifier
基于名字的注入：注入对象的id值，必须与@Qualifier中设置的名字相同

三、@Autowired放置位置
1. 放置在成员变量的set方法上(根据类型进行注入)
2. 直接放置在成员变量之上(通过反射直接对成员变量注入)

四、那我们看到前面的Spring提供的@Autowired和@Qualifier注解，为我们进行提供的引用类型注入的注解
JAVA EE规范中有没有为我们提供类似功能的注解呢？
答：
JSR 250 
@Resource，这个注解也是基于名字，进行的注入;
相当于@Resource(name="user1") = @Autowire+@Qualifier("user1")
如果在使用@Resource的时候，没有指定name属性的值，这样的话，他会按类型进行注入

JSR 330
@Inject，作用与@Autowired完全一致的，基于类型进行注入的，不能直接使用，需要使用的话就需要引入jar包
<dependency>
	<groupId>javax.inject</groupId>
	<artifactId>javax.inject</artifactId>
	<version>xxx</version>
</dependency>
在Spring的开发中比较少，主要是在EJB 3.0比较多，但是这个EJB 3.0有点黄了
```



##### 3.3.1.2 @Qualifier

@Qualifier：根据属性名称进行注入

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
@RestControllerpublic 
class TestController {    
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

##### 3.3.1.3 @Resource

可以根据类型注入，也可以根据名称进行注入

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



#### 3.3.2 JDK类型

```markdown
曾经在Xml配置中进行配置的
<bean id="user" class="club.musician.entity">
	<property name="id" value="10"></property>
	<property name="name" value="SennerMing"></property>
</bean>
//以后都不配置bean这个标签了，这种方式也就终结了，那写到哪里呢？
答案是写到配置文件里面
```

```markdown
那问题来了，我们把这些值的信息，写到了配置文件里面，那Spring在帮我们创建bean的时候，怎么能用的到呢？
答：
之前的XML配置文件的方式
<context:property-palceholder location="classpath:/xxx.properties"/>，配置完成后，Spring就可以根据这个进行可以直接在<property name="name" value="${配置文件中的key}"></peoperty>

注解的方式我们就可以使用@Value来完成这个功能
```



##### 3.3.2.1 @Value

注入普通类型属性

```java
@Value(value = "SennerMing")
private String name;
```

```java
package annotation.deeplearning.bean;

import org.springframework.stereotype.Component;

@Component
public class Category {
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

Category.properties

```properties
category.id=20
category.name=SennerMing
```

Spring-annotation.xml

```xml
<context:property-placeholder location="classpath:category.properties"></context:property-placeholder>
```

测试方法

```java
@Test
public void testValue() {
  ApplicationContext context = new ClassPathXmlApplicationContext("spring-annotation.xml");
  Category category = (Category) context.getBean("category");
  System.out.println(category.getId());
  System.out.println(category.getName());
}
```

小结

```markdown
@Value注解
1. 设置xxx.properties
2. Spring的工厂读取这个配置文件
<context:property-placeholder location=""/>
3. 代码注解
@Value(value="${key}")
```

针对小结的问题2

```markdown
Spring的工厂读取这个配置文件
<context:property-placeholder location=""/>
那这个我们能不能也用注解进行替换呢？那这个注解就是@PropertySource
```

##### 3.3.2.2 @PropertySource

```markdown
1. 作用：用于替换Spring配置文件中的<context:property-placeholder location="">
2. 开发步骤：
	1). 设置xxx.properties 
	2). 使用@PropertySource
	3). 属性上使用@Value
```

示例

```java
package annotation.deeplearning.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:category.properties"})
public class Category {
    @Value(value="${category.id}")
    private Integer id;
    @Value(value = "${category.name}")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

##### 3.3.2.3 @Value注意事项

```markdown
一、@Value不能使用到静态变量上面
如果是静态的就赋不上值

二、@Value+@PropertySource是无法进行集合类型的注入
那么后续的话，Spring提供了新的配置文件的形式：YAML(YML)
```

### 3.4 注解扫描详解

```markdown
一、来看看我们的配置文件，是如何配置包扫描的
<context:component-scan base-package="club.musician"/>
这个标签，是对当前包及其子包，这种扫描的方式是比较粗犷的，那有没有更细粒度的扫描控制呢？
```

#### 3.4.1 排除方式

```xml
<context:component-scan base-package="club.musician">
  <!-- 
			一、type:
					1. assignable:在包扫描的过程中，会排除其指定的类型，不进行扫描
					2. annotation:用于排除指定的注解，不进行扫描
					3. aspectj:通过切入点表达式来进行配置的，但是只支持包和类的表达式
					4. regex:正则表达式
					5. custom:自定义策略，框架底层开发使用多
			二、不同，相同类型都可以进行叠加
	-->
	<context:exclude-filter type="assignable" expression="annotation.deeplearning.bean.User"/>
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Repository"/>
  <context:exclude-filter type="aspectj" expression="annotation.deeplearning.bean..*"/>
  <context:exclude-filter type="aspectj" expression="*..User"/>
  

</context:component-scan>
```



#### 3.4.2 包含方式

```markdown
//我只想扫描带@Service注解的类
<context:component-scan base-package="annotation.deeplearning.bean" use-default-filters="false">
<!--
     包含的方式：
         1.首先得为<context:component-scan>标签加入一个属性:use-default-filters="false"
            不使用默认的包扫描方式，让当前包及其子包的扫描方式失效
         2.<context:include-filter>
            作用：指定扫描哪些注解
            1. assignable:在包扫描的过程中，会排除其指定的类型，进行扫描
            2. annotation:用于排除指定的注解，进行扫描
            3. aspectj:通过切入点表达式来进行配置的，但是只支持包和类的表达式
            4. regex:正则表达式
            5. custom:自定义策略，框架底层开发使用多
			不同，相同类型都可以进行叠加
-->
        <context:include-filter type="annotation" expression="org.springframework.stereotype.Service"/>
</context:component-scan>
```

### 3.5 对于注解开发的思考

配置互通

```markdown
一、那么什么是配置互通呢？
Spring在创建对象是有两种配置方式的，一种是基于注解，一种是基于XML的，假设我有两个类
@Repository
public class UserDao{}

public class UserServiceImpl{
	private UserDao userDao;
	set;get;
}
那么我们通过配置文件的方式给UserService注入UserDao的对象
<bean id="user" class="club.musician.service.UserServiceImpl">
	<property name="userDao" ref="userDao"></property>
</bean>
这样写是完全没有问题的，反之亦然
```

什么情况下使用注解，什么情况下使用配置文件？

```markdown
问：那么我们已经有了这个@Component的注解，是不是就能替换掉这个<bean>标签了呢？
答：不行的，因为这些注解(@Component,@Autowired,@Value...)，只能加入到我们程序员在开发过程中类的上面的，那么在后续开发的过程中，我们会遇到很多不是我们开发的类，在做Spring整合的过程中，遇到了MapperScannerConfigure、SqlSessionFatoryBean啊，那我们就只能使用到bean标签了

在后续的注解高级用法中，是可以解决上面的问题的
```



## 4 基于注解的SSM整合(基础注解版)

### 4.1 搭建开发环境

1. 引入相关依赖

   参照sweb项目的配置

2. 引入相关配置文件

   - applicationContext.xml

   - Struts.xml
   - Log4j.properties
   - XxxMapper.xml

3. 初始化配置

   - web.xml Spring

   - web.xml Struts

## 5 Spring高级注解

Spring3.x及以上

### 5.1 配置Bean

```markdown
Spring在3.x提供的新的注解，用于替换XML配置文件
1. 那么怎么用呢？
@Configuration
public class AppConfig{}
那么加入这个@Configuration注解之后，这个普通的java类就成为了一个配置Bean

2. 那么这个类加上了@Configuration之后，是用来替换XML配置文件的，那它到底替换了XML的那些内容呢？
1).先来看看我们之前XML都配置了哪些东西吧？
通过bean标签，进行对象的创建:<bean id="" class=""/>
为属性进行注入:<property name="" value=""></property>
定义注解的扫描:<context:component-scan base-package=""/>
引入其他的配置文件:<import resource=""/>

其实这些都能通过我们的@Configuration bean进行配置
```

创建工厂，换工厂的实现类

```markdown
一、之前我们用的是：
ClassPathXmlApplicationContext("applicationContext.xml");需要指定配置文件路径

二、现在要用的实现类是：
AnnotationConfigApplicationContext(AppConfig.class);
1).指定配置类的class
2).指定一个basePackage，进而Spring会去这个包下面去找具有@Configuration的这个注解的类
```

### 5.2 @Configuration本质

```markdown
本质：也是@Component的衍生注解
当然也可以应用<context:component-scan>进行扫描
```

### 5.3 @Bean注解

```markdown
在XML配置文件中，我们用注解替换XML配置后，怎么样完成对Bean的创建呢？
```

#### 5.3.1 @Bean的基本使用

```markdown
@Bean如何完成对象的创建
1. 简单对象
能直接通过new的方式创建的对象，像是UserServiceImpl、UserDao等等

2. 复杂对象
不能通过new的方式创建的对象，像是SqlSessionFactoryBean、MapperScannerConfigure、Connection

方式理解:XML
<bean id="user" class="club.musician.entity.User"></bean>
这种通过XML的配置方式，是Spring调用User的构造器进行Bean对象的创建的

方式理解：@Bean
@Configuration
public class AppConfig{
	@Bean
	public User user(){return new User()}
}
其中@Bean添加的方法，方法签名上的User是需要创建Bean的类型，而这个user()方法名来讲，就等同于创建Bean的id，那方法体的实现就是，把创建对象的代码，书写在方法体中
```

#### 5.3.2 与FactoryBean的整合

```java
package annotation.deeplearning.bean.senior;

import annotation.deeplearning.bean.senior.entity.ConnectionFactoryBean;
import annotation.deeplearning.bean.senior.entity.TestEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class AppConfig {

    @Bean
    public TestEntity testEntity() {
        return new TestEntity();
    }

    /**
     * 创建一个复杂对象
     */
//    @Bean
//    public Connection connection() {
//        Connection connection = null;
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicianclub",
//                    "root", "********");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        return connection;
//    }

    /**
     * 整合FactoryBean
     */
    @Bean
    public Connection connection() {
        Connection connection = null;
        ConnectionFactoryBean connectionFactoryBean = new ConnectionFactoryBean();
        try {
            connection = connectionFactoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
```

#### 5.3.3 @Bean自定义id值

```mark
@Bean("user")//这个相当的简单的
```

#### 5.3.4 @Bean控制创建次数

```markdown
@Bean
@Scope("singleton|prototype")//这样就行了
```

#### 5.3.5 @Bean注解的注入

```markdown
XML方式:
<bean id="userDao" class="club.musician.dao.UserDao"></bean>
<bean id="userService" class="club.musician.service.impl.UserServiceImpl">
	<proeprty name="userDao" ref="userDao"></property>
</bean>

@Bean方式:
@Bean
public UserDao userDao(){
 sout("AppConfig.userDao");
 return new userDaoImpl();
}
@Bean
public UserService userServiceImpl(UserDao userDao){
 UserServiceImpl userServiceImpl = new UserServiceImpl();
 useServiceImpl.setUserDao(userDao);
 return userServiceImpl;
}
```

1.复杂对象的复杂实现

```java
package annotation.deeplearning.bean.senior;

import annotation.deeplearning.bean.senior.entity.ConnectionFactoryBean;
import annotation.deeplearning.bean.senior.entity.TestEntity;
import annotation.deeplearning.bean.senior.injection.UserDao;
import annotation.deeplearning.bean.senior.injection.UserDaoImpl;
import annotation.deeplearning.bean.senior.injection.UserService;
import annotation.deeplearning.bean.senior.injection.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class AppConfig {

    @Bean
    public UserDao userDao(){
        System.out.println("AppConfig.userDao");
        return new UserDaoImpl();
    }
    @Bean
    public UserService userService(UserDao userDao){
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        userServiceImpl.setUserDao(userDao);
        return userServiceImpl;
    }
}
```

测试代码

```java
@Test
public void testBeanInjection() {
  ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
  UserService userService = (UserService) context.getBean("userService");
  userService.register();
}
```

2.复杂对象的简单写法

```java
@Bean
public UserService userService(){
  UserServiceImpl userServiceImpl = new UserServiceImpl();
  //直接调用userDao()完事~
  userServiceImpl.setUserDao(userDao());
  return userServiceImpl;
}
```

3.@Bean(JDK类型的注入)

```java
@Bean
public User user(){
  User user = new User();
  user.setId(1);
  user.setName("SennerMing");
  return user;
}
```

这样耦合度太高了可以结合前面学习的@PropertySource+@Value解耦合

### 5.4 @ComponentScan注解

```markdown
@ComponentScan注解在配置bean中进行使用，等同于XML配置文件中的<context:component-scan/>标签
目的：进行相关注解的扫描(@Component @Value @Autowired....)
```

```java
@Configuration
@ComponentScan(basePackages="club.musician")
public class AppConfig{
}
```

1.排除

```xml
<context:component-sacn base-package="club.musician">
	<context:exclude-filter type="annotation" expression="org.springframework.stereotype.Service"/>
</context:component-scan>
```



```java
@Configuration
@ComponentScan(basePackage="club.musician",
              excludeFilters={@ComponentScan.Filter(type=FilterType.ANNOTATION,value={Service.class})})
public class AppConfig{
  
}
```

2.包含

```java
@ComponentScan(basePackages = {"annotation.deeplearning.bean.senior"},useDefaultFilters = false,
includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION.ANNOTATION,value = {Controller.class})}
)
```

### 5.5 Spring工厂创建对象的多种配置方式

#### 5.5.1 多种配置方式的应用场景

- @Component、@Repository、@Service、@Controller与@Autowired的组合主要应用到，咱们程序猿自己开发的Java类对象的创建。



- @Bean框架提供的类型，别的程序猿开发的类型(没有源码的)，像是SqlSessionFactoryBean、MapperScannerConfigure



- <bean>标签，在纯注解的开发过程中，基本不用，用到的场景一般都是遗留系统的整合



- @Import，@Import(User.class)，一般Spring框架的底层使用，还可以应用到多配置bean的整合上使用到



#### 5.5.2 配置的优先级

```markdown
@Component及其衍生注解  <  @Bean  <   配置文件bean标签
优先级高的配置，覆盖优先级低的配置

配置覆盖的过程中，id值，必须保持一致！否则创建多个Bean对象
```

**验证优先级中出现的问题就是**

```markdown
我们已经使用注解进行了开发
ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

那我们的配置文件怎么加载进来啊？
答：可以在AppConfig上加入@ImportResource("applicationContext.xml")，这样就能引入咱们写的Spring配置文件了
```

#### 5.5.3 注解配置解耦合

```java
//像是我们在程序中使用@Bean的注解
@Bean
public UserDao userDao(){
  return new UserDaoImpl();
}
//这样的话，就产生了强相关，耦合度特别高
```

按照我们上面的方式，在原有的AppConfig上面添加@ImportResource，这样，就还是对原有的代码进行了修改，不符合开闭原则，对修改关闭，对扩展开放，所以我们要新添加一个AppConfig1，在这个AppConfig1上使用@ImportResource注解引入我们的新加的配置文件，那工厂怎么创建呢？有多个配置Class

```java
ApplicationContext ctx = new AnnoatationConfigApplicationContext(AppConfig.class,AppConfig1.class);

//还有一种写法就是直接扫描包就行了,指定一个basePackages
ApplicationContext ctx = new AnnotationConfigApplicationContext("club.musician");
```

#### 5.5.4 整合多个配置信息

```markdown
为什么会有多个配置信息？
答：为了后期的维护，有持久化相关的配置信息，有自定义配置类的

那么拆分多个配置bean的开发，是一种模块化开发的形式，也体现了面向对象各司其职的设计思想
```

那么有了拆分自然就有了整合

```markdown
整合的方式：
1. 多配置类的整合

2. 配置类和@Component及相关衍生注解的整合

3. 与Spring的配置文件XML进行整合，进行配置覆盖，或为了整合遗留系统
```

```markdown
多配置整合的要点
1. 如何使多配置的信息，汇总成一个整体

2. 如何实现跨配置的注入
```

一、多配置信息

```markdown
1.多个@Configuration，使用AnnotationConfigApplicationContext("指定basePackages")；

AnnotationConfigApplicationContext(AppConfig.class,AppConfig1.class);

很像之前我们的种写法ClassPathXmlApplicationContext("application-*.xml");
```

```markdown
2.主配置，在AppConfig中使用@Import(AppConfig1.class)，他和这个<import resource="">是很相似的
```





二、跨配置注入

```markdown
1.写成员变量，并加上@Autowired
```

```java
@Repository
public class UserDaoImpl implements UserDao{}

@Configuration
@ComponentScan(basePackages="")
public vlass AppConfig3{
  
  @Bean
  public UserService userService(){
    UserServiceImpl userService = new UserServiceImpl();
    return userService;
  }
}
```



三、遗留系统整合、配置覆盖

```java
public class UserDaoImpl implements UserDao{}
//<bean id="userDao" class="club.musician.dao,.impl.UserDaoImpl"> //applicationContext.xml

@Configuration
@ImportResource("applicationContext.xml")
public class AppConfig4{
  
  @Autowired
  private UserDao userDao;
  
  @Bean
  public UserService userService(){
    UserServiceImpl userService = new UserServiceImpl();
    userService.setUserDao(userDao);
    return userService;
  }
}

ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig4.class);

```

### 5.6 配置Bean的底层原理

我们来回顾下@Configuration的使用方式

```java
//之前是创建了一个AppConfig的类，在类上打上了@Configuration的标签
@Configuration
public class AppConfig{
  @Bean
  public User user(){
    return new User();
  }
}
//然后在测试类中，可以进行多次的获取
@Test
public void testAppConfig(){
  ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
  User user0 = (User)ctx.getBean("user");
  User user1 = (User)ctx.getBean("user");
}
```

```markdown
	那我们来分析分析啦，Spring想要创建这个User的Bean对象，是不是得从配置类中，调用那个加了@Bean的user()方法呀，里面我们是直接通过new的方式创建的呀？那他如果是通过调用了加了@Bean的user()方法的话，是不是应该等同于用new的方法，两次创建了User的Bean对象呀？

	但是事实上并不是这样的，作为程序猿来讲，我们做了创建对象的这个代码逻辑，而作为Spring来讲，他做的事控制对象的创建次数，Spring通过他自己的方式，为我们的Bean做了自己的特殊功能，那这个不就是AOP的开发嘛？Spring通过代理的方式，引入了代码的创建的额外功能，为我们的原始类做了增强。

	那Spring的AOP编程有两种方式实现代理，一种是JDK的方式，还有一种是CGlib的方式，那我们的@Bean加的那个User方法，并没有实现任何接口，那Spring的底层是使用的CGlib为我们创建的User类的代理Bean对象。
```



### 5.7 四维一体的开发思想

```markdown
一、何谓四维一体
Spring开发一个功能的四种形式，虽然开发的方式不同，但是最终效果是一样的。
1. 基于schema
2. 基于特定功能注解的
3. 基于原始<bean>标签
4. 基于@Bean注解
```

```java
//==============================基于Schema与特定功能注解========================
//我有个xxx.properties文件   [id=10,name=SennerMing]

@Component("user")
public class User implements Serializable{
  private Integer id;
  private String name;
}

//早期解决方案
//<context:property-placeholder location="xxx.properties"/>
//现在的解决方案
//@PropertySource("classpath:/xxx.properties")

/** 那么无论是早期的还是现在的，这种Proterties的注入，底层的实现都是由PropertySourcesPlaceholderConfigurer来完成的 */

//由这个PropertySourcesPlaceholderConfigurer类的注解来看，其就是<context:property-placeholder>的底层支持，我们直接使用这个类进行开发我们的properties的键值对的使用啊？
```

基于原始的bean标签：PropertySourcesPlaceholderConfigurer的使用

```xml
<bean id="propertyHolder" class="org.springframework.context.support.PropertySourcesPlaceholderConfigurer">
  <property name="location" value="classpath:xxx.properties"></property>
</bean>
```

还可以使用@Bean进行配置

```java
@Configuration
@ComponentScan(basePackages="")
public class AppConfig{
  @Bean
  public PropertySourcesPlaceholderConfigurer propertyHolder(){
   PropertySourcesPlaceholderConfigurer propertyHolder = new PropertySourcesPlaceholderConfiguer();
    propertyHolder.setLocation(new ClassPathResource("xxx.properties"));
    return propertyHolder;
  }
}
```



```markdown
<context:property-placeholder> 推荐用 @PropertySource
<bean id="" class=""> 推荐使用 @Bean
```

### 5.8 纯注解AOP编程

所在包：annotation.aop

```markdown
四步开发
1. 原始对象
2. 额外功能
3. 切入点
4. 切入点额外功能组装

基于注解的话，2、3、4步会被整合到一起：组装切面

我们之前还需要在Spring中添加<aop:aspectj-autoproxy/>开启Spring的注解开发的功能
```

**注意：在SpringBoot中 这个@EnableAspectjAutoProxy(proxyTargetClass=true)已经设置好了**

Spring AOP代理默认是JDK的，SpringBoot AOP代理默认是CGlib的

### 5.9 注解版Spring+Mybatis

Druid连接池的配置

```java
package annotation.aop.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "jdbc.properties")
public class DruidConfig {

    @Value(value = "${prop.driverClass}")
    private String driverClass;

    @Value(value = "${prop.url}")
    private String url;

    @Value(value = "${prop.username}")
    private String userName;

    @Value(value = "${prop.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }
}
```

Mybatis的SqlSessionFactoryBean还有MapperScannerConfigure

```java
package annotation.aop.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
public class MybatisConfig {

    //1.SqlSessionFactory
    //  1.dataSource
    //  2.mapperLocation
    // 注意这个DataSource要通过在方法参数中进行注入
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setTypeAliasesPackage("annotation.aop.entity");
        sqlSessionFactoryBean.setMapperLocations(new ClassPathResource("/mapper/UserDAOMapper.xml"));
        return sqlSessionFactoryBean;
    }

    //2.MapperScannerConfigure
    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        //下面这句其实可以不用
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("annotation.aop.dao");
        return mapperScannerConfigurer;
    }
}
```

**注意：MapperScannerConfigure的通配写法**

```java
//1.SqlSessionFactory
//  1.dataSource
//  2.mapperLocation
@Bean
public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
  SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
  sqlSessionFactoryBean.setDataSource(dataSource);
  sqlSessionFactoryBean.setTypeAliasesPackage("annotation.aop.entity");
  //        sqlSessionFactoryBean.setMapperLocations(new ClassPathResource("/mapper/UserDAOMapper.xml"));
  ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
  Resource[] resources = resourcePatternResolver.getResources("classpath:/mapper/*Mapper.xml");
  sqlSessionFactoryBean.setMapperLocations(resources);

  return sqlSessionFactoryBean;
}
```

### 5.10 纯注解版事务编程

```markdown
1. 原始对象
2. 额外功能
3. 事务属性
4. 事务的配置
```

### 5.11 Spring框架中使用YML

```markdown
YML(YAML)是一种新形势的配置文件，比XML更简单 ，比properties更强大
```

properties进行配置的问题

```markdown
1.Properties表达过于繁琐，无法表达数据的内在联系
2.Properties无法表达对象、集合类型
```

```java
//jdbc.driverClass=xxxxx
//jdbc.url=xxxx
//jdbc.username=xxxxx
//jdbc.password
//1. 都是JDBC开头
//2. 那这个properties和那个java bean有联系呢？不看代码你不知道

//看看YML
//mybatisProperties:
//	driverClass: xxxxx
//	url: xxxx
//	username: xxxx
//	password: xxxxx
//1. 能看的出来是MybatisProperties这个java bean的配置
//2. 减少了配置的冗余
```

YML语法简介

```markdown
1. 基本语法
name: SennerMing
password: 123456

2. 对象概念
account: 
	id: 10
	password: 123456

3. 定义集合
这是List集合
service:
	- 11111
	- 22222
```

```markdown
之前是用PropertySourcePlaceHolderConfigurer把properties中的键值对注入我们的Bean中，他是封装到Properties的这个集合中，我们记着这是一个类对吧。
1. 那YML是怎么被读取呢？

2. 怎么把YML转Properties呢？
```

```markdown
这就需要借助Spring的YamlPropertiesFactoryBean来负责读取Properties，他会将我们的YAML文件读取到Properties整理好就读取到咱们的yaml中
```

开发

```markdown
思路：
1. 准备YAML文件
2. 读取YAML 转换成 Properties
YamlPropertiesFactoryBean.setResource(yaml配置文件的路径) ---> new ClassPathResource();
YamlPropertiesFactoryBean.getObject() -----> Properties;
3. 应用PropertySourcesPlaceholderConfigurer.setProperties()
4. 使用@Value进行注入
```

环境搭建

```xml
<dependency>
  <groupId>org.yaml</groupId>
  <artifactId>snakeyaml</artifactId>
  <version>1.27</version>
</dependency>
```

Yaml文件

```yaml
```

在配置Bean中操作，完成YAML的读取，与PropertySourcePlaceholderConfigurer的创建

```java
```

在Bean中使用@Value注入



