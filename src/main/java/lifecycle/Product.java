package lifecycle;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Product implements InitializingBean, DisposableBean {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("setName()......");
        this.name = name;
    }

    public Product() {
        System.out.println("Product().....");
    }

    public void myInit() {
        System.out.println("myInit....");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet()......");
    }

    public void myDestroy() {
        System.out.println("Destroy()....");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy().....");
    }

}
