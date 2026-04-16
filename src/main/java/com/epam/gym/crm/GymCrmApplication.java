package com.epam.gym.crm;

import com.epam.gym.crm.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymCrmApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GymCrmApplication.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            LOG.info("Gym CRM application context started. Beans loaded: {}", context.getBeanDefinitionCount());
        }
    }
}