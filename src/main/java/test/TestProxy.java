package test;

import aop.*;
import javafx.application.Application;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestProxy {

    @Test
    public void testSimpleProxy() {
        UserService userService = new UserServiceProxy();
        userService.login("SennerMing", "123456");
        userService.register(new User());

        OrderService orderService = new OrderServiceProxy();
        orderService.showOrders();
    }

    @Test
    public void testDynamic() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("proxy.xml");
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.login("SennerMing", "123465");
        userService.register(new User());
    }

    @Test
    public void testOrderDynamic() {
        ApplicationContext context = new ClassPathXmlApplicationContext("proxy.xml");
        OrderService orderService = (OrderService) context.getBean("orderService");
        orderService.showOrders();
    }


    @Test
    public void testMyProxyBeanPostProcessor() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aopfactory.xml");
        aop.factory.UserService userService = (aop.factory.UserService) applicationContext.getBean("userService");
        userService.login("SennerMing", "123456");
        userService.register(new User());
    }

    @Test
    public void testMyAnnotationAspect() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aspect.xml");
        aspect.UserService userService = (aspect.UserService) applicationContext.getBean("userService");
//        userService.login("SennerMing", "123465");
        userService.register(new User());
    }


}
