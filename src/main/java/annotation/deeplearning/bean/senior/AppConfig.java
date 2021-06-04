package annotation.deeplearning.bean.senior;

import annotation.deeplearning.bean.senior.entity.ConnectionFactoryBean;
import annotation.deeplearning.bean.senior.entity.TestEntity;
import annotation.deeplearning.bean.senior.injection.UserDao;
import annotation.deeplearning.bean.senior.injection.UserDaoImpl;
import annotation.deeplearning.bean.senior.injection.UserService;
import annotation.deeplearning.bean.senior.injection.UserServiceImpl;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Configuration
//@ComponentScan(basePackages = {"annotation.deeplearning.bean.senior"},
//        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = {Controller.class})
////        @ComponentScan.Filter(type = FilterType.ASPECTJ,pattern = {"annotation.deeplearning.bean.senior"})
//        })
@ComponentScan(basePackages = {"annotation.deeplearning.bean.senior"},useDefaultFilters = false,
includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = {Controller.class})}
)
public class AppConfig {


//    @Bean
//    public TestEntity testEntity() {
//        return new TestEntity();
//    }


    /**
     * 创建一个复杂对象
     */
//    @Bean
//    public Connection connection() {
//        Connection connection = null;
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/musicianclub",
//                    "root", "*********");
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
//    @Bean
//    public Connection connection() {
//        Connection connection = null;
//        ConnectionFactoryBean connectionFactoryBean = new ConnectionFactoryBean();
//        try {
//            connection = connectionFactoryBean.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return connection;
//    }


//    @Bean
//    public UserDao userDao(){
//        System.out.println("AppConfig.userDao");
//        return new UserDaoImpl();
//    }
//
//    @Bean
//    public UserService userService(){
//        UserServiceImpl userServiceImpl = new UserServiceImpl();
////        userServiceImpl.setUserDao(userDao);
//        userServiceImpl.setUserDao(userDao());
//        return userServiceImpl;
//    }

}
