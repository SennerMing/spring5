package aop;

public class OrderServiceImpl implements OrderService {

    @Override
    public void showOrders() {
        System.out.println("OrderService.showOrders ....");
    }
}
