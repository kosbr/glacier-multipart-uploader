package com.github.kosbr.aws.config;

import org.hibernate.cfg.Environment;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.github.kosbr.aws.repository")
@EnableTransactionManagement
public class DAOConfiguration {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/.aws/aws-uploader";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    @Bean
    public DriverManagerDataSource dataSource() {
        final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(
                DB_CONNECTION, DB_USER, DB_PASSWORD
        );
        driverManagerDataSource.setDriverClassName(DB_DRIVER);
        return driverManagerDataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    Properties jpaProperties() {
        final Properties jpaProperties = new Properties();
        jpaProperties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        jpaProperties.put(Environment.HBM2DDL_AUTO, "update");
        jpaProperties.put(Environment.SHOW_SQL, "false");
        return jpaProperties;
    }


    @Bean
    public EntityManagerFactory entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
        entityManagerFactoryBean.setPackagesToScan("com.github.kosbr.aws.model");
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setJpaProperties(jpaProperties());
        entityManagerFactoryBean.setPersistenceUnitName("unit");
        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean.getObject();
    }

    @Bean
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }
}
