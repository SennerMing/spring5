package service;

import dao.UserDao;
import dao.UserDaoImpl;

public class UserService {

    //创建UserDao属性类型，生成set方法
    private UserDao userDao;

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void add() {
        System.out.println("UserService add()......");
        userDao.update();
        //创建UserDao对象 -- 传统的方式
//        UserDao userDao = new UserDaoImpl();
//        userDao.update();
    }

}
