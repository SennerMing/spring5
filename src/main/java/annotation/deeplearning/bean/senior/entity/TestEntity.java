package annotation.deeplearning.bean.senior.entity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
public class TestEntity {
    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
