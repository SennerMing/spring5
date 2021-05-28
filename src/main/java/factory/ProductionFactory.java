package factory;

import org.springframework.beans.factory.FactoryBean;

public class ProductionFactory implements FactoryBean<Production> {
    @Override
    public Production getObject() throws Exception {
        Production production = new Production();
        production.setName("桂格燕麦");
        return production;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
