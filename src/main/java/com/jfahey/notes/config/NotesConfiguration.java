package com.jfahey.notes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class NotesConfiguration {

    @Bean
    public Clock notesClock() {
        return Clock.systemDefaultZone();
    }
}
