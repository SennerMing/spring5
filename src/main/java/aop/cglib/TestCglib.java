package aop.cglib;

import aop.User;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class TestCglib {

    public static void main(String[] args) {
        //创建原始对象
        UserService userService = new UserService();
        //通过cglib方式创建动态代理
        /**
         * Proxy.newProxyInstance(classLoader,interfaces,invocationHandler)
         *
         * 1.那么cglib，也是使用动态字节码技术，所以也需要classLoader进行Class对象的创建
         * 2.是不需要父接口了，但是cglib不需要接口了，需要的是继承的父类
         * 3.也是我们要自己实现的额外功能
         *
         * Enhancer.setClassLoader()
         * Enhancer.setSuperClass()
         * Enhancer.setCallback() -----> MethodInvocationHandler
         *
         * 重要的就是Enhancer.create() ---> 创建代理
         */
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(TestCglib.class.getClassLoader());
        enhancer.setSuperclass(userService.getClass());

        MethodInterceptor interceptor = new MethodInterceptor() {
            /**
             * 等同于InvocationHandler 中的 invoke
             * @param o
             * @param method
             * @param objects
             * @param methodProxy
             * @return
             * @throws Throwable
             */
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("Cglib enhance .....");
                Object ret = method.invoke(userService, objects);
                return ret;
            }
        };

        enhancer.setCallback(interceptor);

        UserService userServiceProxy = (UserService) enhancer.create();
        userServiceProxy.login("SennerMing", "123456");
        userServiceProxy.register(new User());

    }
}
