package aop;

public class UserServiceProxy implements UserService{

    //为目标类增加功能，那我们就得有目标类
    private UserServiceImpl userService = new UserServiceImpl();

    @Override
    public void register(User user) {
        System.out.println("UserServiceProxy.register logging .....");
        userService.register(user);
    }

    @Override
    public boolean login(String name, String password) {
        System.out.println("UserServiceProxy.login logging .....");
        return userService.login(name, password);
    }
}
