package annotation.deeplearning.bean.senior.injection;

public class UserDaoImpl implements UserDao{
    @Override
    public void save() {
        System.out.println("UserDao save()......");
    }
}
