package aop.dynamic;

import org.springframework.aop.MethodBeforeAdvice;
import java.lang.reflect.Method;

public class MyBefore implements MethodBeforeAdvice {

    /**
     * 需要把运行在原始方法执行之前运行的额外功能，书写在before中
     * @param method
     * @param objects
     * @param o
     * @throws Throwable
     */
    @Override
    public void before(Method method, Object[] objects, Object o) throws Throwable {
        System.out.println("--------- Method before advice log --------------");
    }
}
