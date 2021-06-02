package mybatis.service.impl;

import mybatis.dao.UserDAO;
import mybatis.entity.User;
import mybatis.service.UserService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 我们只要在这个类上添加这个注解，那么这个类中的所有方法都有事务管理的额外功能了
 */
//只能读取到，已经提交的数据，解决脏读问题
//@Transactional(isolation = Isolation.READ_COMMITTED)
//@Transactional(rollbackFor = {java.lang.Exception.class},noRollbackFor = {java.lang.RuntimeException.class})
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public void register(User user) throws Exception{
        //设置了timeout，加在前面有用
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        userDAO.save(user);
        //设置了timeout，加在后面没用
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        throw new RuntimeException("异常了兄弟！");
//        throw new Exception("异常异常");
    }

    /**
     * 我们说这个PROPAGATION.SUPPORTS适用于读操作
     *  外部存在事务，融入到外部事务
     *  外部不存在事务，不开启新的事务
     *  readonly:适用于只读操作，效率最高
     * @param name
     * @param password
     */
    @Override
//    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void login(String name, String password) {

    }
}
