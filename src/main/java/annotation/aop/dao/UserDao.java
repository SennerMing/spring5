package annotation.aop.dao;

import annotation.aop.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    int save(User user);
}
