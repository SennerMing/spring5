package annotation.aop.service.impl;

import annotation.aop.dao.UserDao;
import annotation.aop.entity.User;
import annotation.aop.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Transactional
@Service
public class UserServiceImpl implements UserService {

//    @Resource
//    private UserDao userDao;

    @Override
    public void register(User user){
        System.out.println("UserService register.....");
//        userDao.save(user);
        throw new RuntimeException("异常啦兄嘚");
    }

    @Override
    public void login(String name, String password) {
        System.out.println("UserService login ......");
//        userDao.login(name, password);
    }
}
