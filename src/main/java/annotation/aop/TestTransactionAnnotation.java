package annotation.aop;

import annotation.aop.config.DruidConfig;
//import annotation.aop.config.MybatisConfig;
import annotation.aop.config.TransactionManagerConfig;
import annotation.aop.entity.User;
import annotation.aop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class TestTransactionAnnotation {


    @Test
    public void testAnnotationAnnotation() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("annotation.aop");

        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        UserService userService = (UserService) applicationContext.getBean("userServiceImpl");
//        userService.login("SennerMing", "123456");
        User user = new User();
        user.setName("SennerMing44");
        user.setPassword("123456");

        userService.register(user);


//        DataSource dataSource = (DataSource) applicationContext.getBean("dataSource");
//        System.out.println(dataSource);


    }

}
