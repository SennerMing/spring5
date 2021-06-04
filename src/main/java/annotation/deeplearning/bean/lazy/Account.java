package annotation.deeplearning.bean.lazy;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value = true)
public class Account {
    public Account() {
        System.out.println("Account无参构造.....");
    }
}
