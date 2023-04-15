package com.openvelog.openvelogbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Value("#{${test-app.jpa}}")
    private Map<String, String> jpaPropertiesMap;

    @Value("${test-app.datasource.driver}")
    private String driverClassName;

    @Value("${test-app.datasource.url}")
    private String url;

    @Value("${test-app.datasource.username}")
    private String username;

    @Value("${test-app.datasource.password}")
    private String password;

    @Bean
    public DataSource testAppMysqlDataSource() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName(driverClassName);
        dataSourceProperties.setUrl(url);
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean testAppEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(testAppMysqlDataSource());
        factory.setPackagesToScan("com.openvelog.openvelogbe.common.entity", "com.openvelog.openvelogbe.common.repository");
        factory.setPersistenceUnitName("testApp");

        // Set the JPA vendor adapter
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factory.setJpaVendorAdapter(vendorAdapter);

        // Set JPA properties from the injected JpaProperties bean
        factory.setJpaPropertyMap(jpaPropertiesMap);

        return factory;
    }

    @Bean
    @Primary
    public JpaTransactionManager testAppMysqlTransactionManager(EntityManagerFactory testAppEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(testAppEntityManagerFactory);
        return transactionManager;
    }

}
