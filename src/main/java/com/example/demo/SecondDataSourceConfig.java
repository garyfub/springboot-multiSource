package com.example.demo;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Created by sam on 2017/7/30.
 */

@Configuration
@MapperScan(value = "com.example.demo.mapper.second",sqlSessionFactoryRef = "secondSqlSessionFactory")
public class SecondDataSourceConfig {


    @Bean(name = "secondDataSource")
    @ConfigurationProperties(prefix = "second.datasource")

    public DataSource secondDataSource(){
        System.out.println("-------second dataSource-------init");
        return DataSourceBuilder.create().build();
    }


    @Bean
    public DataSourceInitializer secondInitSql(@Qualifier("secondDataSource") DataSource dataSource){

        return init(dataSource,"secondSchema");
    }

    private DataSourceInitializer init(DataSource dataSource,String schameName){
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(dataSource);
        dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(schameName+".sql")));
        return dsi;
    }

    @Bean(name = "secondSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("secondDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
}
