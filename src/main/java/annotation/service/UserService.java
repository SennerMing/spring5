package annotation.service;

import annotation.dao.UserDao;
import dao.UserDaoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

//等价于<bean id="userService" class="annotion.service.UserService"></bean>
//这个value可以不写，那么默认的就是首字母小写的 userService 驼峰命名法
//下面几个注解都可以进行Bean的创建
//@Component(value="userService")
@Service(value="userService")
//@Controller(value="userService")
//@Repository(value="userService")
public class UserService {

    //定义Dao类型的属性
    //不需要添加set的方法
    //直接添加属性注解就行
//    @Autowired
//    @Qualifier(value = "userDaoImpl1")
//    private UserDao userDao;

    @Resource(name = "userDaoImpl1")
    private UserDao userDao;

    @Value(value = "SennerMing")
    private String name;

    public void add() {
        System.out.println("UserService add()....");
        userDao.add();
    }

    public void setUserDao(UserDaoImpl userDao) {
    }
}
