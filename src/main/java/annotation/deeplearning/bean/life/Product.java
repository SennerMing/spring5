package annotation.deeplearning.bean.life;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Scope(value = "singleton")
//@Scope(value = "prototype")
public class Product {

    @PostConstruct
    public void myInit() {
        System.out.println("Product myInit().....");
    }

    @PreDestroy
    public void myDestroy() {
        System.out.println("Product myDestroy()......");
    }

}
