package aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestJDKProxy {

    /**
     * 1.ClassLoader
     *      可以借用TestJDKProxy的，也可以是UserServiceImpl的
     * 2.JDK 1.8 之前
     *      如果内部类需要访问外部类对象需要将外部类对象声明为final
     *      final UserService userService = new UserServiceImpl();
     * @param args
     */
    public static void main(String[] args) {
        //创建原始对象
        UserService userService = new UserServiceImpl();
        //通过这个接口类，告诉动态代理的方法，动态代理类该实现哪些方法
        Class[] interfaces = userService.getClass().getInterfaces();
        //JDK创建动态代理
        UserService userServiceProxy = (UserService) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), interfaces, new InvocationHandler() {
            /**
             *      Proxy 		先忽略掉，代表的是代理对象
             * 		Method 		额外功能所增加给的那个原始的方法
             * 		Object[] 	原始方法的参数
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("代码执行前的操作.......");
                return method.invoke(userService, args);
            }
        });

        userServiceProxy.login("SennerMing", "123456");
        userServiceProxy.register(new User());
    }

}
