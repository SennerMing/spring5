package lifecycle;

public class Person {
    private String name;
    private int age;

    //Person的初始化方法
    public void initMethod() {
        //这时Person的初始化方法
        System.out.println("Person的initMethod().....");
    }

    //Person的销毁方法
    public void destroyMethod() {
        //这时Person的初始化方法
        System.out.println("Person的destroyMethod().....");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("进行Person的name的设置");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        System.out.println("进行Person的age的设置");
        this.age = age;
    }

    public Person() {
        System.out.println("执行了无参数的构造器.....");
    }

    public Person(String name, int age) {
        System.out.println("执行了有参数的构造器.....");
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
