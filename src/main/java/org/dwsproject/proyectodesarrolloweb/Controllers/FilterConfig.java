package org.dwsproject.proyectodesarrolloweb.Controllers;

import org.dwsproject.proyectodesarrolloweb.Service.UserService;
import org.dwsproject.proyectodesarrolloweb.Service.UserSession;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    //This class is used to configure the filters of the application
    private final UserService userService;
    private final UserSession userSession;

    public FilterConfig(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilter() {
        //This method creates a new filter registration bean for the authentication filter
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthenticationFilter(userService, userSession));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}