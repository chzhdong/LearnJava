package org.example.service;

import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.MetricTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MailService mailService;
    private List<User> users = new ArrayList<>();

    @MetricTime(value = "login")
    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new RuntimeException("login failed");
    }

    public User getUser(long id) {
        return this.users.stream().filter(user -> user.getId() == id).findFirst().orElseThrow();
    }

    @MetricTime("register")
    public User register(String email, String password, String name) {
        users.forEach((user) -> {
            if(user.getEmail().equalsIgnoreCase(email)) {
                throw new RuntimeException("email exists");
            }
        });
        KeyHolder holder = new GeneratedKeyHolder();
        if(1 != jdbcTemplate.update(
                (Connection conn) -> {
                    var ps = conn.prepareStatement("INSERT INTO users (email, password, name) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, email);
                    ps.setObject(2, password);
                    ps.setObject(3, name);
                    return ps;
                },
                holder)
        ) {
            throw new RuntimeException("register failed");
        };
        User user = new User(holder.getKey().longValue(), email, password, name);
        users.add(user);
        mailService.sendRegistrationMail(user);
        return user;
    }

    public User getUserById(long id) {
        return jdbcTemplate.execute((Connection conn) -> {
           try(var ps = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
               ps.setObject(1, id);
               try(var rs = ps.executeQuery()) {
                   if(rs.next()) {
                       return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("name"));
                   }
                    throw new RuntimeException("user not found by id.");
               }
           }
        });
    }

    public User getUserByName(String name) {
        return jdbcTemplate.execute("SELECT * FROM users WHERE name = ?", (PreparedStatement ps) -> {
            ps.setObject(1, name);
            try(var rs = ps.executeQuery()) {
                if(rs.next()) {
                    return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("name"));
                }
                throw new RuntimeException("user not found by name.");
            }
        });
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?",
                (ResultSet rs, int rowNum) -> {
                    return new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"), rs.getString("name"));
                },
                email);
    }
}
