package com.core.openapigentest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.core.openapigentest", "org.openapi.example"})
public class AppConfiguration {
}
