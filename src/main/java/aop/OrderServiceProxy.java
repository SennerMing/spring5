package aop;

public class OrderServiceProxy implements OrderService{
    private OrderServiceImpl orderService = new OrderServiceImpl();
    @Override
    public void showOrders() {
        System.out.println("OrderServiceProxy.showOrders logging ....");
        orderService.showOrders();
    }
}
