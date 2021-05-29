package concept;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BeanFactory {

    //那么，我们要将properties中的内容读取到我们的这个Properties中
    //对资源的读取我们一般都是用IO流进行读取，像这种静态的资源，我们一般都在类初始化的时候进行内容的读取
    private static Properties env = new Properties();

    static{
        /**
         * 那么将耦合统一转移到了我们这个 资源文件夹，resources下面的my-application.properties中了
         * 第一步，获取IO输入流
         * 第二步，将properties中的内容封装到Properties中，供我们的后续使用
         */
        InputStream inputStream = BeanFactory.class.getResourceAsStream("/my-application.properties");
        try {
            env.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Animal getAnimal() {
        /**
         * 我们将耦合，转移到一个小小的"全限定名"的字符串上了
         * 那，我们完全可以将这些个"全限定名"的字符串转移到一个单独的资源文件中进行统一的管理
         */
        Animal animal = null;
        Class clazz = null;
        try {
            clazz = Class.forName(env.getProperty("animal"));
            animal = (Animal) clazz.newInstance();
            animal.setAge(1);
            animal.setName("SennerMing");
            System.out.println(animal);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return animal;
    }

    public static AnimalDao getAnimalDao() {
        AnimalDao animalDao = null;

        try {
            Class clazz = Class.forName(env.getProperty("animalDao"));
            animalDao = (AnimalDao) clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return animalDao;
    }

    public static <T> T getBean(String name, Class clazz) {
        T obj = null;
        try {
            Class clazz1 = Class.forName(env.getProperty(name));
            obj = (T) clazz1.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return obj;
    }



    public static void main(String[] args) {
        Animal animal = BeanFactory.getBean("animal",Animal.class);
        System.out.println(animal);
    }


}
