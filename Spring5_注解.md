# Spring 5 Annotation 学习

## 1 注解基础概念

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
那么Provider写好了这个方法后，Consumer在invoke中进行了方法的调用，这个流程看上去一点问题也没有，这两个东西是同一个人写的还好，那这两个人互相都不认识，功能提供者写好的方法，调用者怎么去调用啊？他都不知道有这个方法，那要解决这样的问题，我们就需要做出一个约定，Spring在其中就扮演着这个契约的制定者。
```

