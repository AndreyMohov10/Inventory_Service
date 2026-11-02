package org.example.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
open class Config {
    @Bean
    open fun datasource(
        @Value($$"${spring.datasource.url}") url: String,
        @Value($$"${spring.datasource.username}") user: String,
        @Value($$"${spring.datasource.password}") password: String
    ): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.postgresql.Driver")
        dataSource.url = url
        dataSource.username = user
        dataSource.password = password
        return dataSource
    }
}