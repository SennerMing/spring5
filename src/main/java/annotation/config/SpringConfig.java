package annotation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//作为配置类，用以替代Xml配置文件，之前在annotation.xml中不是加了一个包扫描嘛？
//这个就不用了，直接写在配置类上就行了！
@Configuration
@ComponentScan(basePackages = {"annotation"})
public class SpringConfig {
}
