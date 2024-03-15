package io.muzoo.ssc.project.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class TimeZoneConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Set MariaDB timezone to UTC
        jdbcTemplate.execute("SET GLOBAL time_zone = '+00:00'");
    }
}
