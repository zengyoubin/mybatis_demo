package mybatis.spring_mybatis.test;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import mybatis.spring_mybatis.EntityScan;
import mybatis.spring_mybatis.MapperProvider;
import mybatis.spring_mybatis.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author ZYB
 * @since 2016-12-27 下午4:18
 */
@org.springframework.context.annotation.Configuration
@EntityScan(basePackages = "mybatis.bean")
@MapperScan(basePackages = "mybatis.spring_mybatis.test.mapper")
@ComponentScan(basePackageClasses = MapperProvider.class)
public class Config {
    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8");
        dataSource.setUser("root");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public static Configuration configuration() {
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, Configuration configuration) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{new PageInterceptor()/*,new InsertListInterceptor()*/});
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }
}
