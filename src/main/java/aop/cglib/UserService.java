package aop.cglib;

import aop.User;

public class UserService {

    public void login(String name, String password) {
        System.out.println("UserService login.....");
    }

    public void register(User user) {
        System.out.println("UserService register....");
    }

}
