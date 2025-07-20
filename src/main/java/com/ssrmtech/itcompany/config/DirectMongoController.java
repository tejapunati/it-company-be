package com.ssrmtech.itcompany.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DirectMongoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public ServletRegistrationBean<HttpServlet> mongoTestServlet() {
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setContentType("application/json");
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.setHeader("Access-Control-Allow-Methods", "GET");
                
                Map<String, Object> response = new HashMap<>();
                
                try {
                    response.put("status", "connected");
                    response.put("database", mongoTemplate.getDb().getName());
                    response.put("collections", mongoTemplate.getCollectionNames());
                } catch (Exception e) {
                    response.put("status", "error");
                    response.put("message", e.getMessage());
                }
                
                resp.getWriter().write(response.toString());
            }
        };
        
        return new ServletRegistrationBean<>(servlet, "/direct-mongo-test");
    }
    
    @Bean
    public ServletRegistrationBean<DispatcherServlet> rootDispatcherServlet() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(RootController.class);
        
        DispatcherServlet servlet = new DispatcherServlet(context);
        
        ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean<>(servlet, "/root/*");
        registration.setName("rootDispatcher");
        return registration;
    }
}