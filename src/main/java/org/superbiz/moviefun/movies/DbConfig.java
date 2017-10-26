package org.superbiz.moviefun.movies;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean(name = "albumDataSource")
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "movieDataSource")
    public DataSource moviesDataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return hibernateJpaVendorAdapter;
    }

    @Bean(name = "movieLocalContainer")
    public LocalContainerEntityManagerFactoryBean getBeanForMovies(@Qualifier("movieDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("movies");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "albumLocalContainer")
    public LocalContainerEntityManagerFactoryBean getBeanForAlbums(@Qualifier("albumDataSource") DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albums");
        return localContainerEntityManagerFactoryBean;
    }

    @Bean(name = "movieTransactionManager")
    public PlatformTransactionManager getPlatformTransactionManagerForMovies(@Qualifier("movieLocalContainer") EntityManagerFactory emf){
        JpaTransactionManager platformTransactionManager = new JpaTransactionManager();
        platformTransactionManager.setEntityManagerFactory(emf);
        return platformTransactionManager;
    }

    @Bean(name = "albumTransactionManager")
    public PlatformTransactionManager getPlatformTransactionManagerForAlbums(@Qualifier("albumLocalContainer") EntityManagerFactory emf){
        JpaTransactionManager platformTransactionManager = new JpaTransactionManager();
        platformTransactionManager.setEntityManagerFactory(emf);
        return platformTransactionManager;
    }
}
