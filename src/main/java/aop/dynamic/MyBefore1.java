package aop.dynamic;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class MyBefore1 implements MethodBeforeAdvice {


    /**
     * Method method:
     * 	额外功能所增加给的那个原始方法，就像是UserService.login()、UserService.register()，这个东西是变化的，取决于我们要增加哪个
     *
     * Object[] args:
     * 	额外功能所增加给的那个原始方法的参数，如果增加的是UserService.login()，那么args[name,password]；如果是UserService.register()的话，那么args[new User()]，这个参数和第一个要增加的Method是息息相关的
     *
     * Object target:
     * 	额外功能所增加给的那个原始对象，就是UserServiceImpl或者OrderServiceImpl
     *
     * @param method
     * @param objects
     * @param o
     * @throws Throwable
     */
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("................MyBefore1 logging......................");
    }
}
