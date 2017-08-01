package com.example.demo;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.SystemException;

/**
 * Created by sam on 2017/7/30.
 */

@Configuration
@EnableTransactionManagement
@MapperScan(value = "com.example.demo.mapper.primary",sqlSessionFactoryRef = "primarySqlSessionFactory")
public class PrimaryDataSourceConfig implements TransactionManagementConfigurer {

    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource primaryDataSource(){
        System.out.println("-------primary dataSource-------init");
        AtomikosDataSourceBean dataSourceBean = new AtomikosDataSourceBean();
        dataSourceBean.set
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "atomikosTransactionManager")
    public UserTransactionManager atomikosTransactionManager(){
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean(name = "atomikosUserTransaction")
    public UserTransactionImp atomikosUserTransaction(){
        UserTransactionImp atomikosUserTransation =new UserTransactionImp();
        try {
            atomikosUserTransation.setTransactionTimeout(100);
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return atomikosUserTransation;
    }

    @Bean
    public JtaTransactionManager txManager() {
       return new JtaTransactionManager(atomikosUserTransaction(),atomikosTransactionManager());
    }


    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return txManager();
    }



    @Bean
    public DataSourceInitializer primaryInitSql(@Qualifier("primaryDataSource") DataSource dataSource){

        return init(dataSource,"primarySchema");
    }



    private DataSourceInitializer init(DataSource dataSource,String schameName){
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(schameName+".sql")));
        return dsi;
    }



    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }


}
