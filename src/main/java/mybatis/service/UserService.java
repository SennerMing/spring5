package mybatis.service;

import mybatis.entity.User;

public interface UserService {
    void register (User user) throws Exception;

    void login(String name, String password);
}
