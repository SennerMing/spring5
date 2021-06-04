//package annotation.aop.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class TransactionAspect {
//
//    @Pointcut("execution(* annotation.aop.service..*.*(..))")
//    public void transactionPointcut() {
//    }
//
//    @Around(value="transactionPointcut()")
//    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
//        Object ret = null;
//        try {
//            System.out.println("......... logging .........");
//            ret = proceedingJoinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//        return ret;
//    }
//}
