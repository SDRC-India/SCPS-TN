package in.co.sdrc.scpstn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = { "org.sdrc.usermgmt.core", "in.co.sdrc.scpstn", "in.co.sdrc.sdrcdatacollector" })
 @EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan(basePackages = { "in.co.sdrc.scpstn.domain", "in.co.sdrc.sdrcdatacollector.jpadomains" })
@EnableJpaRepositories(basePackages = { "in.co.sdrc.scpstn.repository",
		"in.co.sdrc.sdrcdatacollector.jparepositories" })
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
		MongoDataAutoConfiguration.class })

public class ScpstnApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ScpstnApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ScpstnApplication.class);
	}
}
