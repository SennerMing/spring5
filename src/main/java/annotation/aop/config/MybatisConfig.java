//package annotation.aop.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.mapper.MapperScannerConfigurer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//
//@Configuration
//public class MybatisConfig {
//
////    @javax.annotation.Resource(name = "dataSource")
////    private DataSource dataSource;
//
//
//    @javax.annotation.Resource
//    private DataSource dataSource;
//
//    //1.SqlSessionFactory
//    //  1.dataSource
//    //  2.mapperLocation
//    @Bean
////    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws Exception {
//    public SqlSessionFactoryBean sqlSessionFactoryBean(){
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setTypeAliasesPackage("annotation.aop.entity");
////        sqlSessionFactoryBean.setMapperLocations(new ClassPathResource("/mapper/UserDAOMapper.xml"));
//        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources = new Resource[0];
//        try {
//            resources = resourcePatternResolver.getResources("classpath:/mapper/*Mapper.xml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        sqlSessionFactoryBean.setMapperLocations(resources);
//
//        return sqlSessionFactoryBean;
//    }
//
//    //2.MapperScannerConfigure
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        //下面这句其实可以不用
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
//        mapperScannerConfigurer.setBasePackage("annotation.aop.dao");
//        return mapperScannerConfigurer;
//    }
//
//}
