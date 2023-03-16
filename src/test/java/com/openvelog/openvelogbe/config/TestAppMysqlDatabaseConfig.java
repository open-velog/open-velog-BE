package com.openvelog.openvelogbe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Profile("test")
@Configuration
@EnableJpaRepositories(
        basePackages = "com.openvelog.openvelogbe.common",
        entityManagerFactoryRef = "testAppEntityManagerFactory",
        transactionManagerRef = "testAppMysqlTransactionManager"
)
public class TestAppMysqlDatabaseConfig {
    @Bean
    @ConfigurationProperties("test-app.datasource")
    public DataSource TestAppMysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean testAppEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(TestAppMysqlDataSource());
        factory.setPackagesToScan("com.openvelog.openvelogbe.common.entity", "com.openvelog.openvelogbe.common.repository");
        factory.setPersistenceUnitName("testApp");

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
    public JpaTransactionManager testAppMysqlTransactionManager(EntityManagerFactory testAppEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(testAppEntityManagerFactory);
        return transactionManager;
    }
}
