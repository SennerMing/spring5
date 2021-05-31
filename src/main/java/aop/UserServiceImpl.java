package aop;

import aop.annotation.Log;

public class UserServiceImpl implements UserService {

    @Log
    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
//        throw new RuntimeException("存在异常！");
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}
