package com.project_management.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RagDataSourceConfig {

    @Bean(name = "ragDataSource")
    @ConfigurationProperties(prefix = "rag.datasource")
    public DataSource ragDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "ragJdbcTemplate")
    public JdbcTemplate ragJdbcTemplate(
            @Qualifier("ragDataSource") DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}