package converter;

import java.io.Serializable;
import java.util.Date;

public class Alienware implements Serializable {

    private String name;
    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Alienware{" +
                "name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
