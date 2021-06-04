package test;

import annotation.deeplearning.bean.Category;
import annotation.deeplearning.bean.User;
import annotation.deeplearning.bean.lazy.Account;
import annotation.deeplearning.bean.life.Product;
import annotation.deeplearning.bean.scope.Consumer;
import annotation.deeplearning.bean.senior.AppConfig;
import annotation.deeplearning.bean.senior.entity.TestEntity;
import annotation.deeplearning.bean.senior.injection.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;

public class TestAnnotation {
    @Test
    public void testComponent() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-annotation.xml");
        User user = (User) applicationContext.getBean("user");
        System.out.println(user.getId());
    }

    @Test
    public void testScope() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-annotation.xml");
        Consumer consumer = (Consumer) applicationContext.getBean("consumer");
        System.out.println(consumer);
        Consumer consumer1 = (Consumer) applicationContext.getBean("consumer");
        System.out.println(consumer1);
    }

    @Test
    public void testLazy() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-annotation.xml");
//        Account account = (Account) context.getBean("account");
//        System.out.println(account);

    }

    @Test
    public void testLife() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-annotation.xml");
        Product product = (Product) context.getBean("product");

        ((ClassPathXmlApplicationContext)context).close();
    }

    @Test
    public void testValue() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-annotation.xml");
        Category category = (Category) context.getBean("category");
        System.out.println(category.getId());
        System.out.println(category.getName());
    }

    @Test
    public void testComponentScan() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-annotation.xml");
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(name);
        }
    }


    @Test
    public void testConfiguration() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TestEntity testEntity = (TestEntity) context.getBean("testEntity");
        System.out.println(testEntity);
    }


    @Test
    public void testConnectionConfiguration() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Connection connection = (Connection) context.getBean("connection");
        System.out.println(connection);
    }

    @Test
    public void testBeanInjection() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) context.getBean("userService");
        userService.register();
    }


    @Test
    public void testComponentScanAnnotation() {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(name);
        }
    }

}
