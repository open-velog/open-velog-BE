package com.openvelog.openvelogbe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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
    @Value("#{${crawling.jpa}}")
    private Map<String, String> jpaPropertiesMap;

    @Value("${crawling.datasource.driver}")
    private String driverClassName;

    @Value("${crawling.datasource.url}")
    private String url;

    @Value("${crawling.datasource.username}")
    private String username;

    @Value("${crawling.datasource.password}")
    private String password;

    @Bean(name = "crawlingMysqlDataSource")
    public DataSource CrawlingMysqlDataSource() {
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName(driverClassName);
        dataSourceProperties.setUrl(url);
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        return dataSourceProperties.initializeDataSourceBuilder().build();
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
        factory.setJpaPropertyMap(jpaPropertiesMap);

        return factory;
    }

    @Bean
    public JpaTransactionManager crawlingMysqlTransactionManager(EntityManagerFactory crawlingMysqlEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(crawlingMysqlEntityManagerFactory);
        return transactionManager;
    }
}
