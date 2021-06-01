

# Spring AOP 学习

## 1 静态代理设计模式

```markdown
问题： 
		一、那么为什么需要代理设计模式？
		答： 在JavaEE分层开发中，哪个层次对我们来讲最重要？
				|- DAO
				|- Service  	可以说业务层是核心，做任何一个项目，写任何一个程序，都是为了满足用户的需求
				|- Controller
				
		二、Service层中包含了那些的代码？
		答：DAO的使用、一些事务的操作、等等
				|- 核心功能   [业务|运算|DAO的操作] 比重最大
				|- 附加功能		[事务|日志|性能]	在项目中应用场景相较于核心业务较小
```

那么针对上面的问题，对Service中的功能稍微展开一下

```markdown
日志
所谓的日志，就是记录谁+时间+操作+结果=用户重要操作的流水账，比如Account1在某年某月某日某时某分给Account2进行100块钱的转账操作成功

性能
对核心性能进行监控，一般是对运算耗时的记录，在开发阶段进行监控，上线的时候可能就不需要了
```

额外功能书写在Service层中好不好？

```markdown
Service层的调用者角度（Controller）：需要在Service层中需要额外功能，例如事务
那么站在设计角度来看：Service层不太需要额外的功能
这就在Service层中产生了一定的矛盾
```

那么在现实生活场景中的解决方案

``` markdown
想想我们刚毕业的时候，想在我们上学的地方立足，找工作，当上总经理，赢取白富美，但是首要的问题是：学校宿舍不让住了呀，那我们就先租房子吧！
1. 业务梳理
房客 - 调用者  功能梳理 [需要租房|交钱]
房东 - 业务层	功能梳理 [张贴广告|带人看房|签合同|收钱]
							核心功能  [签合同|收钱]
							额外功能  [张贴广告|带人看房]
2. 矛盾产生
房东(软件的设计者)：我不想到处去贴广告，我房子那么多，每一套都让我带你去看房子，我工作要不要啦？
房客：不行啊，你不贴广告，不带我去看，我怎么给你签合同啊？！
			
3. 问题解决
这样房客和房东的矛盾就产生了，眼看着就要打起来了！
为了解决这样的矛盾，现实生活中就产生了一个职业，那就是中介(Proxy)
那么中介就必须有同样的功能(房东的额外功能)： [张贴广告|带人看房]
			
4. 流程梳理
现实中的租房流程：
房客  ----根据广告--->  中介(Proxy)  -----带去看房----
																	------比较满意---->  房东  -----签合同、交钱---->
那么我们对附加的功能不满意，怎么办？好办呀，我们直接换个更好的中介(Proxy)就行啦
			
5. 进行一个小小的总结
租房问题在现实中在引入中介(Proxy)的情况下得以解决，主要是将矛盾转移到了第三方，房客可以通过中介(proxy)访问到最终要出租房屋的目标类(原始类)房东，从而租到了房子，那么中介(Proxy)这个角色，服务双方不满意的话，可以直接换掉
```

### 1.1 代理设计模式

#### 1.1.1 相关概念

```markdown
通过代理类，为原始类增加额外的功能，只负责核心业务逻辑的Service就是原始类，那么通过代理类（Proxy）为目标类或者叫原始类增加额外功能
```

#### 1.1.2 相关名词的解释

```markdown
1. 目标类 原始类
			指的是业务类（核心功能 ---> 业务运算 DAO的调用）
2. 目标方法 原始方法
			指的是目标类（原始类）中的一些方法，就是目标方法（原始方法）
3. 额外功能(附加功能)
			日志，事务，性能
```

#### 1.1.3 代理开发的核心要素

```markdown
1.代理类的开发，首先得有目标类，我们为谁添加额外功能，我们要有个目标对象啊，还要确定要为目标类添加哪些额外功能，还要确保，代理类中的方法要和目标类（原始类）中的方法一一对应

代理类 = 目标类（原始类） +  额外功能  +  (一个隐含要素，确保代理方法的对应)
```

### 1.2 基本代理模式的实现

#### 1.2.1 目标类(原始类)

```java
package aop;

public class UserServiceImpl implements UserService {

    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
```

#### 1.2.2 代理类

```java
package aop;
//1.要有目标类对象
//2.要有额外的功能
//3.要实现相同接口
public class UserServiceProxy implements UserService{

    //为目标类增加功能，那我们就得有目标类
    private UserServiceImpl userService = new UserServiceImpl();

    @Override
    public void register(User user) {
        System.out.println("UserServiceProxy.register logging .....");
        userService.register(user);
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceProxy.login logging .....");
        return userService.login(name, password);
    }
}
```

#### 1.2.3 调用测试

```java
@Test
public void testSimpleProxy() {
  UserService userService = new UserServiceProxy();
  userService.login("SennerMing", "123456");
  userService.register(new User());

  OrderService orderService = new OrderServiceProxy();
  orderService.showOrders();
}
```

#### 1.2.4 小结

上面的这种代理方式，我们成为**静态代理**，最核心的就是为每一个原始类，手工编写一个代理类

```markdown
1. 那么静态代理方式存在的问题
		静态类文件数量过多，不利于项目的管理
    UserServiceImpl --> UserServiceProxy
    OrderServiceImpl --> OrderServiceProxy
    
    问题一、那么从业务逻辑上来看，这种类的出现有没有意义呢？
    			是有意义的，因为确实能完成日志记录的额外功能
    			
    问题二、那么从程序设计的逻辑上来看，这种类的出现有没有意义呢？
    			没有，因为增加的这些代理类，都是为了进行日志的记录，反而这种数量过多从而不利于项目的管理，比如，我们想修改下我们日志记录的方式，比如增加更多的逻辑，比如将日志进行存储用于数据分析，那我们要改的东西就多了，额外功能维护性太差了！
```

## 2 Spring动态代理

概念和静态代理完全相同

```markdown
概念：通过代理类为原始类（目标类）增加额外的功能
好处：利于原始类（目标类）的额外功能的维护
```

### 2.1 搭建开发环境

```xml
<!-- Spring动态代理开发依赖 -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-aop</artifactId>
  <version>5.3.6</version>
</dependency>
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjweaver</artifactId>
  <version>1.9.5</version>
</dependency>
<dependency>
  <groupId>org.aspectj</groupId>
  <artifactId>aspectjrt</artifactId>
  <version>1.9.6</version>
</dependency>
```

### 2.2 进行开发

#### 2.2.1 创建原始对象(目标对象)

就使用静态代理的UserService

```java
package aop;

public class UserServiceImpl implements UserService {

    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
```

Spring的配置(proxy.xml)

```xml
<bean id="userService" class="aop.UserServiceImpl"></bean>
```

#### 2.2.2 额外的功能

Spring给我们提供了一个接口：MethodBeforeAdvice

```markdown
1.额外功能书写在接口的实现中，看这个接口的名字，就不难猜出，我们在其中书写的代码逻辑
		会运行在原始方法执行之前
```

创建自己的MethodBeforeAdvice的实现

```java
package aop.dynamic;

import org.springframework.aop.MethodBeforeAdvice;
import java.lang.reflect.Method;

public class MyBefore implements MethodBeforeAdvice {

    /**
     * 需要把运行在原始方法执行之前运行的额外功能，书写在before中
     * @param method
     * @param objects
     * @param o
     * @throws Throwable
     */
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("--------- Method before advice log --------------");
    }
}
```

在Spring配置文件中注册

```xml
<bean id="myBefore" class="aop.dynamic.MyBefore"></bean>
```



#### 2.2.3 定义切入点

```markdown
切入点：额外功能需要添加的位置

目的：由程序员根据自己的需求，决定额外功能加给哪个原始方法
UserService.register、UserService.login

简单的测试：将所有方法都作为切入点，都加入额外的功能
```

这需要在Spring配置文件中进行配置

```xml
<aop:config>
  <!--     
      这个pointcut就是我们刚才所说的切入点，
          id:先随便取个名字;
          expression:切入点表达式;下面这个写法，就表达的是所有的方法
   -->
  <aop:pointcut id="pc" expression="execution(* *(..))"/>
</aop:config>
```



#### 2.2.4 组装

将第2和第3步进行整合，额外功能+切入点，也是Spring配置文件的整合

```xml
<!--
    将额外功能和上面的切入点(pointcut)进行整合
-->
<aop:advisor advice-ref="myBefore" pointcut-ref="pc"></aop:advisor>
```



#### 2.2.5 调用

```markdown
调用的目的：获得Spring工厂创建的动态代理对象，并进行使用
		ApplicationContext ctx = new ClassPathXmlApplicationContext("proxy.xml");

注意：
		1.Spring工厂通过院士对象的id值，获得的是代理对象，此时我们的原始对象是UserService
		2.通过原始对象id进行代理对象的获取，那么可以通过声明的接口类型，进行代理对象的存储
		
		UserService userService = ctx.getBean("userService");		
```

```java
@Test
public void testDynamic() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("proxy.xml");
  UserService userService = (UserService) applicationContext.getBean("userService");
  userService.login("SennerMing", "123465");
  userService.register(new User());
}
//运行结果
//		--------- Method before advice log --------------
//		UserServiceImpl.login 业务逻辑 + DAO
//		--------- Method before advice log --------------
//		UserServiceImpl.register  业务逻辑 + DAO
```



#### 2.2.6 小结

原始类只进行核心业务的实现;

额外功能书写我们要给原始类增加的额外功能的逻辑;

切入点就是，要将额外功能需要添加的位置;

组装将额外功能和切入点进行整合



### 2.3 动态代理分析

```markdown
动态代理的相关问题：
1. Spring创建的动态代理类在哪？
答：回顾我们的开发流程，首先我们创建了原始类，接着实现了MethodBeforeAdvice书写了咱们的额外功能，然后配置了额外功能需要执行的切入点，最后进行组装，在开发过程中，我们并没有看到什么代理类的影子，在调用的时候，通过目标类的ID获得到的Bean对象却是这个目标类的代理。
这个Spring框架在运行时，通过动态字节码的技术，这个代理类是在JVM创建，运行在JVM内部，等程序结束后，会和JVM一起消失。

2. 什么叫动态字节码技术？
答：传统上，我们Java使用一个类的对象，其实是通过这个类的字节码生成这个Object对象，我们常说的一次编译，到处运行，那么这个一次编译，就将.java文件编译成.class文件，这个.class文件中就是存放咱们类的字节码。
JVM可以通过动态字节码的技术，既不用我们写.java，也不用我们将.java编译为.class，直接在JVM内部生成类对象，这种牛批的技术，我们可以使用第三方的动态字节码框架来进行使用。

3. 动态字节码框架有哪些？
答：ASM、Javassist、Cglib，生成动态字节码，我们的动态代理类其实就是一些动态字节码，我们就能基于这个动态代理类创建代理类对象进行使用。

小结论：
动态代理不需要定义类文件，都是JVM运行过程中动态创建的，所以不会造成静态代理中，类文件数量过多的，影响项目管理的问题。
```



### 2.4 简化代理开发

上面我们已经对UserService这个业务增加了日志代理(MethodBeforeAdvice)的开发与配置(Pointcut+Advice)，那么我们还想对OrderService增加日志的额外功能。

那我们只需要在Spring配置文件中新增OrderService的Bean的配置就行了

```xml
<bean id="orderService" class="aop.OrderServiceImpl"></bean>
```

因为我们的额外功能没变、切入点是所有的方法、组装（Advice+Pointcut都没变），什么都没有变化，所以新增一个业务的代理就变得很简单了

```java
@Test
public void testOrderDynamic() {
  ApplicationContext context = new ClassPathXmlApplicationContext("proxy.xml");
  OrderService orderService = (OrderService) context.getBean("orderService");
  orderService.showOrders();
}
```

在额外功能不变的前提下，创建其他目标类的代理对象时，只需要配置目标类就行，额外功能的维护性大大增强了，进行中介的更换的话也是非常方便的，我们只需要将咱们的MethodBeforeAdvice的实现进行更换就行了

```xml
<!--    <bean id="myBefore" class="aop.dynamic.MyBefore"></bean>-->
<bean id="myBefore" class="aop.dynamic.MyBefore1"></bean>
```



### 2.5 动态代理详解

```markdown
1. Spring动态代理开发，主要的四个步骤
- 目标对象
- 额外功能
- 切入点
- 组装
```

#### 2.5.1 额外功能的详解

```markdown
1. 实现了MethodBeforeAdvice的接口都要进行void before(Method method,Object[] agrs,Object target)的实现
接口的最核心的作用，是添加需要在原始方法之前要执行的额外功能
问题： 
1. 那么这些参数都是几个意思呢？
答：
Method method: 
	额外功能所增加给的那个原始方法，就像是UserService.login()、UserService.register()，这个东西是变化的，取决于我们要增加哪个
Object[] args:
	额外功能所增加给的那个原始方法的参数，如果增加的是UserService.login()，那么args[name,password]；如果是UserService.register()的话，那么args[new User()]，这个参数和第一个要增加的Method是息息相关的
Object target:
	额外功能所增加给的那个原始对象，就是UserServiceImpl或者OrderServiceImpl

2. 这三个参数在实际应用当中，又该如何使用呢？
答：根据实际需要进行使用，不一定都会用到

这种MethodBeforeAdvice在生产中使用的情况还是比较少的，Spring中还有更强大的功能实现
```

#### 2.5.2 方法拦截器

```markdown
方法拦截器的接口：MethodInterceptor

问题：那么它和MethodBeforeAdvice相比，有什么优势呢？
与MethodBeforeAdvice相比，这个MethodInterceptor方法在原始方法的前后都可以进行增强
```

代码实现

```java
package aop.dynamic;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Around implements MethodInterceptor {

    /**
     * invoke方法：
     *      额外功能书写在其中
     *
     * ==============================================================
     * 那么这个额外功能的执行时机：
     *
     *      原始方法执行 之前
     *      原始方法执行 之后
     *      原始方法执行 前 后
     *
     *==============================================================
     * 那么我们怎么判断这个额外功能的执行时机呢？
     *
     *      首先就要确定这个原始方法是怎样运行的
     *
     *      参数：
     *            MethodInvocation（Method）：额外功能所增加给的那个原始方法
     *            那有可能是UserService.login或UserService.register
     *
     *      methodInvocation.proceed()就代表着原始方法的运行！ [UserService.login()|UserService.register()]
     *
     *      我们知道原始方法是怎样运行的，就可以确定我们这个额外功能的运行时机
     *
     *==============================================================
     *那么这个返回值怎么理解？
     *      Object：原始方法的返回值
     *      如果我们MethodInvocation是UserService.login()还是UserService.register()
     *
     *      Object ret = methodInvocation.proceed()
     *
     * ==============================================================
     * 事务就是典型的额外功能
     *      UserServiceImpl{
     *          register(){
     *
     *          }
     *      }
     *
     *      tx.begin()
     *      userServiceImpl.register();
     *      tx.commit()
     *
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        System.out.println("MethodInterceptor before real method invoke...");
        Object ret = methodInvocation.proceed();
      	System.out.println("MethodInterceptor after real method invoke...");
        return ret;
    }
}
```

Spring的配置

```xml
<bean id="userService" class="aop.UserServiceImpl"></bean>

<bean id="orderService" class="aop.OrderServiceImpl"></bean>

<!--    <bean id="myBefore" class="aop.dynamic.MyBefore"></bean>-->

<!--    <bean id="myBefore" class="aop.dynamic.MyBefore1"></bean>-->

<bean id="around" class="aop.dynamic.Around"/>

<aop:config>
  <!--
            这个pointcut就是我们刚才所说的切入点，
                id:先随便取个名字;
                expression:切入点表达式;下面这个写法，就表达的是所有的方法
         -->
  <aop:pointcut id="pc" expression="execution(* *(..))"/>
  <!--
            将额外功能和上面的切入点(pointcut)进行整合
         -->
  <!--        <aop:advisor advice-ref="myBefore" pointcut-ref="pc"></aop:advisor>-->
  <aop:advisor advice-ref="around" pointcut-ref="pc"></aop:advisor>

</aop:config>
```

原始代码存在异常怎么办？

```java
public class UserServiceImpl implements UserService {

    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
        throw new RuntimeException("存在异常！");
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
```

调用TestProxy中的testDynamic()，查看运行结果

```markdown
MethodInterceptor before real method invoke...
UserServiceImpl.login 业务逻辑 + DAO
MethodInterceptor after real method invoke...
MethodInterceptor before real method invoke...
UserServiceImpl.register  业务逻辑 + DAO
java.lang.RuntimeException: 存在异常！
MethodInterceptor after real method invoke...
```

需要注意的细节

```markdown
1. MethodInterceptor对方法返回值的影响
如果我们将原始方法的返回值，直接作为invoke()方法的返回值返回，那么MethodInterceptor不会影响原始方法的返回值的
想要进行影响 - 就不要用原始方法的返回值即可
```

## 3 切入点的详解

```markdown
切入点决定了额外功能添加的位置(方法)

1.	先看看我们之前的写法
<aop:pointcut id="pc" expression="execution(* *(..))">
execution(* *(..)) --> 匹配了所有的方法，那么后面如果我们有三个方法 MethodA、MethodB、MethodC，那么我们只想在A和C上添加额外功能那，又该怎么办呢？

1.execution() 	切入点函数
2.* *(..)				切入点表达式
```

### 3.1 切入点表达式

```markdown
1.方法切入点表达式
问题来了，这个* *(..)表达式，为什么会匹配了所有的方法呢？
那我们先得从发方法的定义上来看了

public  void      add   (int i,int j)
修饰符	 返回值类型	方法名	   参数列表
第一个 * 对应的是[修饰符和返回值] 
第二个 * 对应的是方法的名字
()就代表着参数表
..代表着对参数没有要求(参数有没有，参数有几个，参数是什么类型的都行)

2.针对方法名的切入点
定义login方法作为切入点
* login(..)

定义register作为切入点
* register(..)

3.针对参数的切入点
定义login方法且login方法有两个字符串类型的参数作为切入点
* login(String,String)

4.如果参数的类型不是java.lang包下的呢？
像是Uservice.register(User user),必须要写全限定名

5.那么这个* login(String,..)表达式的意思是什么呢？
可以匹配的方法有login(String),login(String,String),login(String,aop.User);
```

上面的这些个切点表达式它不精准

```markdown
项目结构：
		|-package a
				|- UserServiceImpl1
						|- User login(String userName,String password)
						|- boolean login(User user)
						|- boolean register(User user)
				|- UserServiceImpl2
						|- User login(String name,String password)
		|-package b
				|- UserServiceImpl
						|- login(String name,String password)
						|- register(User user)
那么我们的[* login(..)]这个的切点表达式，上面的login方法全都匹配上了

这样我们写的表达式就显得表达不足了
		还缺了包、类等的描述
```

### 3.2 完整切入点表达式

精准表达式

```markdown
完整的切入点表达式包括
修饰符返回值 + [ 包 | 类 & 方法(参数) ]

精确的切入点
* aop.UserServiceImpl.login(..)
* aop.UserServiceImpl.login(String,String)
```

#### 3.2.1 类切入点

指定特定的类作为切入点（额外功能加入的位置），自然这个类中的所有方法，都会加上对应的额外功能

```markdown
项目结构：
|- aop
		|-package a
				|- UserServiceImpl
						|- User login(String userName,String password)
						|- boolean login(User user)
						|- boolean register(User user)
				|- UserServiceImpl2
						|- User login(String name,String password)
		|-package b
				|- UserServiceImpl
						|- login(String name,String password)
						|- register(User user)
我们只想给UserServiceImpl的类中所有的方法变成我们的切入点，一个一个方法的写？
* a.UserServiceImpl.*(..)

新的需求，以包作区分
想让a.UserServiceImpl还有b.UserServiceImpl作为切入点
莫非是，* *.UserServiceImpl.*(..)
但是实际运行的情况，并没有给他们的方法中加入了额外的代码，他一个*只代表了一层包，如果按照上面的包的逻辑来看，就没法执行了，那么最终的切入点表达式就是 * *.*.UserServiceImpl.*(..)

还有甭管多少层级的UserServiceImpl的所有方法都要进行切入
* *..UserServiceImpl.*(..)
```

#### 3.2.2 包切入点

```markdown
实战价值更有意义的
项目结构
|- musician.club
		|- package a
					|- UserServiceImpl
								|- User login(String name,String password)
								|- boolean login(User user)
								|- boolean register(User user)
					|- UserServiceImpl1
								|- User login(String name,String password)
		|- package b
					|- UserServiceImpl
								|- login(String name,String password)
								|- register(User user)
只想对a包进行切入，那么下面的写法，包中的所有类都进行了切入，但不包括a包的子包！
* musician.club.a.*.*(..)

那么让这个a包还有其子包全部都生效，该怎么做呢？
* musician.club.a..*.*(..)
```

### 3 .3 切入点函数

#### 3.3.1 execution

```markdown
execution:是最为重要的切入点表达式，功能最齐全
执行[方法|类|包]的切入点表达式

弊端：execution执行切入点表达式，书写起来比较麻烦
     execution(* musician.club.aop..*.*(..))

注意：其他的切入点函数，仅仅是起到了简化了execution书写的复杂度，功能上是完全一致的
```

#### 3.3.2 args

```markdown
args:主要用于函数（方法）参数的匹配

示例：方法名是啥我不关心，但是方法的参数必须是两个字符串的参数
execution的写法	- execution:(* *(String,String))
args的写法				- args(String,String)
```

#### 3.3.3 within

```markdown
within:主要用于进行类、包切入点的表达式

切入点：不管在哪个包下面的UserServiceImpl这个类，进行切入			← package
execution(* *..UserServiceImpl.*(..))
within(*..UserServiceImpl)

如果要选择包作为切入点进行切入																	package →
execution(* musician.club.aop..*.*(..))									
within(musician.club.aop..*)
```

#### 3.3.4 @annotation

```markdown
@annotation:主要作用是为具有特殊注解的方法加入额外功能

来看
UserServiceImpl implements UserService{
		@Log(比如这个就是我们的特殊注解)
		@Override
		public void register(User user){}
		
		@override
		public void login(String name,String password){} 
}
那么@annotation就回去扫描去为这个加了@Log注解的方法进行额外功能的增加
```

 自定义注解

```java
package aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//决定了注解加到哪里去
@Target(ElementType.METHOD)
//决定了这个注解在什么时间起作用
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

}
```

Spring切入点配置的修改，proxy.xml

```xml
<aop:config>
  <!--
            这个pointcut就是我们刚才所说的切入点，
                id:先随便取个名字;
                expression:切入点表达式;下面这个写法，就表达的是所有的方法
         -->
  <!--        <aop:pointcut id="pc" expression="execution(* *(..))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* login(..))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* login(String,String))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* login(String))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* login(String,..))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* aop.UserServiceImpl.login(String,..))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* aop.UserServiceImpl.*(..))"/>-->
  <!--        <aop:pointcut id="pc" expression="execution(* *.UserServiceImpl.*(..))"/>-->
  <!--        <aop:pointcut id="pc" expression="args(String,String)"/>-->
  <!--        <aop:pointcut id="pc" expression="within(*..UserServiceImpl)"/>-->
  <aop:pointcut id="pc" expression="@annotation(aop.annotation.Log)"/>
  <!--
            将额外功能和上面的切入点(pointcut)进行整合
         -->
  <!--        <aop:advisor advice-ref="myBefore" pointcut-ref="pc"></aop:advisor>-->
  <aop:advisor advice-ref="around" pointcut-ref="pc"></aop:advisor>

</aop:config>
```

测试代码还是TestProxy.testDynamic()

#### 3.3.5 切入点函数的逻辑运算

```markdown
切入点函数的逻辑运算：指的是整合多个切入点函数一起配合工作，进而完成更为复杂的需求
and操作
	案例1：方法名得叫login，参数还得是两个字符串
	1.execution(* login(String,String))
	2.execution(* login(..)) and args(String,String)
	
	and操作不能用于同种类型的切入点函数的组合
	案例2：register()和login()方法作为切入点
	execution(* login(..)) and execution(* register(..)) 这种写法能行吗？不好意思绝对不行,怎么可能是两种条件同时满足，这个方法既叫login又叫register？
	
or操作
execution(* login(..)) or execution(* register(..))就能解决上面的案例2
```

### 3.4 Spring动态代理的小结

```markdown
意义：通过代理类为目标类（原始类）增加额外的功能
好处：利于目标类（原始类）的维护

Spring动态代理开发的四个步骤
|- 目标对象 UserService的实现类UserServiceImpl
|- 额外功能 实现MethodInterceptor，实现这个接口中对应的invoke(MethodInvocation)的方法，可以影响原函数的返回值
|- 切入点 	分为两大部分
			切入点表达式[返回值 & (包|类) & 方法(参数)]
			切入点函数：execution、args、within、@annotation
|- 组装		将Advice和Pointcut进行组合
```

## 4 AOP编程

### 4.1 概念

```markdown
AOP		(Aspect Oriented Programing) 		面向切面编程
以切面为基本单位的程序开发，通过切面间彼此协同，相互调用，完成程序的构建

OOP		(Object Oriented Programing) 		面向对象编程		Class对象		Java
以对象为基本单位的程序开发，通过对象间的彼此协同，相互协同，完成程序的构建
UserService UserDao等等等等

POP		(Producer Oriented Programing) 	面向过程编程		方法、函数		C
以过程为基本单位的的程序开发，通过过程间的彼此协同，相互调用，完成程序的构建

那么对概念有了一定的了解了，我们就需要理解什么是切面了
答：切面 = 切入点 + 额外功能，那么上面的动态代理的开发，就等同于我们这个面向切面的编程

```

小小的总结

```markdown
什么是AOP编程啊？
Spring的动态代理开发，通过代理类为原始类增加额外的功能。
好处：利于原始类的额外功能的维护

注意：有了AOP编程，并不是取代了OOP的编程，是在OOP的基础上发展而来的，是对OOP的有益的补充！
```

### 4.2 AOP编程的开发步骤

```markdown
其实本质上就是Spring的动态代理的开发
1.原始对象的实现
2.额外功能（MethodInterceptor）
3.切入点
4.组装切面（额外功能+切入点）
```

### 4.3 名词的解释

```markdown
切面 = 切入点 + 额外功能
切面它为什么叫切面呢？
//闲扯
从几何学的角度
		面 = 点 + 相同的性质
		
我们将我们的UserServiceImpl想象成一个柱体，OrderServiceImpl也想象成一个柱体，那么这个一个一个的柱体是由什么构成的呢？是由一个一个service中的方法进行构成的，那么这些个方法，有很多都是使用DAO层进行数据库的调用，那么就都需要事务的额外功能，那么这些个方法（具有相同性质的点）就构成了一个面，那么这个面就存在于这些个圆柱体中（Service层中）
```

## 5 AOP的底层实现原理

### 5.1 核心问题

```markdown
1.Spring的AOP如何创建这些个动态代理类的？（动态字节码技术）

2.Spring的工厂是如何加工创建代理对象的？
	通过原始对象的id值，获得的是这个Bean对象却是代理对象
```

### 5.2 动态代理类的创建

#### 5.2.1 JDK的动态代理

代码实现

```java
package aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestJDKProxy {
		/**
     * 1.ClassLoader
     *      可以借用TestJDKProxy的，也可以是UserServiceImpl的
     * 
     * 2.JDK 1.8 之前
     *      如果内部类需要访问外部类对象需要将外部类对象声明为final
     *      final UserService userService = new UserServiceImpl();
     * @param args
     */
    public static void main(String[] args) {
        //创建原始对象
        UserService userService = new UserServiceImpl();
        //通过这个接口类，告诉动态代理的方法，动态代理类该实现哪些方法
        Class[] interfaces = userService.getClass().getInterfaces();
        //JDK创建动态代理
        UserService userServiceProxy = (UserService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), interfaces, new InvocationHandler() {
            /**
             *      Proxy 		先忽略掉，代表的是代理对象
             * 		Method 		额外功能所增加给的那个原始的方法
             * 		Object[] 	原始方法的参数
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代码执行前的操作.......");
                return method.invoke(userService, args);
            }
        });

        userServiceProxy.login("SennerMing", "123456");
        userServiceProxy.register(new User());
    }
}
```

分析：

```markdown
来看一下JDK中创建动态代理的核心代码
Proxy.newProxyInstance(classLoader,interfaces,invocationHandler);

回顾下当时代理的创建，是有三个要素的
  1. 原始对象
  2. 额外功能
  3. 代理对象和原始对象实现相同的接口

在分析额外功能的执行时机之前，我们先分析如何调用原始方法，那么原始方法的执行是怎么做的呢？
之前在MethodInterceptor中的invoke函数中我们是调用invocation.proceed()进行原始方法的调用

InvocationHandler的分析：
InvocationHandler是一个接口，实现方法：Object invoke(Object proxy,Method method,Object[] args){}
这个函数的作用就是用于书写我们的额外功能，运行的时机在原始方法执行之前、执行之后、执行前后及抛出异常

参数分析：	
Proxy 		先忽略掉，代表的是代理对象
Method 		额外功能所增加给的那个原始的方法
Object[] 	原始方法的参数

Object：原始方法的返回值，很像我们之前写的MethodInterceptor中的Object invoke(MethodInvocation invocation)，二者的返回值的意义是一样的

那么在InvocationHandler中的invoke函数中是怎么样进行原始方法的调用呢？

和之前的那个MethodBeforeAdvice中的before方法是非常相像的，我们只有Method和Args是没法进行原始方法的调用的
那么只能用Method的反射进行原始代码的调用：method.invoke(userService,args);
```

类加载器的作用

```markdown
ClassLoader
1.通过类加载器把对应的类的字节码文件加载到JVM中

2.通过类加载器创建类的Class对象，进而创建这个类的对象
	User的Class对象 --> new User() --> user对象

User.java  ---编译--->  User.class[字节码]  ---ClassLoader-->  JVM中

传统方式，我们如果想要创建一个User对象，那么首先要将字节码加载到JVM虚拟机中，怎么加载呢？就得用到ClassLoader，那么有了这个JVM中的User的字节码，就能创建User的对象了嘛？还是不行，还需要先创建User的Class对象，有了这个User的Class对象，才能通过这个Class对象创建user对象。
如何获得类的加载器：每一个类的.class文件，JVM会自动分配与之对应的ClassLoader

动态方式，JVM去加载动态代理类，生成我们需要的代理对象的字节码，通过字节码生成Class对象，才能创建代理类对象。
动态代理类是使用了动态字节码的技术，直接把字节码写到了JVM中了，但是动态生成的Class字节码没有指定的ClassLoader，无法完成Clas对象的创建。
那么在动态代理创建的过程中，需要ClassLoader创建代理类的Class对象，可是因为动态代理类没有对应的.class文件，JVM也就不会为其分配ClassLoader，但是又需要怎么办？那么就借用一个！
```



#### 5.2.2 CGlib的动态代理

CGlib创建动态代理类的原理：父子继承关系创建代理对象，原始类作为父类，代理类作为子类，这样既可以保证二者方法的一致性，同时在代理类中提供新的实现（额外功能+原始方法）

```markdown
一.先回一下JDK的动态代理
1.首先会有一个统一的接口UserService
2.接着会有个原始类要实现咱么的这个统一的接口UserserviceImpl implements UserService
3.然后咱们的这个要创建这个带有额外功能的代理类UserServiceProxy implements UserService，在目标方法的前、后。前后、异常执行附加功能

二.那么为什么这个原始类和代理类都要实现统一的接口呢？
1.保证代理类和原始类方法一致，用以迷惑调用者
2.实现了相同接口，代理类就可以提供新的实现，既加入了额外功能，也调用了原始方法的功能
Proxy.newProxyInstance();
这个JDK动态代理在编码上最大的特点就是目标类和代理类都要实现同一个接口

三.在现实生产中，我就有一个没有实现任何接口的原始类，我想给他做动态的代理，那怎么办呢？
CGlib牛就牛在可以解决这类问题，他会要求代理类继承原始类
class UserServiceProxy extends UserServiceImpl{
		login(){
			//额外功能
			super.login();
		}
		register(){}
}
```

CGlib的编码实现

```java
package aop.cglib;

import aop.User;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TestCglib {

    public static void main(String[] args) {
        //创建原始对象
        UserService userService = new UserService();
        //通过cglib方式创建动态代理
        /**
         * Proxy.newProxyInstance(classLoader,interfaces,invocationHandler)
         *
         * 1.那么cglib，也是使用动态字节码技术，所以也需要classLoader进行Class对象的创建
         * 2.是不需要父接口了，但是cglib不需要接口了，需要的是继承的父类
         * 3.也是我们要自己实现的额外功能
         *
         * Enhancer.setClassLoader()
         * Enhancer.setSuperClass()
         * Enhancer.setCallback() -----> MethodInvocationHandler
         *
         * 重要的就是Enhancer.create() ---> 创建代理
         */
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(TestCglib.class.getClassLoader());
        enhancer.setSuperclass(userService.getClass());

        MethodInterceptor interceptor = new MethodInterceptor() {
            /**
             * 等同于InvocationHandler 中的 invoke
             * @param o
             * @param method
             * @param objects
             * @param methodProxy
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("Cglib enhance .....");
                Object ret = method.invoke(userService, objects);
                return ret;
            }
        };

        enhancer.setCallback(interceptor);

        UserService userServiceProxy = (UserService) enhancer.create();
        userServiceProxy.login("SennerMing", "123456");
        userServiceProxy.register(new User());

    }
}
```

### 5.3总结JDK与CGlib

```markdown
JDK动态代理		Proxy.newProxyInstance()		通过接口创建代理的实现类
CGlib				 Enhancer										 通过集成父类创建的代理类	
```

**对BeanPostProcessor进行回顾**

```markdown
调用构造函数，属性注入之后   ------>  BeanPostProcessor.postProcessBeforeInitialization -----> InitializingBean  --------> initMethod="myInit" -----> BeanPostProcessor.postProcessAfterInitialization

那么结合着BeanPostProcessor还有JDK的动态代理知识，可以回答5.1的核心问题，我们怎么就通过原始类的id就能获得到他的代理类呢？

思路分析：
1.根据<bean id="userService" class="aop.UserServiceImpl"></bean>标签完成Bean对象的构造
2.构造完成后，会调用BeanPostProcessor.postProcessBeforeInitialization()，但是其中并未做任何处理
3.InitializingBean的AfterPropertiesSet()
4.接着到了init-method="myInit"
5.初始化完成后，会调用BeanPostProcessor.postProcessAfterInitialization(),那么我们就可以在这个方法中使用JDK动态代理userServiceProxy = Proxy.newProxyInstance(classLoader,interfaces,invocationHandler) return userServiceProxy;就将代理对象返回
6.那么用户通过代码 UserService userService = (UserService)ctx.getBean("userService")获得到动态代理对象
```

根据回顾的内容进行，使用BeanPostProcessor完成自定义的代理功能的实现

```java
package aop.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object proxyBean = Proxy.newProxyInstance(MyProxyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("........MyProxyBeanPostProcessor logging ......");
                return method.invoke(bean, args);
            }
        });
        return proxyBean;
    }
}
```

Spring的配置文件,aopfactory.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">


    <bean id="userService" class="aop.factory.UserServiceImpl"></bean>

    <!--
        1.实现BeanPostProcessor进行加工
        2.配置文件将BeanPostProcessor注册到Spring工厂
     -->
    <bean id="myProxyBeanPostProcessor" class="aop.factory.MyProxyBeanPostProcessor"></bean>

</beans>
```

进行测试

```java
@Test
public void testMyProxyBeanPostProcessor() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aopfactory.xml");
  aop.factory.UserService userService = (aop.factory.UserService) applicationContext.getBean("userService");
  userService.login("SennerMing", "123456");
  userService.register(new User());
}
```

## 6 基于注解的AOP编程

### 6.1 开发步骤详解

```markdown
基于注解的AOP编程的开发步骤
1.创建原始类
2.额外功能的实现
3.切入点
4.组装 - 切入点与额外功能的组装Pointcut+Advice

那么第一步创建原始类，和之前的AOP开发的方式没有什么不同
区别是在下面的2.3.4步，这些步骤都被揉进了@Aspect中了，@Aspect揉进了(Pointcut+Advice)
```

首先创建原始类，就使用之前常用的UserService

创建一个切面(Advice+Pointcut)

```java
package aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 加上这个@Aspect注解那么这个类就代表了一个切面(Aspect = Advice[额外功能] + Pointcut[额外功能要放在哪执行])
 *
 * 回顾我们在Spring框架中，之前是如何进行切面的实现的
 *      1.额外功能
 *      public class MyMethodInterceptor implements MethodInterceptor{
 *
 *          public Object invoke(MethodInvocation methodInvocation){
 *              //sout("原函数调用之前的额外功能....")
 *              Object ret = methodInvocation.proceed();
 *              //sout("原函数调用之后的额外功能....")
 *              return ret;
 *          }
 *      }
 *      2.切入点
 *      我们之前是在Spring中进行切入点的配置
 *      <aop:config>
 *          <aop:pointcut id="pc" expression="execution(* *(..))"></aop:pointcut>
 *      <aop:config/>
 *      其实里面最终要的就是这个切入点的表达式，就是需要额外功能要执行的位置，回到下面的around()方法
 *
 *      3.在Spring中进行配置
 */
@Aspect
public class MyAspect {

    /**
     * 这个方法名字随便起的
     *
     * 1. 需要加上@Around的这个注解，这个注解就相当于之前需要实现MethodInterceptor这个接口
     *
     * 2. Object 就相当于之前的invoke()函数的返回值，就是原始方法的返回值
     *
     * 3. 之前invoke的方法中又MethodInvocation这个对象，能对原方法进行调用，那么ProceedingJoinPoint就和其等同
     *
     * 那么至此这个额外功能就开发完了，跳回到上面的切入点
     *
     * 其实这个@Around()里面就可以在里面写切入点表达式了
     *
     * @return
     */
    @Around("execution(* login(..))")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        Object ret = null;
        try {
            System.out.println("----------- aspect logging --------------");
            ret = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }
}
```

进行Spring的配置，aspect.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!-- 原始对象 -->
    <bean id="userService" class="aspect.UserServiceImpl"></bean>

    <!-- 切面的创建，在这个切面中，既体现了额外功能，还体现了切入点 -->
    <bean id="myAspect" class="aspect.MyAspect"></bean>

    <!-- 告知Spring工厂基于注解进行AOP的编程 -->
    <aop:aspectj-autoproxy></aop:aspectj-autoproxy>

</beans>
```

进行测试

```java
@Test
public void testMyAnnotationAspect() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aspect.xml");
  aspect.UserService userService = (aspect.UserService) applicationContext.getBean("userService");
  userService.login("SennerMing", "123465");
  userService.register(new User());
}
```

**切入点使用带来的问题**

### 6.2 切入点复用

在切面类中定义一个函数，上面打上@Pointcut注解，通过这种方式定义切入点表达式，后续更加有利于切入点的复用

@Around切入点复用

```java
@Around("execution(* login(..))")
public Object around(ProceedingJoinPoint proceedingJoinPoint){
  Object ret = null;
  try {
    System.out.println("----------- aspect logging around--------------");
    ret = proceedingJoinPoint.proceed();
  } catch (Throwable throwable) {
    throwable.printStackTrace();
  }
  return ret;
}


@Around("execution(* login(..))")
public Object around1(ProceedingJoinPoint proceedingJoinPoint){
  Object ret = null;
  try {
    System.out.println("----------- aspect logging around 1--------------");
    ret = proceedingJoinPoint.proceed();
  } catch (Throwable throwable) {
    throwable.printStackTrace();
  }
  return ret;
}

//									运行结果							
		/**
     * ----------- aspect logging around--------------
     * ----------- aspect logging around 1--------------
     * UserServiceImpl.login 业务逻辑 + DAO
     * UserServiceImpl.register  业务逻辑 + DAO
     */
```

虽然上面的方式可以实现切入点的复用，但是，如果后面需要给register()方法要加上同样的额外功能，那么就要进行多处切入点的修改，而且，这样写会带来很多代码的冗余，大大增加了代码维护的难度。

Spring工厂考虑到了这个问题，于是他向我们提供了@Pointcut这个注解，里面可以填上我们需要的切入点表达式，那@Around中填什么呢？就填我们这个@Pointcut打在的那个方法，来看示例

```java
package aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 加上这个@Aspect注解那么这个类就代表了一个切面(Aspect = Advice[额外功能] + Pointcut[额外功能要放在哪执行])
 *
 * 回顾我们在Spring框架中，之前是如何进行切面的实现的
 *      1.额外功能
 *      public class MyMethodInterceptor implements MethodInterceptor{
 *
 *          public Object invoke(MethodInvocation methodInvocation){
 *              //sout("原函数调用之前的额外功能....")
 *              Object ret = methodInvocation.proceed();
 *              //sout("原函数调用之后的额外功能....")
 *              return ret;
 *          }
 *      }
 *      2.切入点
 *      我们之前是在Spring中进行切入点的配置
 *      <aop:config>
 *          <aop:pointcut id="pc" expression="execution(* *(..))"></aop:pointcut>
 *      <aop:config/>
 *      其实里面最终要的就是这个切入点的表达式，就是需要额外功能要执行的位置，回到下面的around()方法
 *
 *      3.在Spring中进行配置
 */
@Aspect
public class MyAspect {

    @Pointcut("execution(* login(..))")
    public void myPointcut(){}
    /**
     * 这个方法名字随便起的
     *
     * 1. 需要加上@Around的这个注解，这个注解就相当于之前需要实现MethodInterceptor这个接口
     *
     * 2. Object 就相当于之前的invoke()函数的返回值，就是原始方法的返回值
     *
     * 3. 之前invoke的方法中又MethodInvocation这个对象，能对原方法进行调用，那么ProceedingJoinPoint就和其等同
     *
     * 那么至此这个额外功能就开发完了，跳回到上面的切入点
     *
     * 其实这个@Around()里面就可以在里面写切入点表达式了
     *
     * @return
     */
    @Around(value="myPointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        Object ret = null;
        try {
            System.out.println("----------- aspect logging around--------------");
            ret = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }


    @Around(value="myPointcut()")
    public Object around1(ProceedingJoinPoint proceedingJoinPoint){
        Object ret = null;
        try {
            System.out.println("----------- aspect logging around 1--------------");
            ret = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

}
```

### 6.3 动态代理创建方式

```markdown
AOP底层实现，两种代理创建的方式
1.JDK			通过实现接口，动态做新的实现类		创建代理对象
2.CGlib		通过继承父类，动态做建新的子类		创建代理对象
```

```markdown
Spring中到底使用何种方式进行动态代理呢？
默认情况下，AOP编程底层应用的是JDK动态代理创建方式

如果切换CGlib该怎么做？
1.Spring 基于注解的 AOP的开发
<!-- 将Spring的AOP底层实现切换为CGlib动态代理技术 -->
<aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
2.传统的的AOP开发
<!-- 将传统的AOP开发代理的实现切换为CGlib -->
<aop:config proxy-target-class="true">

```

## 7 AOP开发中的特殊问题

### 7.1 什么情况下会出现这种问题

1.将MyAspect中的myPointcut()切入点表达式修改为类切入点表达式，下面这个两种方式都可以复现问题

```java
@Pointcut("execution(* login(..))")			//AOP的特殊问题
@Pointcut("execution(* *..UserServiceImpl.*(..))")			//AOP的特殊问题
public void myPointcut(){}
```

2.让UserServiceImpl中的register方法调用login方法

```java
package aspect;

import aop.User;
import aop.annotation.Log;

public class UserServiceImpl implements UserService {

    @Log
    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
//        throw new RuntimeException("存在异常！");
        this.login("MingSenner", "654321");		
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
```

测试代码：

```java
@Test
public void testMyAnnotationAspect() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aspect.xml");
  aspect.UserService userService = (aspect.UserService) applicationContext.getBean("userService");
  //        userService.login("SennerMing", "123465");
  userService.register(new User());
}
```

按道理来说，这个register方法中调用了login方法，无论上面的@Pointcut怎么写，这个login方法调用之前，应该是会执行我们写的额外功能，但是现实情况是，上面两种@Pointcut的写法，都没有！

```markdown
执行结果：
@Pointcut("execution(* login(..))")
UserServiceImpl.register  业务逻辑 + DAO
UserServiceImpl.login 业务逻辑 + DAO

@Pointcut("execution(* *..UserServiceImpl.*(..))")
----------- aspect logging around--------------
----------- aspect logging around 1--------------
UserServiceImpl.register  业务逻辑 + DAO
UserServiceImpl.login 业务逻辑 + DAO
```

分析分析

```markdown
UserService userService = (UserService)ctx.getBean("userService")，这个返回的代理对象！
当调用register()方法的时候，用的是这个代理对象，但是在register方法体里面调用login是原始对象！
```

### 7.2 解决方案

问题的总结：

在同一个业务类中，进行业务方法间的相互调用，只有最外层的方法，才是加入了额外功能（内部方法，通过原始对象自己调用，不会加入额外功能）。如果想要让内层的方法也调用代理对象的方法，就要实现ApplicationContextAware，使用工厂为我们生产的代理对象进行方法调用。

```java
package aspect;

import aop.User;
import aop.annotation.Log;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UserServiceImpl implements UserService , ApplicationContextAware {

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Log
    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
//        throw new RuntimeException("存在异常！");

        /**
         * UserService userService = (UserService)ctx.getBean("userService")，这个返回的代理对象！
         * 当调用register()方法的时候，用的是这个代理对象，但是在这个方法内部调用的login方法，是原始对象！
         * 原始对象调用，怎么可能还有额外功能呢？
         * 解决方案：
         *      1.从Spring工厂获得UserService代理，再调用login
         *      ApplicationContext ctx = new ClassPathXmlApplicationContext("aspect.xml");
         *      UserService userService = ctx.getBean("userService");//Proxy对象
         *      userService.login()
         *      当时这样，咱们说，ApplicationContext是一个重量级的对象，不要老是创建他，很占用内存的，而且
         *      这样的开发方式影响性能不说，还非常之不优雅
         *
         *      2.让这个原始类实现ApplicationContextAware接口，让Spring工厂将ApplicationContext传进来
         *
         */
        aop.factory.UserService userService = (aop.factory.UserService) ctx.getBean("userService");
        userService.login("MingSenner", "654321");       //AOP的特殊问题
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
```

## 8 AOP阶段知识的总结

```markdown
AOP编程概念（Spring的动态代理开发）
概念：通过代理类为原始类增加额外功能
好处：利于原始类及额外功能的维护

开发步骤 1.原始对象 2.额外功能 3.切入点 4.切面组装
核心：主要是后面的3步
```

```markdown
一、传统方式： 
1.额外功能
 MethodInterceptor的接口实现 
 Object invoke(MethodInovcation methodInvocation){
 	 Object ret = methodInvocation.proceed();
 	 return ret;
 }
 
2.切入点还有切面组装
<bean id="myMethodInterceptor" class="xxx.xx.MyMethodInterceptor"></bean>
<aop:config>
   <aop:pointcut id="pc" expression="execution(* *(..))"></aop:pointcut>
   <aop:advisor pointcut-ref="id" advice-ref="myMethodInterceptor"/>
</aop:config>
      		
切入点表达式：方法、包、类
切入点函数：execution、args、within @annotation
```

```markdown
二、注解方式
1.额外功能+Pointcut都在一个@Aspect中进行实现
@Aspect
public class MyAspect{

	@Pointcut("execution()")
	public void myPointcut(){}

	@Around("execution()")
	public Object around(ProceedingJointPoint jointPoint){
		Object ret = jointPoint.proceed()
		return ret;
	}
}

2.进行Aspect在Spring中的注册，以及开启Spring的Aspect注解的功能
<bean id="myAspect" class="xxx.xx.MyAspect"></bean>
<aop:aspectj-autoproxy/>
```

```mark
底层实现
1.	JDK			Proxy.newProxyInstance()  ---> 	原始对象的接口，创建代理对象
2.	CGlib 	Enhancer.create()					--->	爸原始类作为代理的父类，通过继承的方式创建代理对象

指定Spring底层动态代理的技术
1.传统方式		<aop:config proxy-target-class="true">
2.注解方式		<aop:aspectj-autoproxy proxy-target-class="true">
```