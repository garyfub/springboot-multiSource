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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Created by sam on 2017/7/30.
 */

@Configuration
@MapperScan(value = "com.example.demo.mapper.primary",sqlSessionFactoryRef = "primarySqlSessionFactory")
public class PrimaryDataSourceConfig {

    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource primaryDataSource(){
        System.out.println("-------primary dataSource-------init");
//        return DataSourceBuilder.create().build();

        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db = builder
                .setType(EmbeddedDatabaseType.H2) //等价于设置url=jdbc:h2:mem:testdb
                .addScript("primarySchema.sql")
                .build();
        return db;
    }

//两种方式执行schema.sql
//    @Bean
//    public DataSourceInitializer primaryInitSql(@Qualifier("primaryDataSource") DataSource dataSource){
//
//        return init(dataSource,"primarySchema");
//    }



//    private DataSourceInitializer init(DataSource dataSource,String schameName){
//        DataSourceInitializer dsi = new DataSourceInitializer();
//        dsi.setDataSource(dataSource);
//        dsi.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource(schameName+".sql")));
//        return dsi;
//    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
}
