package yaml;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestYaml {

    @Test
    public void testYaml() {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(YamlConfig.class);
        Account account = (Account) applicationContext.getBean("account");
        System.out.println(account);
    }

}
