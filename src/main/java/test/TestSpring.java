package test;

import basic.Book;
import basic.Order;
import basic.User;
import bean.Employee;
import collection.District;
import collection.Student;
import factory.Production;
import factory.ProductionFactory;
import lifecycle.Person;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

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
        //获取到对象并使用
        System.out.println(employee);
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
        Production production1 = context.getBean("production", Production.class);
        //获取到对象并使用
        System.out.println(production1);
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


}
