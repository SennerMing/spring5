package test;

import mybatis.entity.User;
import mybatis.dao.UserDAO;
import mybatis.service.UserService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

    @Test
    public void TestSpringMybatis() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-mybatis.xml");
        UserDAO userDAO = (UserDAO) context.getBean("userDAO");

        User user = new User("MingSenner", "654321");
        userDAO.save(user);
    }


    @Test
    public void TestSpringMybatisTransaction() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-mybatis.xml");
        UserService userService = (UserService) applicationContext.getBean("userService");
        try {
            userService.register(new User("小王3","xiaowangba"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
