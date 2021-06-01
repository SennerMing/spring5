package aop.factory;

import aop.User;

public class UserServiceImpl implements UserService{


    @Override
    public void login(String name, String password) {
        System.out.println("Factory UserServiceImpl.login.......");
    }

    @Override
    public void register(User user) {
        System.out.println("Factory UserServiceImpl.register.......");
    }
}
