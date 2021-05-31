package aop.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//决定了注解加到哪里去
@Target(ElementType.METHOD)
//决定了这个注解在什么时间起作用
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

}
