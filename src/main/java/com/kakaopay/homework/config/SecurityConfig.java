package com.kakaopay.homework.config;

import com.kakaopay.homework.filter.JwtTokenFilter;
import com.kakaopay.homework.support.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public FilterRegistrationBean someFilterRegistration(JwtTokenProvider jwtTokenProvider) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new JwtTokenFilter(jwtTokenProvider));
        registration.addUrlPatterns("/v1/*");

        return registration;
    }
}
