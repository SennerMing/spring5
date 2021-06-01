package aop.factory;

import aop.User;

public interface UserService {
    void login(String name, String password);

    void register(User user);
}
