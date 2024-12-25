package org.example;

import org.example.service.User;
import org.example.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        UserService userService = (UserService) context.getBean("userService");
        User user = userService.login("bob@example.com", "password");
        System.out.println(user.getName());
    }
}