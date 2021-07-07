
package com.example.appengine.demos.springboot;

import org.springframework.context.annotation.ComponentScan;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

//@EnableScheduling
@EnableWebMvc
@SpringBootApplication
@ComponentScan
public class SpringBootExampleApplication extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		System.out.println("Application going to start");
		SpringApplication.run(SpringBootExampleApplication.class, args);
		PropertyConfigurator.configure("src/main/resources/log4j.properties");
	}
	
	@Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        registry.viewResolver(resolver);
	}
}
