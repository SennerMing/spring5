package beanpost;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
//实现了BeanPostProcessor，且没有实现任何方法，他竟然没有任何报错，你受得了吗
//这是JDK1.8之后的新特性 default函数
public class MyBeanPostProcessor implements BeanPostProcessor {

    /**
     * 这个还是要注意，一定要把这个Bean进行返回
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 我们就实现一个就得了
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Category) {
            Category category = (Category) bean;
            category.setName("小王");
        }
        return bean;
    }
}
