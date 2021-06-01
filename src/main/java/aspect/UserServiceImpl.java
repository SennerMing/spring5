package aspect;

import aop.User;
import aop.annotation.Log;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class UserServiceImpl implements UserService , ApplicationContextAware {

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    @Log
    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl.register  业务逻辑 + DAO");
//        throw new RuntimeException("存在异常！");

        /**
         * UserService userService = (UserService)ctx.getBean("userService")，这个返回的代理对象！
         * 当调用register()方法的时候，用的是这个代理对象，但是在这个方法内部调用的login方法，是原始对象！
         * 原始对象调用，怎么可能还有额外功能呢？
         * 解决方案：
         *      1.从Spring工厂获得UserService代理，再调用login
         *      ApplicationContext ctx = new ClassPathXmlApplicationContext("aspect.xml");
         *      UserService userService = ctx.getBean("userService");//Proxy对象
         *      userService.login()
         *      当时这样，咱们说，ApplicationContext是一个重量级的对象，不要老是创建他，很占用内存的，而且
         *      这样的开发方式影响性能不说，还非常之不优雅
         *
         *      2.让这个原始类实现ApplicationContextAware接口，让Spring工厂将ApplicationContext传进来
         *
         */
        aop.factory.UserService userService = (aop.factory.UserService) ctx.getBean("userService");
        userService.login("MingSenner", "654321");       //AOP的特殊问题
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceImpl.login 业务逻辑 + DAO");
        return true;
    }
}

