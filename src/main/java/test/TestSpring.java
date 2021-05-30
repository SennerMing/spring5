package test;

import annotation.config.SpringConfig;
import basic.Book;
import basic.Order;
import basic.User;
import bean.Department;
import bean.Employee;
import collection.District;
import collection.Student;
import concept.Animal;
import factory.ConnectionFactoryBean;
import factory.Production;
import javafx.application.Application;
import lifecycle.Person;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

import java.sql.Connection;

public class TestSpring {

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


    @Test
    public void testBook() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        //2.获取通过配置创建的对象
        Book book = context.getBean("book", Book.class);
        //获取到对象并使用
        System.out.println(book);
    }

    @Test
    public void testOrder() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        //2.获取通过配置创建的对象
        Order order = context.getBean("order", Order.class);
        //获取到对象并使用
        System.out.println(order);
    }


    @Test
    public void testRef() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("beans1.xml");
        //2.获取通过配置创建的对象
        UserService userService = context.getBean("userService", UserService.class);
        //获取到对象并使用
        userService.add();
    }

    @Test
    public void testInnerBean() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        //2.获取通过配置创建的对象
        Employee employee = context.getBean("employee", Employee.class);
        Department department = context.getBean("department", Department.class);
        //获取到对象并使用
        System.out.println(employee);
        System.out.println(department);
    }

    @Test
    public void testCollection() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("collection.xml");
        //2.获取通过配置创建的对象
        Student student = context.getBean("student", Student.class);
        //获取到对象并使用
        System.out.println(student);
    }

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

    @Test
    public void testFactory() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");
        //2.获取通过配置创建的对象
//        Production production1 = context.getBean("production", Production.class);
        Connection conn = context.getBean("conn", Connection.class);
        Connection conn1 = context.getBean("conn", Connection.class);
        //获取到对象并使用
        System.out.println(conn);
        System.out.println(conn1);
    }

    @Test
    public void testFactoryBean() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("factory.xml");
        //2.获取通过配置创建的对象
//        Production production1 = context.getBean("production", Production.class);
        ConnectionFactoryBean conn = context.getBean("&conn", ConnectionFactoryBean.class);
        //获取到对象并使用
        System.out.println(conn);
    }

    @Test
    public void testLifeCycle() {
        //1.加载我们写的Spring的xml配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("lifecycle.xml");
        //2.获取通过配置创建的对象
        Person person = context.getBean("person", Person.class);
        //获取到对象并使用
        System.out.println(person);

        //手动让Bean实例销毁
        ((ClassPathXmlApplicationContext)context).close();
    }

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

    @Test
    public void testFactoryMethod() {
        //1.获得Spring ApplicationContext 工厂
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //2.获取通过配置创建的对象，和我们自己实现的BeanFactory很像
//        Animal animal = (Animal) context.getBean("animal");

        /**
         * 此时在Spring的Bean工厂里面只有一个Bean标签的类型是Animal的，如果有两个id不同但是class都是Animal的
         * 声明的话，此时运行这个代码会报excepted single matching bean but found 2 : Animal
         * 想使用这个方法的话，必须要确保此时的配置文件中只能有一个Bean的class是Animal类型的
         */
//        Animal animal1 = context.getBean(Animal.class);

//        Animal animal2 = context.getBean("animal", Animal.class);

        /**
         * 像是applicationContext.xml中的一些Bean标签就是SpringContext so-called BeanDefinitions
         * 实际上就是那些个id值
         */
//        String[] beanDefinitionNames = context.getBeanDefinitionNames();
//        for (String definitionName : beanDefinitionNames) {
//            System.out.println(definitionName);
//        }

        /**
         * 根据类型获得其所有的id属性，别名
         */
//        String[] beanNames = context.getBeanNamesForType(Animal.class);
//        for (String beanName : beanNames) {
//            System.out.println(beanName);
//        }

        /**
         * 用于判断是否具有id值得BeanDefinition，他只能判断id不能判断name
         */
        boolean exist = context.containsBeanDefinition("animal");
        System.out.println(exist);

        /**
         * 用于判断是否存在指定id的bean，他既能判断id又能判断name
         */
        boolean exist1 = context.containsBean("ani2");
        System.out.println(exist1);

        //获取到对象并使用
//        System.out.println(animal);

        //手动让Bean实例销毁
        ((ClassPathXmlApplicationContext)context).close();
    }

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

    @Test
    public void testName() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Animal animal = (Animal) context.getBean("ani2");
        System.out.println(animal);
    }



}
