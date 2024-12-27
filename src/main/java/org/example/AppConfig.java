package org.example;

import jakarta.annotation.PreDestroy;
import org.example.service.AppService;
import org.example.service.MailService;
import org.example.service.User;
import org.example.service.UserService;
import org.example.validator.Validators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.time.ZoneId;

@Configuration
@ComponentScan(basePackages = "org.example")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("classpath:/app.properties")
public class AppConfig {

    @Bean
    @Profile("!test")
    ZoneId createZoneId() {
        return ZoneId.systemDefault();
    }

    @Bean
    @Profile("test")
    ZoneId createZoneIdForTest() {
        return ZoneId.of("America/New_York");
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = (UserService) context.getBean(UserService.class);
        User user = userService.login("bob@example.com", "password");
        System.out.println(userService.getClass());

        MailService mailService = (MailService) context.getBean(MailService.class);
        mailService.getTime();
        System.out.println(mailService.getClass());
    }
}
