package org.example.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.annotation.MetricTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ConditionalOnProperty(name="app.stmp", havingValue="true")
public class MailService {

    @Value("${app.zone}")
    private String zone;
    private ZoneId zoneId;

    @PostConstruct
    public void init() {
        this.zoneId = ZoneId.of(this.zone);
        System.out.println("Init mail service with ZoneId " + this.zoneId);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("shutdown mail service");
    }

    public void setZoneId(@Autowired(required = false) @Qualifier("z") ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @MetricTime("getTime")
    public String getTime() {
        return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public void sendLoginMail(User user) {
        System.err.println(String.format("Hi, %s! You are logged in at %s", user.getName(), getTime()));
    }

    public void sendRegistrationMail(User user) {
        System.out.println(String.format("Welcome, %s!", user.getName()));
    }
}
