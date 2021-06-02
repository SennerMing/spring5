# Spring 持久层 学习

## 1 持久层相关介绍

```markdown
一、Spring框架为什么要与持久层技术进行整合？
1.JAVA EE开发需要持久层进行数据库的访问操作。
2.JDBC Hibernate MyBatis进行持久开发过程存在大量的代码冗余
3.Spring基于模板设计模式对于上述的持久层技术进行了封装
```



```markdown
二、Spring框架能与那些持久层框架进行整合呢？
1.	JDBC								JDBCTemplate
2.	Hibernate(JPA)			HibernateTemplate
3.	Mybatis							SqlSessionFactoryBean MapperScannerConfigure
```



## 2 Spring与MyBatis整合

```markdown
MyBatis开发步骤的回顾
1.实体类的创建、2.实体别名、3.表、4.创建DAO接口、5.实现Mapper文件、6.注册Mapper文件、7.Mybatis API调用
```

### 2.1 Mybatis开发步骤回顾

依赖的导入

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.23</version>
</dependency>

<!-- Mybatis -->
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>3.5.6</version>
</dependency>
```

实体类创建

```java
package mybatis;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id;
    private String name;
    private String password;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

DAO接口创建

```java
package mybatis.dao;

import mybatis.entity.User;

public interface UserDAO {
    int save(User user);
}
```

Mapper创建

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="mybatis.dao.UserDAO">

    <insert id="save" parameterType="mybatis.entity.User">
        insert into tab_user_info(name,password) values(#{name},#{password})
    </insert>

</mapper>
```

Mybatis-config.xml配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    <properties resource="jdbc.properties"></properties>
    <settings>
        <!-- 打印查询语句 -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
        <!-- 控制全局缓存（二级缓存）-->
        <setting name="cacheEnabled" value="true"/>
        <!-- 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。默认 false  -->
        <setting name="lazyLoadingEnabled" value="true"/>
        <!-- 当开启时，任何方法的调用都会加载该对象的所有属性。默认 false，可通过select标签的 fetchType来覆盖-->
        <setting name="aggressiveLazyLoading" value="false"/>
        <!--  Mybatis 创建具有延迟加载能力的对象所用到的代理工具，默认JAVASSIST -->
        <!--<setting name="proxyFactory" value="CGLIB" />-->
        <!-- STATEMENT级别的缓存，使一级缓存，只针对当前执行的这一statement有效 -->
        <!--
                <setting name="localCacheScope" value="STATEMENT"/>
        -->
        <setting name="localCacheScope" value="SESSION"/>
    </settings>

    <typeAliases>
        <typeAlias alias="user" type="mybatis.entity.User"/>
    </typeAliases>

    <!--    <typeHandlers>
            <typeHandler handler="com.wuzz.type.MyTypeHandler"></typeHandler>
        </typeHandlers>-->

    <!-- 对象工厂 -->
    <!--    <objectFactory type="com.wuzz.objectfactory.GPObjectFactory">
            <property name="wuzz" value="666"/>
        </objectFactory>-->

    <!--    <plugins>
            <plugin interceptor="com.wuzz.interceptor.SQLInterceptor">
                <property name="wuzz" value="betterme" />
            </plugin>
            <plugin interceptor="com.wuzz.interceptor.MyPageInterceptor">
            </plugin>
        </plugins>-->

    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/><!-- 单独使用时配置成MANAGED没有事务 -->
            <dataSource type="POOLED">
                <property name="driver" value="${prop.driverClass}"/>
                <property name="url" value="${prop.url}"/>
                <property name="username" value="${prop.userName}"/>
                <property name="password" value="${prop.password}"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="mapper/UserDAOMapper.xml"/>
    </mappers>

</configuration>
```

进行开发调用

```java
package test;

import mybatis.entity.User;
import mybatis.dao.UserDAO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestMybatis {

    @Test
    public void testEnvironment() {
        try {
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

            SqlSession sqlSession = sqlSessionFactory.openSession();

            UserDAO userDAO = sqlSession.getMapper(UserDAO.class);

            User user = new User("SennerMing", "123456");
            userDAO.save(user);

            sqlSession.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

虽然上面的开发并不难，但是很烦

### 2.2 Mybatis结合Spring思路分析

```markdown
配置繁琐 代码冗余
```

```markdown
配置繁琐
1.实体别名的配置
<typeAliases>
	<typeAlias alias="user" type="mybatis.entity.User" />
</typeAliases>
这个后面会产生比较多的问题，会出现大量的实体类，都需要这样配置的话，工作量比较大，而且维护起来比较困难

2.同样的mapper注册也是比较繁琐的，和上面的1相同
<mappers>
	<mapper resource="mapper/UserDAOMapper.xml"/>
</mappers>
```

```markdown
代码冗余

InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
SqlSession sqlSession = sqlSessionFactory.openSession();

//主要的逻辑
UserDAO userDAO = sqlSession.getMapper(UserDAO.class);
User user = new User("SennerMing", "123456");
userDAO.save(user);

sqlSession.commit();

可以看到除了主要的逻辑，其他还有大量的重复代码
```

那么作为Spring是怎样简化这些配置，解决代码冗余的问题的

#### 2.2.1 针对SqlSessionFactoryBean

```markdown
1.SqlSessionFactoryBean
作用：用于封装SqlSessionFactory创建的代码
对应的在Spring中的配置
<bean id="ssfb" class="SqlSessionFactoryBean全限定名"></bean>

2.mybatis-config.xml的配置文件
主要作用：
1.Datasource:连接池的配置
2.typeAliases:类别名的配置(设置后我们可以直接在Mapper中使用别名了)
3.mappers:mapper的配置
有了mybatis-config就能进行SqlSessionFactoryBean的创建了，那么SqlSessionFactoryBean对这些配置产生了依赖
那么就可以在Spring中进行配置了
<bean id="ssfb" class="SqlSessionFactoryBean全限定名">
	<property name="dataSource"></property>
	<property name="typeAliasesPackage"></property> <!-- 对应的实体包 -->
	<property name="mappersLocation"></property>		<!-- 对应的Mappers的位置 -->
</bean>
那么这样的话，mybatis-config里面的内容都转移到了Spring的配置中了，那么mybatis-config就可以不要了
```

mybatis-config不要了，内容转移到了Spring框架里了，那么我们来思考下应用中需要注意的细节，以及这样做给我们带来了哪些好处呢？

```markdown
1.DataSource
之前是写死在里面了，现在用Spring，我们在连接池注入的时候，只要引用我们自己定义的连接池对象就行了，这就解了耦合了，提高了连接池的复用性

2.TypeAliases
之前我们是一个一个的往里面添加，现在用了Spring，我们只需要指定我们所有实体类存放的包就行了，Spring会自动进行实体类的别名的创建，自动化了，大大减少了我们开发的工作量，提高了代码的维护性

3.Mappers
之前我们也是一个一个的往里面添加，现在用了Spring，我们只需要指定mapper存放的路径，还有我们可以进行通配的设置，比如：*Mapper.xml,那么它就可以匹配 UserDAOMapper.xml、ProductDAOMapper.xml这些Mapper文件了，是不是很开心？用起来特别的方便！
```

#### 2.2.2 针对SqlSession

```markdown
1.SqlSession
对于这个SqlSession的获取，Spring也为我们做了封装，MapperScannerConfigure
<bean id="scanner" class="MapperScannerConfigure全限定名">
		<!--指定SqlSessionFactoryBean-->
		<property name="sqlSessionFactoryBeanName" value="ssfb"></property> 
		<!--指定我们DAO所在的包，Spring回去自动去找，并帮我们实现这些DAO的持久化类供我们使用-->
		<property name="basePackage"></property> 
</bean>

注意：MapperScannerConfigure所创建的DAO对象，他的id值是接口收个单词字母的小写
		UserDao 		---> 	userDAO
		ProductDAO	--->	productDAO
		ctx.getBean("userDAO"|"productDAO");
```

### 2.3 Spring与Mybatis整合的开发步骤

#### 2.3.1 配置思路

```markdown
配置文件(ApplicationContext)进行相关的配置
<!-- 创建SqlSessionFactory -->
<bean id="ssfb" class="SqlSessionFactoryBeanFactory全限定名">
	<property name="dataSource" ref="我们的连接池"></property>
	<property name="typeAliasesPackage">
			<!-- 注意，这里面要指定我们实体类所在的包
				musician.club.entity，方便在mapper中的使用
      -->
	</property>
	<property name="mapperLocations">
			<!-- 注意，这里指定配置文件(映射文件)的路径 还有通用配置
      	musician.club.mapper/*.Mapper.xml
      -->
	</property>
</bean>

<!-- DAO接口的实现类对象的创建
		sqlSession  ----  session.getMapper(XXXDAO.class) ---- xxxDAO实体类对象
-->
<bean id="scanner" class="MapperScannerConfigure">
			<!-- 想要实现DAO接口实体类对象的创建
						1.那么必须得先有SqlSessionFactory用以获得SqlSession
						2.还要指定我们DAO接口所在的路径
			-->
			<property name="sqlSessionFactoryName" value="ssfb"></property>
			<property name="basePackage">指定那个DAO接口的包<property>
</bean>

未来这些个东西，与传统方式的Mybatis配置的对比，看起来也没什么简化的啊？但是这些只需要配置一次就好。
```



```markdown
编码
1.实体
2.实体别名									配置繁琐  -- 现在不用了，会去扫描typeAliasesPackage指定的包
3.表
4.创建DAO接口
5.实现Mapper文件
6.注册Mapper文件						配置繁琐	--	现在不用了，Spring会去扫描指定的basePackage
7.MybatisAPI调用						代码冗余	-- 没有代码冗余了，已经在Spring中配置了SqlSessionFactoryBean
```

#### 2.3.2 整合开始

导入依赖

```xml
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <version>8.0.23</version>
</dependency>

<!-- Mybatis -->
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>3.5.6</version>
</dependency>

<!-- Spring整合Mybatis -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-jdbc</artifactId>
  <version>5.3.5</version>
</dependency>

<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis-spring</artifactId>
  <version>2.0.6</version>
</dependency>

<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>druid</artifactId>
  <version>1.2.5</version>
</dependency>
```

Spring配置，spring-mybatis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"></context:property-placeholder>

    <!-- 连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${prop.driverClass}"></property>
        <property name="url" value="${prop.url}"/>
        <property name="username" value="${prop.userName}"/>
        <property name="password" value="${prop.password}"/>
    </bean>

    <!-- 配置SqlSessionFactoryBean -->
    <bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="typeAliasesPackage" value="mybatis.entity"/>
        <property name="mapperLocations">
            <list>
                <value>classpath:mapper/*Mapper.xml</value>
            </list>
        </property>
    </bean>

    <!-- DAO实现类对象的创建 MapperScannerConfigure-->
    <bean id="mapperScannerConfigure" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactoryBean"/>
        <property name="basePackage" value="mybatis.dao"/>
    </bean>
</beans>
```

entity还有dao都是之前的

测试方法

```java
@Test
public void TestSpringMybatis() {
  ApplicationContext context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
  UserDAO userDAO = (UserDAO) context.getBean("userDAO");

  User user = new User("MingSenner", "654321");
  userDAO.save(user);
}
```

#### 2.3.3 整合细节

```markdown
1.在Mybatis与Spring整合之后，我们通过Spring拿到DAO对象，为什么我们没有进行事务的提交，但是数据却插入了数据库中呢？
可以说谁控制了Connection对象，谁就控制了事务。

分析谁管理着连接Connection？
上面测试代码运行，日志中会出现以下信息：
SpringManagedTransaction:49 - JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@60129b9a] will not be managed by Spring
那不是Spring进行控制，难道是Mybatis？之前我们在配置mybatis-config的时候，是给了他的数据库的连接信息，现在我们没有配置，而是在SqlSessionFactoryBean中配置了dataSource为我们的Druid连接池，哦原来，控制Connection对象的是我们的连接池！

Mybatis内置的连接池对象 ---> 创建Connection
	Connection.setAutoCommit(false);操作完成，需要我们自己手动提交

Druid(C3P0,DBCP)作为连接池
	Connection.setAutoCommit(true);操作完成，不需要我们进行手动提交，自动控制事务，一条sql，自动提交

答问题1：因为Spring与Mybatis整合时，我们引入了外部的连接池对象，保持自动的事务提交这个机制（Connection.setAutoCommit(true)）,不需要手工进行事务的操作，也能进行事务的提交

注意：未来在实战的过程中，我们还需要进行事务的手动控制，需要将多条sql组合成一个事务，后续呢，会通过Spring通过事务控制来解决这个问题！
```

## 3 Spring事务

```markdown
1.什么是事务？
答：保证业务操作完整性的一种数据库机制 - 在一个业务中涉及的多个操作，要么一起成功，要么一起失败，而且不能产生相互的影响，是由数据库完成的，我们的Java代码仅仅是完成了对其的调用

2.事务的4个特点
A：原子性、C：一致性、I：隔离性、D：持久性
```

事务的控制

```markdown
采用不同的持久化技术，采用的控制事务的方式是不一样的
1.JDBC：
	Connection.setAutoCommit(false);
	Connection.commit();
	Connection.rollback();
	全都依附于Connection连接对象

2.Mybatis
	Mybatis自动开启事务
	sqlSession.commit();
	sqlSession.rollback()
	
他们都是相同的套路，1.开启事务、2.提交、3.回滚

这个sqlSession的底层也封装了连接对象，其实它里面也是用的JDBC的方式进行事务的控制

结论：
无论你是JDBC、Mybatis还是JPA，底层还是使用Connection对象来进行事物的控制！
```

### 3.1 Spring事务控制

之前我们总结过，事务一般都作为额外功能，我们通过”额外功能“这四个字，就能联想到咱们的AOP开发，其实Spring也确实是通过AOP进行事务的控制的。

```mark
AOP的回顾
1.原始对象、2.额外功能、3.切入点、4.组装切面
```

1. 原始对象

```java
//那么原始对象其实指的就是咱们的这些个service层中的业务对象，只有Service，我们才进行事务的控制
public class XxxUserServiceImpl{
  //该Service中的方法只进行核心功能的开发
  //1.原始对象 ----> 原始方法 ----> 核心功能（业务处理+DAO调用）
  //2.我们要将DAO作为Service的成员变量，通过依赖注入的方式进行赋值
  private XxxDAO xxxDAO;set;get;
}
```

2. 额外功能

```markdown
之前我们的AOP编程
1.MethodInterceptor
public class MyMethoInterceptor implements MethodInterceptor{
		Object invoke(MethodInovation methodInvocation){
				Object ret = null;
				//事务的开启
				try{
					Connection.setAutoCommit(false);
					ret = methodInvocation.proceed();
					//事务的提交或回滚
					Connection.commit()
				} catch(Exception e){
				  Connection.rollback()
				}
				return ret;
		}
}

2.@Aspect @Around(事务的控制写到这个地方)
```

这些个代码，你会写，Spring作为那么火的框架，他能不会写？人家都帮你整好了！

噔噔噔，**org.springframework.jdbc.datasource.DataSourceTransactionManager**

问题来了，甭管你什么Manager他都需要Connection来进行事务的控制，所以咱们的这个Spring框架中的DataSourceTransactionManager依赖DataSource，依赖那我们就进行诸如，我们的DataSource已经交给了Druid进行管理了，Spring也想到了这点，那么我们就只需要将连接池注入到这个Manager中就行了！

3. 切入点

```markdown
@Transactional
通过这个注解我们就可以指定额外功能-事务，加载哪些地方了

1. 类上：类中的所有方法都会加入事务
2. 方法上：这个方法会加入事务		
```

4. 组装切面

```markdown
1.切入点
2.额外功能

这个组装切面的功能是通过一个标签来完成的，这个就是获取我们第二步中额外功能需要使用到的TransactionManager
<tx:annatation-driven transaction-manager="">
```

### 3.2 Spring控制事务编码

搭建开发环境，引入依赖

```xml
<!-- Spring事务管理 -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-tx</artifactId>
  <version>5.3.5</version>
</dependency>
```

原始对象

```java
package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
@Transactional
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void register(User user) {
        userDAO.save(user);
    }
}
```

Spring的配置，spring-mybatis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <context:property-placeholder location="classpath:jdbc.properties"></context:property-placeholder>

    <!-- 连接池 -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
        <property name="driverClassName" value="${prop.driverClass}"></property>
        <property name="url" value="${prop.url}"/>
        <property name="username" value="${prop.userName}"/>
        <property name="password" value="${prop.password}"/>
    </bean>

    <!-- 配置SqlSessionFactoryBean -->
    <bean id="sqlSessionFactoryBean" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="typeAliasesPackage" value="mybatis.entity"/>
        <property name="mapperLocations">
            <list>
                <value>classpath:mapper/*Mapper.xml</value>
            </list>
        </property>
    </bean>

    <!-- DAO实现类对象 的创建 MapperScannerConfigure-->
    <bean id="mapperScannerConfigure" class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactoryBean"/>
        <property name="basePackage" value="mybatis.dao"/>
    </bean>

    <!--
        我们之前讲过，Spring整合了Mybatis
        这个MapperScannerConfigure会帮我们创建好这个DAO接口的实现对象，供我们使用
        那么它创建出来的这个实现类对象的名字就是我们那个DAO的首字母的小写
    -->
    <bean id="userService" class="mybatis.service.impl.UserServiceImpl">
        <property name="userDAO" ref="userDAO"></property>
    </bean>

    <!--
        配置我们的这个DataSourceTransactionManager，
        这个东西会帮我们实现事务的管理，我们只需要将DataSource交给他就行了
     -->
    <bean id="dataSourceTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!--
        在mybatis.service.impl.UserServiceImpl上加入了@Transactionnal注解之后，还需开启Spring的事务功能
        Spring就会自动扫描@Transactional注解了
     -->
    <tx:annotation-driven transaction-manager="dataSourceTransactionManager"/>

</beans>
```

这就开发完成了，下面进行测试

```java
@Test
public void TestSpringMybatisTransaction() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-mybatis.xml");
  UserService userService = (UserService) applicationContext.getBean("userService");
  userService.register(new User("小王","xiaowangba"));
}
```

开发细节

```markdown
1.数据确实是入库了，但是你怎么证明这个额外功能-事务，添加成功了呢？
验证：在UserServiceImpl.register()中抛出一个异常，如果回滚，那么证明是有了事务，对吧？
@Override
public void register(User user) {
   userDAO.save(user);
   throw new RuntimeException("异常了兄弟！");
}
其实通过日志的打印，能看到JDBC Transaction的相关信息

2.看看配置文件<tx:annotation-driven>
这个标签其实还有一个属性proxy-target-class，看到这个属性，我们就立马联想到，Spring AOP中动态代理的设置，这个是进行动态代理底层的切换，默认是false那就是JDK的，true就是CGlib的
```

## 4 Spring的事务属性

```markdown
1. 什么是事务属性？
属性：描述物体特征的一系列的值
如果对人进行描述：性别、身高、体重....

前面我们已经为Spring添加上了事务
事务属性：描述事物特征的一系列的值
但是这个事务有哪些属性呢？
1. 隔离属性
2. 传播属性
3. 只读属性
4. 超时属性
5. 异常属性 
```

```markdown
2.如何添加事务属性
@Transactional()，比如添加隔离属性，那么就可以写@Transactional(isolation=)，如果还想为其增加传播属性呢？那么就可以写成@Transactional(isolation=,propagation,...)
```

### 4.1 事务属性的详解

#### 4.1.1 并发产生的问题

```mark
概念：他描述了事务解决并发问题的特征
一. 什么是并发
答: 过个事务（用户）在同一时间，访问操作了相同的数据
		同一时间： 0.000几秒 很微小 但是计算机也是可以区分出来，其前后差别

二. 并发会产生哪些问题
答：会产生三类问题
1. 脏读
2. 不可重复读
3. 幻影读

三.针对这些并发产生的问题，应该如何解决
答：通过隔离属性进行解决的，我们在隔离属性中设置不同的值，解决并发过程中出现的问题。
```

#### 4.1.2 脏读

脏读的描述：

```mark
本事务中读取了另一个事务中没有提交的数据。会在本事务中产生了数据不一致的问题！
```

小故事：

假如今天SennerMing发工资，工商银行通过短信通知到了SennerMing，而此时SennerMing的老婆也通过工商银行APP查看到了SennerMing的银行账户多出来的工资。

一块取钱：

| 时间 | 夫人                                | 银行账户         | SennerMing                   |
| ---- | ----------------------------------- | ---------------- | ---------------------------- |
| T1   | 操作1:查看账户(余额:1000)           |                  |                              |
| T2   | 操作2:取300块钱                     |                  |                              |
| T3   | 操作3:更新银行账户(余额:700)        |                  |                              |
| T4   |                                     |                  | 操作1:查看账户(余额:700)     |
| T5   |                                     |                  | 操作2:取200块钱              |
| T6   | 操作4:rollback,良心发现不想买口红了 |                  |                              |
| T7   |                                     |                  | 操作3:更新银行账户(余额:500) |
| T8   |                                     |                  | 操作4:提交                   |
| T9   |                                     | 最终账户余额:500 |                              |

银行账户

| ID   | NAME       | PASSWORD | BALANCE |
| ---- | ---------- | -------- | ------- |
| 1    | SennerMing | 123456   | 1000    |
| 2    | XiaoWang   | 654321   | 2000    |
| 3    | XiaoLi     | 789456   | 1000    |
| 4    | XiaoZhang  | 987456   | 1000    |

问题来了，这个SennerMing可以读到夫人还未提交的数据，这个就麻烦啦，数据不一致了喔，SennerMing凭空消失了300块钱，o(╥﹏╥)o



```
解决方案
@Transactional(isolation=Isolation.READ_COMMITED)
只能读取到，已经提交的数据，解决脏读问题
```

```java
//只能读取到，已经提交的数据，解决脏读问题
@Transactional(isolation = Isolation.READ_COMMITTED)
public class UserServiceImpl implements UserService {}
```



#### 4.1.3 不可重复读

不可重复读的描述

```markdown
在同一个事物中，多次读取相同的数据，但是读取的结果不一样。会在本事务中产生数据不一致问题
```

小故事：

还是SennerMing和他的夫人一块取钱的那些事。

一块取钱：

| 时间 | 夫人                        | 银行账户     | SennerMing                  |
| ---- | --------------------------- | ------------ | --------------------------- |
| T1   | 开启了事务                  |              |                             |
| T2   | TX操作1:查询账户(余额:1000) |              |                             |
| T3   | 其他操作(事务未结束)        |              | 开启事务                    |
| T4   | 其他操作(事务未结束)        |              | TX操作1:查询账户(余额:1000) |
| T5   | 其他操作(事务未结束)        |              | TX操作2:取钱(200)           |
| T6   | 其他操作(事务未结束)        |              | TX操作3:更新账户(余额:800)  |
| T7   | 其他操作(事务未结束)        | 账户余额:800 | 提交TX                      |
| T8   | TX操作2:查询账户(余额:800)  |              |                             |
| T9   | ...                         |              |                             |

同一个事务之内，查询到的数据不一致，第二次查询出来的800块钱，这个800块钱他不是脏数据，不正常的地方是在一次事务中，对同一个数据进行了多次查询，数据竟然不一致了，那我到底以1000块钱进行后续的处理，还是以800块钱进行后续的处理

```markdown
注意：1.余额800这个数据它不是脏读 2.在同一个事务中
```

银行账户

| ID   | NAME       | PASSWORD | BALANCE |
| ---- | ---------- | -------- | ------- |
| 1    | SennerMing | 123456   | 1000    |
| 2    | XiaoWang   | 654321   | 2000    |
| 3    | XiaoLi     | 789456   | 1000    |
| 4    | XiaoZhang  | 987456   | 1000    |



````mark
解决方案
@Transactional(isolation=Isolation.REPETABLE_READ)
不可重复读，其实数据库的底层是一把行锁，对SennerMing夫人来讲的话，如果她的事务中的事务属性为不可重读，那SennerMing再进行这行数据的访问，他只能去等；那夫人在这个事务当中，只要她不进行修改，那她读到的数据永远是一样的，等夫人将事务进行了提交，锁才得到了释放，SennerMing才能访问到这一行数据！
````



#### 4.1.4 幻影读

幻影读的描述

```markdown
在同一个事务中，多次对整表进行查询统计，但是结果不一致，会在本市无中缠上数据不一致的问题
```

小故事：

有一天，银行行长想对自己银行的存款进行统计操作，但是同时，银行的柜员SennerMing正在为新客户他的老婆开了个账户，并且存了300块钱。

一块取钱：

| 时间 | 行长                        | 银行账户  | SennerMing(柜员)              |
| ---- | --------------------------- | --------- | ----------------------------- |
| T1   | 开启了事务                  | 总额:5000 |                               |
| T2   | TX操作1:统计余额(余额:5000) | 总额:5000 |                               |
| T3   | 其他操作(事务未结束)        | 总额:5000 | 开启了事务                    |
| T4   | 其他操作(事务未结束)        | 总额:5000 | TX操作1:为新用户存钱(余额300) |
| T5   | 其他操作(事务未结束)        | 总额:5000 | TX操作2:其他骚操作            |
| T6   | 其他操作(事务未结束)        | 总额:5000 | 提交TX                        |
| T7   | TX操作2:查询账户(余额:5300) | 总额:5300 |                               |
| T8   | ...                         |           |                               |

同一个事务之内，进行银行的所有存款的统计，两次统计的存款总额不一致，你说这不就要命了吗？

```markdown
注意：与不可重复读不一样，不可重复读的是行锁，幻影读是表锁
```

银行账户

| ID   | NAME           | PASSWORD | BALANCE |
| ---- | -------------- | -------- | ------- |
| 1    | SennerMing     | 123456   | 1000    |
| 2    | XiaoWang       | 654321   | 2000    |
| 3    | XiaoLi         | 789456   | 1000    |
| 4    | XiaoZhang      | 987456   | 1000    |
| 5    | 新增用户"老婆" | 963852   | 300     |

```markdown
解决方案
@Transactional(isolation=Isolation.SERIALIZABLE)，本质是数据库底层的表锁，在一个事务中，如果属性设置为幻影读级别，那么这个事务就会将整张表锁住，等到其事务进行了提交，表锁才得以释放，其他的事务才能进行表的访问及操作
```

#### 4.1.5 总结

```markdown
并发安全	:		SERIALIZABLE  >  REPETABLE_READ   >  READ_COMMITED
运行效率	:		SERIALIZABLE  <  REPETTABLE_READ  <  READ_COMMITED
```

### 4.2 数据库对于隔离属性的支持

| 隔离属性的值             | MySQL | Oracle |
| ------------------------ | ----- | ------ |
| ISOLATION_READ_COMMITED  | ✅     | ✅      |
| ISOLATION_REPETABLE_READ | ✅     | ❎      |
| ISOLATION_SERIALIZABLE   | ✅     | ✅      |

```markdown
Oracle不支持REPETABLE_READ值，如何解决不可重复读
采用的是多版本比对的方式，解决了不可重复读的问题
```

默认隔离属性

```markdown
1.在使用Spring进行事务管理的时候，我们未指定任何隔离属性的时候，默认的隔离级别是什么呢？
我们将UserServiceImpl中的@Transactional指定的属性清空，再执行下测试代码，可以看到事务的相关日志信息：
[mybatis.service.impl.UserServiceImpl.register]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT

2.可以看到是ISOLATION_DEFAULT，那这个ISOLATION_DEFAULT是个什么意思呢？
意思是：会调用不同数据库所设置的默认隔离属性
MySQL:		ISOLATION_DEFAULT ---> REPETABLE_READ
Oracle:		ISOLATION_DEFAULT ---> READ_COMMITED

3.进行验证，数据库的默认隔离属性
MySQL: 
select @@transaction_isolation;
+-------------------------+
| @@transaction_isolation |
+-------------------------+
| REPEATABLE-READ         |
+-------------------------+
1 row in set (0.00 sec)

Oracle:
SELECT s.sid, s.serial#,
CASE BITAND(t.flag, POWER(2, 28))
WHEN 0 THEN 'READ COMMITTED'
ELSE 'SERIALIZABLE'
END AS isolation_level
FROM v$transaction t 
JOIN v$session s ON t.addr = s.taddr AND s.sid = sys_context('USERENV', 'SID');
       SID    SERIAL# ISOLATION_LEVE
---------- ---------- --------------
       149         61 READ COMMITTED
```

### 4.3 隔离属性在开发中的建议

```markdown
推荐使用Spring默认的ISOLATION_DEFAULT
1. MySql repetable_read
2. Oracle read_commited

问题： 你说你MySQL用那个repetable_read，级别适中，兼顾性能和数据一致性，但是Oracle用read_commited也太低了吧？
答：实战中，并发访问的情况，本来就很低，有海量的用户，才可能产生这样的问题；如果真的遇到了这样的问题，我们优先推荐的，也并不是靠隔离属性来解决，我们大概率会通过乐观锁来处理，Hibernate(JPA) Version来进行处理，很方便；那我们的Mybatis就得通过拦截器自定义开发，这算是Mybatis比较高阶的技能
```

### 4.4 传播属性

```markdown
概念：描述了事务解决嵌套问题的特征

1.什么是事务的嵌套？
答：一个大的事务中，包含了若干个小事务
TX A Begin
	|- TX B begin end
	|- TX C begin end
TX A Finish

2.那么那些情况（场景）下会发生事物的嵌套呢？
答：Service之间的相互调用，是可能出现事务的嵌套的，传统的方式是，Service调用DAO的，这个可以满足用户绝大多数情况下的需求，随着业务的不断扩大，是有可能出现Service调用Service的情况的。

3.那怎么Service调用Service就会产生事务的嵌套呢？
AServiceImpl{
	MethodA(){
		TX A begin;
		//调用BService中的方法，这就发生了事务的嵌套了
		BServiceImpl.MethodB();
		TX A finish;
	}
}

BServiceImpl{
 		MethodB(){
 				TX B begin;
 				TX B finish;
 		}
}

4.那么一旦发生了事物的嵌套会发生什么问题呢？
答：假设事务关系如下
TX A begin;
	|- TX B begin finish;
	|- TX C begin finish;
TX A finish;
你想想，如果C事务出现了问题，将会怎么样，按道理讲C事务是属于A事务的，那么C事物发生了问题，肯定也对事务A进行了影响吧，C事务的失败也导致了A的失败，那事务B呢？兄弟，事务B怎么办呢？他也没错，他都已经提交过了，你说怎么办？他怎么回滚呢？

问题总结归纳：
大事务中融入了很多小的事务，他们彼此影响，最终就会导致外部的大事务，丧失了事务的原子性
```

#### 4.4.1 传播属性的值及其用法

无论我们以后给这个传播属性设置什么样的值，其中心的思想就是，会保证在同一时间，只会有一个事务的存在。

| 传播属性的值  | 外部不存在事务 | 外部存在事务               | 用法                                                  | 备注                               |
| ------------- | -------------- | -------------------------- | ----------------------------------------------------- | ---------------------------------- |
| REQUIRED      | 开启新的事务   | 融合到外部事务中           | @Transactional(propagation=Propagation.REQUIRED)      | 一般会应用到增删改方法中           |
| SUPPORTS      | 不开启新的事务 | 融合到外部事务中           | @Transactional(propagation=Propagation.SUPPORTS)      | 主要应用在查询方法中               |
| REQUIRES_NEW  | 开启新的事务   | 挂起外部事物，创建新的事务 | @Transactional(propagation=Propagation.REQUIRES_NEW)  | 日志持久化                         |
| NOT_SUPPORTED | 不开启新的事务 | 挂起外部事物               | @Transactional(propagation=Propagation.NOT_SUPPORTED) | 就不支持事务，让外部事务暂停       |
| NEVER         | 不开启新的事务 | 抛出异常                   | @Transactional(propagation=Propagation.NEVER)         | 就不支持事务，外部有事务我还抛异常 |
| MANDATORY     | 抛出异常       | 融合到外部事物中           | @Transactional(propagation.MANDATORY)                 | 就需要事务，还融到外部事务中       |

#### 4.4.2 结合Service进行理解

假设有三个Service

```java
public class AService{
  @Transactional
  public void aMethod(){
    TX A begin;
    TX A end;
  }
}

public class BService{
  @Transactional
  public void bMethod(){
    TX B begin;
    TX B end;
  }
}

public class CService{
  @Transactional
  public void cMethod(){
    TX C begin;
    TX C end;
  }
}
```

##### 4.4.2.1 REQUIRED

1.外部不存在事务	开启新的事务

2.外不存在事务		融合到外部事物中

```java
@Transactional
public void aMethod(){
  TX A begin;
 	@Transactional
  bMethod(){
    TX B begin;
    TX B end;
  }
  @Transactional
  cMethod(){
    TX C begin;
    TX C end;
  }
  TX A end;
}
/**
	REQUIRED加到了A上，A事务外部没有事务了，那么其就开启了一个新的事务了！
	
	REQUIRED加到了B上，B的外部有没有事务？有，那么B的事务就不要了，其融入了A的事务当中了，以TX A为主！
	
	REQUIRED加到了C上，C的情况和B的情况相同！
	
	REQUIRED加到了B和C上，那么事务嵌套的情况就不存在了，只剩下事务A了！这就保证了事务的原子性了！
*/
```

##### 4.4.2.2 SUPPORTS

1.外部不存在事务	不开启新的事务

2.外部存在事务		融合到外部事务中

```java
@Transactional
public void aMethod(){
  TX A begin;
 	@Transactional
  bMethod(){
    TX B begin;
    TX B end;
  }
  TX A end;
}
/**
	SUPPORTS加到了A上，A事务外部没有事务了，他自己也不开启事务！
	
	SUPPORTS加到了B上，B事务外部存在事务，他就融合到了外部事务当中了！
*/
```

##### 4.4.2.3 REQUIRED_NEW

1.外部不存在事务	开启新的事务

2.外部存在事务		挂起外部事物，创建新的事务，新事务执行完毕后，再执行外部事务

什么场景下使用呢？

```java
//日志功能
@Transactional
logMethod{
  //记录 谁 + 时间 + 什么事 + 结果
  insert(); //持久化日志信息
}
//后续每个业务操作，都会存在调用logMethod()进行日志信息的持久化
```

```markdown
分析分析：
1.这个场景使用REQUIRED行不行？
答：不行，外部不存在事务的情况下，开启新的事务，这个可以，但是外部存在事务，就进行了融合，这就不行了，如果外部

2.事物中嵌套的业务逻辑或者子事务发生了错误，那么外部事务就进行了回滚，日志信息就被回滚掉了！我尼玛
这个场景下使用SUPPORTS行不行？
答：不行，外不存在事务的情况下SUPPORTS和REQUIRED情况相同的，日志服务在最外层，开不开启事务不太重要吧
```



#### 4.4.3 传播属性的默认值

```markdown
[mybatis.service.impl.UserServiceImpl.register]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
PROPAGATION_REQUIRED:就是传播属性的默认值
```

推荐传播属性的使用方式

```markdown
1.增删改		 直接使用默认值 REQUIRED
2.查询		  显式的指定传播属性的值为SUPPORTS
```



### 4.5 只读属性

```markdown
readonly:针对于只进行查询操作的业务方法，可以加入只读属性，提供运行效率
默认值：false
```

实例代码

```java
package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
//只能读取到，已经提交的数据，解决脏读问题
//@Transactional(isolation = Isolation.READ_COMMITTED)
@Transactional
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void register(User user) {
        userDAO.save(user);
//        throw new RuntimeException("异常了兄弟！");
    }
  	
		/**
     * 我们说这个PROPAGATION.SUPPORTS适用于读操作
     *  外部存在事务，融入到外部事务
     *  外部不存在事务，不开启新的事务
     *  readonly:适用于只读操作，效率最高
     * @param name
     * @param password
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void login(String name, String password) {
        
    }
}
```

### 4.6 超时属性

```markdown
timeout:指定了事务等待的最长时间

1.为什么事务要进行等待呢？
答：当一个事务(TX A)对行数据(DATA ID:5)进行了使用，ISOLATION.REPETABLE_READ那么，这行数据(DATA ID:5)就被事务(TX A)加上了一把行锁，那事务(TX B)也要对这行数据(DATA ID:5)进行访问，就得等待事务(TX A)结束。
当前事务访问数据时，有可能访问的数据被别的事务进行加锁的处理，那么此时本事务就必须进行等待。

2.timeout的时间单位为秒
如果设置的时间过了，就会抛出异常

3.如何应用
@Transactional(timeout=2)
```

测试样例

```java
package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
//只能读取到，已经提交的数据，解决脏读问题
//@Transactional(isolation = Isolation.READ_COMMITTED)
@Transactional(timeout = 2)
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void register(User user) {
        //加载前面有用
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userDAO.save(user);
        //加在后面没用
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        
//        throw new RuntimeException("异常了兄弟！");
    }

    /**
     * 我们说这个PROPAGATION.SUPPORTS适用于读操作
     *  外部存在事务，融入到外部事务
     *  外部不存在事务，不开启新的事务
     *  readonly:适用于只读操作，效率最高
     * @param name
     * @param password
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void login(String name, String password) {

    }
}
```

```java
@Test
public void TestSpringMybatisTransaction() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-mybatis.xml");
  UserService userService = (UserService) applicationContext.getBean("userService");
  userService.register(new User("小王1","xiaowangba"));
}
```

抛出的异常

```markdown
org.springframework.transaction.TransactionTimedOutException: Transaction timed out: deadline was Wed Jun 02 08:38:36 CST 2021
```

超时属性的默认值

```markdown
timeout:-1
怎么理解？最终是由对应的数据库进行设置
```

### 4.7 异常属性

```markdown
1. 什么是异常属性？
答：还记得咱们之前，在开启事务的方法中抛出了一个异常嘛？
[throws new RuntimeException("异常了兄弟！")]; 
现在将其改成[throws new Exception("异常异常!")];
```

Exception示例

```java
package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
//只能读取到，已经提交的数据，解决脏读问题
//@Transactional(isolation = Isolation.READ_COMMITTED)
@Transactional
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    public UserDAO getUserDAO() {return userDAO;}
    public void setUserDAO(UserDAO userDAO) {this.userDAO = userDAO;}

    @Override
    public void register(User user) throws Exception{
        userDAO.save(user);
        throw new Exception("异常异常");
    }

    /**
     * 我们说这个PROPAGATION.SUPPORTS适用于读操作
     *  外部存在事务，融入到外部事务
     *  外部不存在事务，不开启新的事务
     *  readonly:适用于只读操作，效率最高
     * @param name
     * @param password
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void login(String name, String password) {}
}
```

测试代码

```java
@Test
public void TestSpringMybatisTransaction() {
  ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-mybatis.xml");
  UserService userService = (UserService) applicationContext.getBean("userService");
  try {
    userService.register(new User("小王1","xiaowangba"));
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

运行结果

```markdown
2021-06-02 08:52:48 DEBUG DataSourceTransactionManager:740 - Initiating transaction commit
2021-06-02 08:52:48 DEBUG DataSourceTransactionManager:330 - Committing JDBC transaction on Connection [com.mysql.cj.jdbc.ConnectionImpl@77825085]
2021-06-02 08:52:48 DEBUG DataSourceTransactionManager:389 - Releasing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@77825085] after transaction
java.lang.Exception: 异常异常
```

结合日志与数据库分析

```markdown
从上面的日志信息可以看到"Committing JDBC transaction on Connection"的描述，心里一惊，再一看数据库
select * from tab_user_info;确实能查到"小王1"的数据，凉了，数据竟然在抛出Exception的情况下，提交成功了！
```

异常小结

```markdown
Spring事务处理过程中
默认 对于Exception及其子类，采用的是提交策略
默认 对于RuntimeException及其子类，采用的是回滚的策略
```

如何设置

```markdown
rollbackFor = {java.lang.Exception.class,xxxx,xxxx}
noRollbackFor = {java.lang.Exception.class,xxxx,xxxx}
```

实例

```java
package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
//只能读取到，已经提交的数据，解决脏读问题
//@Transactional(isolation = Isolation.READ_COMMITTED)
@Transactional(rollbackFor = {java.lang.Exception.class},noRollbackFor = {java.lang.RuntimeException.class})
public class UserServiceImpl implements UserService {
    private UserDAO userDAO;
    public UserDAO getUserDAO() {return userDAO;}
    public void setUserDAO(UserDAO userDAO) {this.userDAO = userDAO;}

    @Override
    public void register(User user) throws Exception{
        userDAO.save(user);
//        throw new RuntimeException("异常了兄弟！");
        throw new Exception("异常异常");
    }

    /**
     * 我们说这个PROPAGATION.SUPPORTS适用于读操作
     *  外部存在事务，融入到外部事务
     *  外部不存在事务，不开启新的事务
     *  readonly:适用于只读操作，效率最高
     * @param name
     * @param password
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void login(String name, String password) {}
}

```



```markdown
2. 在开发中起到什么作用？
答：很少进行设置，建议一般都是用默认值
```

### 4.8 事务属性常见配置总结

```markdown
1. 隔离属性		建议默认值，ISOLATION.DEFAULT，根据对应数据库是隔离级别
2. 传播属性		建议默认值，REQUIRED，适合增删改；对于查询操作建议SUPPORTS
3. 只读属性		默认值false，适合增删改；设置为true，适用于查询操作
4. 超时属性		建议默认值-1，根据对应数据库的超时时间
5. 异常属性		建议默认值 RuntimeException ---> rollback;

增删改操作，@Transactional就够了
查询操作，@Transactional(propagation.Propagation.SUPPORTS,readOnly=true)
```



### 4.9 基于标签的事务配置方式

```markdown
一、基于注解@Transactional的事务配置的回顾
1. 在Spring中配置原始类ref中的DAO对象是Mybatis通过MapperScannerConfigure通过扫描包，帮我们自动创建的DAO对象
<bean id="userService" class="mybatis.service.impl.UserServiceImpl">
	<property name="userDAO" ref="userDAO"></property>
</bean>

2. 配置Spring的事务管理，其通过AOP技术，帮我们为Service进行增强，其需要连接池，使用JDBC Connection对事务进行管理
<bean id="dataSourceTransactionMnanager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
	<property name="dataSource" ref="dataSource"></property>
</bean>

3. 开启Spring事务注解功能
<tx:annotation-driven transaction-manager="dataSourceTransactionManager"/>

4. 在原始类中添加@Transactional注解
@Transactional(isolation,propagation,timeout,readOnly,rollbackFor,noRollbackFor....)
```

```markdown
二、基于标签的事务配置
与基于注解的配置方式，主要是第3和第4步
3. 主要是事务属性的配置，看到advice就能想到我们之前的MethodBeforeAdvice及后面的MethodInterceptor都是用于书写额外功能的
<tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">
	<tx:attributes>
		<!-- 
				等效于:
					@Transactional
					public void register(){}
		-->
		<tx:method name="register" isolation="" propagation=""></tx:method>
		<tx:method name="login"></tx:method>
	</tx:attributes>
</tx:advice>

4. 进行advice与切入点的组装
<aop:config>
	<aop:pointcut id="pc" expression="execution(* mybatis.service.impl.UserServiceImpl.register(..))"></aop:pointcut>
	<aop:advisor advice-ref="" pointcut-ref=""></aop:advisor>
</aop:config>
```

配置方式示例

```xml
<!-- ==============================================标签方式开始================================================== -->

    <!-- 配置需要添加事务的方法 -->
    <tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">
        <tx:attributes>
            <tx:method name="register" isolation="DEFAULT" propagation="REQUIRED" rollback-for="java.lang.Exception"/>
        </tx:attributes>
    </tx:advice>

    <!-- 组装切面(切点+额外功能) -->
    <aop:config>
        <aop:pointcut id="pc" expression="execution(* mybatis.service.impl.UserServiceImpl.register(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pc"/>
    </aop:config>

    <!-- ==============================================标签方式开始================================================== -->
```

实战中的应用方式

上面这种，一个方法对应写一个tx:method非常的不便，也难以维护，下面进行改进

```xml
    <!-- ==============================================标签方式开始================================================== -->

    <!-- 配置需要添加事务的方法 -->
<!--    <tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">-->
<!--        <tx:attributes>-->
<!--            <tx:method name="register" isolation="DEFAULT" propagation="REQUIRED" rollback-for="java.lang.Exception"/>-->
<!--        </tx:attributes>-->
<!--    </tx:advice>-->

        <!-- 实战建议 -->
    <tx:advice id="txAdvice" transaction-manager="dataSourceTransactionManager">
        <tx:attributes>
            <!--
                modify*，代表了以modify开头所有的方法
                如果实战中需要使用标签方式的事务管理，那么建议咱们原始类中的增删改方法，都以modify开头
             -->
            <tx:method name="modify*" isolation="DEFAULT" propagation="REQUIRED" rollback-for="java.lang.Exception"/>
            <!--
                这个*与上面的组合，就是代表了modify*以外的所有方法，命名无所谓，啥都行
                所以这个tx:method的顺序是有意义的
                既然上面是增删改，那这个就代表着查询
             -->
            <tx:method name="*" isolation="DEFAULT" propagation="SUPPORTS" read-only="true"/>
        </tx:attributes>
    </tx:advice>

    <!-- 组装切面(切点+额外功能) -->
<!--    <aop:config>-->
<!--        <aop:pointcut id="pc" expression="execution(* mybatis.service.impl.UserServiceImpl.register(..))"/>-->
<!--        <aop:advisor advice-ref="txAdvice" pointcut-ref="pc"/>-->
<!--    </aop:config>-->

    <!-- 实战建议，service包下面的所有方法 -->

    <aop:config>
        <aop:pointcut id="pc" expression="execution(* mybatis.service..*.*(..))"/>
        <aop:advisor advice-ref="txAdvice" pointcut-ref="pc"/>
    </aop:config>

    <!-- ==============================================标签方式开始================================================== -->
```

