package annotation.aop.service;

import annotation.aop.entity.User;

public interface UserService {
    void register(User user);

    void login(String name, String password);
}
