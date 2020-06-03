package ru.ekhart86.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.ekhart86.dao.JdbcSingerDao;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class SpringJdbcConfig {

    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.user}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Value("${embedded.schema}")
    private String schema;
    @Value("${embedded.data}")
    private String testData;

    @Bean
    public DataSource dataSourceH2() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(schema)
                .addScript(testData)
                .build();
    }

    @Bean
    public DataSource mysqlDataSourceMySQL() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcSingerDao jdbcSingerDaoMySQL() {
        JdbcSingerDao jdbcSingerDao = new JdbcSingerDao();
        jdbcSingerDao.setDataSource(mysqlDataSourceMySQL());
        return jdbcSingerDao;
    }

    @Bean
    public JdbcSingerDao jdbcSingerDaoH2() {
        JdbcSingerDao jdbcSingerDao = new JdbcSingerDao();
        jdbcSingerDao.setDataSource(dataSourceH2());
        return jdbcSingerDao;
    }
}
