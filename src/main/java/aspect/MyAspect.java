package aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 加上这个@Aspect注解那么这个类就代表了一个切面(Aspect = Advice[额外功能] + Pointcut[额外功能要放在哪执行])
 *
 * 回顾我们在Spring框架中，之前是如何进行切面的实现的
 *      1.额外功能
 *      public class MyMethodInterceptor implements MethodInterceptor{
 *
 *          public Object invoke(MethodInvocation methodInvocation){
 *              //sout("原函数调用之前的额外功能....")
 *              Object ret = methodInvocation.proceed();
 *              //sout("原函数调用之后的额外功能....")
 *              return ret;
 *          }
 *      }
 *      2.切入点
 *      我们之前是在Spring中进行切入点的配置
 *      <aop:config>
 *          <aop:pointcut id="pc" expression="execution(* *(..))"></aop:pointcut>
 *      <aop:config/>
 *      其实里面最终要的就是这个切入点的表达式，就是需要额外功能要执行的位置，回到下面的around()方法
 *
 *      3.在Spring中进行配置
 */
@Aspect
public class MyAspect {

//    @Pointcut("execution(* login(..))")   //切入点表达式转移到这了
    @Pointcut("execution(* *..UserServiceImpl.*(..))")  //AOP的特殊问题
    public void myPointcut(){}
    /**
     * 这个方法名字随便起的
     *
     * 1. 需要加上@Around的这个注解，这个注解就相当于之前需要实现MethodInterceptor这个接口
     *
     * 2. Object 就相当于之前的invoke()函数的返回值，就是原始方法的返回值
     *
     * 3. 之前invoke的方法中又MethodInvocation这个对象，能对原方法进行调用，那么ProceedingJoinPoint就和其等同
     *
     * 那么至此这个额外功能就开发完了，跳回到上面的切入点
     *
     * 其实这个@Around()里面就可以在里面写切入点表达式了
     *
     * @return
     */
//    @Around("execution(* login(..))")
    @Around(value="myPointcut()")       //写上打了@Pointcut注解的方法
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        Object ret = null;
        try {
            System.out.println("----------- aspect logging around--------------");
            ret = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

//    @Around("execution(* login(..))")
    @Around(value="myPointcut()")       //写上打了@Pointcut注解的方法
    public Object around1(ProceedingJoinPoint proceedingJoinPoint){
        Object ret = null;
        try {
            System.out.println("----------- aspect logging around 1--------------");
            ret = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return ret;
    }

}
