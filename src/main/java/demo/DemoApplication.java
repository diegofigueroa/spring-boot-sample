package demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    @Value("${database.url:postgresql://postgres:password@localhost:5432/demo}")
    private String databaseUrl;

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplication(DemoApplication.class);
        app.setDefaultProperties(getApplicationProperties());
        app.run(args);
    }

    @Bean
    public DataSource dataSource() {
        final URI dbUri = URI.create(databaseUrl);
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath());
        dataSource.setUsername(dbUri.getUserInfo().split(":")[0]);
        dataSource.setPassword(dbUri.getUserInfo().split(":")[1]);

        return dataSource;
    }

    private static Properties getApplicationProperties() {
        final Properties properties = new Properties();

        properties.setProperty("management.context-path", "/internal");

        return properties;
    }

}
