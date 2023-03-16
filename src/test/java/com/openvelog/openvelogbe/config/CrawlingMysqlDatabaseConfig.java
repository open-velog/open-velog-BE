package com.openvelog.openvelogbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("test")
@EnableJpaRepositories(
        basePackages = "com.openvelog.openvelogbe.crawling",
        entityManagerFactoryRef = "crawlingMysqlEntityManagerFactory",
        transactionManagerRef = "crawlingMysqlTransactionManager"
)
public class CrawlingMysqlDatabaseConfig {
    @Bean
    @ConfigurationProperties("crawling.datasource")
    public DataSource CrawlingMysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean crawlingMysqlEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(CrawlingMysqlDataSource());
        factory.setPackagesToScan("com.openvelog.openvelogbe.crawling");
        factory.setPersistenceUnitName("crawling");

        // Set the JPA vendor adapter
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);

        // Set JPA properties from the injected JpaProperties bean
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        factory.setJpaPropertyMap(jpaProperties);

        return factory;
    }

    @Bean
    public JpaTransactionManager crawlingMysqlTransactionManager(EntityManagerFactory crawlingMysqlEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(crawlingMysqlEntityManagerFactory);
        return transactionManager;
    }
}
