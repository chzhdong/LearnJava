package org.example;

import jakarta.annotation.PreDestroy;
import org.example.service.AppService;
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
@ComponentScan
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

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ZoneId zoneId = context.getBean(ZoneId.class);
        System.out.println(zoneId);
        AppService appService = context.getBean(AppService.class);
        System.out.println(appService.getLogo());
        UserService userService = (UserService) context.getBean("userService");
        Validators validators = (Validators) context.getBean("validators");
        validators.validate("bob@example.com", "gfsfgdsg", "gfdggsfdgds");
        User user = userService.login("bob@example.com", "password");
        System.out.println(user.getName());
    }
}
