package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setDefaultProperties(getApplicationProperties());
        app.run(args);
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl("jdbc:postgresql://localhost:5432/demo");
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
