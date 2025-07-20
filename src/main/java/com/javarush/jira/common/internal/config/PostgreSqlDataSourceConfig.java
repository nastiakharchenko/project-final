package com.javarush.jira.common.internal.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("native")
public class PostgreSqlDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
//    Dotenv dotenv = Dotenv.configure().directory("sensitive-properties.env").load();
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/jira");
//        dataSource.setUsername(dotenv.get("SPRING_DATASOURCE_USERNAME"));
//        dataSource.setPassword(dotenv.get("SPRING_DATASOURCE_PASSWORD"));
//        return dataSource;
//    }
}
