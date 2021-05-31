package aop.dynamic;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class Around implements MethodInterceptor {

    /**
     * invoke方法：
     *      额外功能书写在其中
     *
     * ==============================================================
     * 那么这个额外功能的执行时机：
     *
     *      原始方法执行之前
     *      原始方法执行之后
     *      原始方法执行 前 后
     *
     *==============================================================
     * 那么我们怎么判断这个额外功能的执行时机呢？
     *
     *      首先就要确定这个原始方法是怎样运行的
     *
     *      参数：
     *            MethodInvocation（Method）：额外功能所增加给的那个原始方法
     *            那有可能是UserService.login或UserService.register
     *
     *      methodInvocation.proceed()就代表着原始方法的运行！ [UserService.login()|UserService.register()]
     *
     *      我们知道原始方法是怎样运行的，就可以确定我们这个额外功能的运行时机
     *
     *==============================================================
     *那么这个返回值怎么理解？
     *      Object：原始方法的返回值
     *      如果我们MethodInvocation是UserService.login()还是UserService.register()
     *
     *      Object ret = methodInvocation.proceed()
     *
     *==============================================================
     * 事务就是典型的额外功能
     *      UserServiceImpl{
     *          register(){
     *
     *          }
     *      }
     *
     *      tx.begin()
     *      userServiceImpl.register();
     *      tx.commit()
     *==============================================================
     *
     *
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
//        System.out.println("MethodInterceptor before real method invoke...");
//        Object ret = null;
//        try {
//            ret = methodInvocation.proceed();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        System.out.println("MethodInterceptor after real method invoke...");
//        return ret;

        /**
         * 如果我们将原始方法的返回值，直接作为invoke()方法的返回值返回，那么MethodInterceptor不会影响原始方法的返回值的
         */
        System.out.println("---------logging-----------");
        Object ret = methodInvocation.proceed();
        return ret;
    }
}
