package annotation.controller;

import annotation.config.TestClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TestController {

    //此时这两个注解的连用就类似 @Resource(name="testClass1")
    @Autowired
    @Qualifier("testClass1")
    private TestClass testClass;


}