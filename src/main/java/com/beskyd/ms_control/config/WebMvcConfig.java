package com.beskyd.ms_control.config;


import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.beskyd.ms_control.business.general.MailService;
import com.beskyd.ms_control.business.general.SummaryInfoService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsCurrentValuesService;
import com.beskyd.ms_control.business.schemestocksontrol.values.AssetsMarginalValuesService;
import com.beskyd.ms_control.business.stockrequests.StockRequestService;
import com.beskyd.ms_control.business.usermanagement.UserService;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.beskyd.ms_control.business.*"}, excludeFilters={
    @ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value=SummaryInfoService.class)})
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Bean
    public InternalResourceViewResolver resolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/resources/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login-page");
    }
    
    @Bean
    public View jsonTemplate() {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setPrettyPrint(true);
        return view;
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder.json().timeZone(TimeZone.getTimeZone(ZoneId.systemDefault())).build()));
    }
    
    @Bean
    public ViewResolver viewResolver() {
        return new BeanNameViewResolver();
    }
    
    @Bean
    PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/resources/**")
            .addResourceLocations("/resources/");
    }
    
    @Bean
    public SummaryInfoService getSummaryInfoService(StockRequestService stockRequestService, AssetsCurrentValuesService acvService, AssetsMarginalValuesService amvService, MailService mailService, UserService userService) {
        return new SummaryInfoService(stockRequestService, acvService, mailService, userService);
    }
    
}
