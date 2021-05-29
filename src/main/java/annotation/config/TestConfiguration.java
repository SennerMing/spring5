package annotation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//我们定义了两个TestClass对象，分别是testClass1和testClass2
//我们如果在另外一个对象中直接使用@Autowire去注入的话，spring肯定不知道使用哪个对象
//会排除异常 required a single bean, but 2 were found
@Configuration
public class TestConfiguration {
   @Bean("testClass1")
   TestClass testClass1(){
       return new TestClass("TestClass1");
   }
   @Bean("testClass2")
   TestClass testClass2(){
       return new TestClass("TestClass2");
   }
}