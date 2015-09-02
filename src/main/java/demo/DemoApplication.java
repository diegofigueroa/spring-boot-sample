package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    @Value("${database.url:jdbc:postgresql://localhost:5432/demo}")
    private String databaseUrl;

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setDefaultProperties(getApplicationProperties());
        app.run(args);
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl(databaseUrl);
        dataSource.setUsername("postgres");
        dataSource.setPassword("password");

        return dataSource;
    }

    private static Properties getApplicationProperties() {
        final Properties properties = new Properties();

        properties.setProperty("management.context-path", "/internal");

        return properties;
    }

}
