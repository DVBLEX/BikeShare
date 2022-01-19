package com.beskyd.ms_control.security;

import com.beskyd.ms_control.business.usermanagement.User;
import com.beskyd.ms_control.business.usermanagement.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Bean
    AuthenticationSuccessHandler successHandler() {
        return new MyAuthenticationSuccessHandler();
    }

    @Bean
    LogoutSuccessHandler logoutHandler() {
        return new LogoutHandler();
    }

    @Bean
    AuthenticationFilter authenticationFilter() throws Exception{
        AuthenticationFilter authFilter = new AuthenticationFilter(userService);
        authFilter.setAuthenticationSuccessHandler(successHandler());
        authFilter.setAuthenticationFailureHandler(failureHandler());
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "PUT"));
        authFilter.setAuthenticationManager(authenticationManagerBean());
        return authFilter;
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()

                .antMatchers("/", "/login", "/msc-api/user/drop-password", "/msc-api/user/recover-password", "/views/users/password-recovery/**", 
        "/msc-api/distribution/get-order-pdf/*", "/msc-api/distribution/get-distribution-pdf/*" /*, "/msc-api/assets/products", "/msc-api/assets/products/**" */)
                .permitAll()
                .mvcMatchers("/views/home").hasAnyRole(User.ROLE_ADMIN, User.ROLE_OPERATOR, User.ROLE_PURCHASE_MANAGER, User.ROLE_SCHEME_LEADER, User.ROLE_FULFILLMENT_OPERATOR)
                .mvcMatchers("/views/assets-edit", "/views/scheme-stock-control", "/views/purchase-orders", "/views/suppliers").hasAnyRole(User.ROLE_ADMIN, User.ROLE_PURCHASE_MANAGER, User.ROLE_FULFILLMENT_OPERATOR)
                .mvcMatchers("/views/stock-requests", "/views/distribution").hasAnyRole(User.ROLE_ADMIN, User.ROLE_PURCHASE_MANAGER, User.ROLE_FULFILLMENT_OPERATOR, User.ROLE_SCHEME_LEADER)
                .mvcMatchers("/views/create-repair-report", "/views/routine-review").hasAnyRole(User.ROLE_ADMIN, User.ROLE_OPERATOR)
                .mvcMatchers("/views/assets-report", "/views/repair-reports", "/views/repair-history").hasAnyRole(User.ROLE_ADMIN, User.ROLE_OPERATOR, User.ROLE_SCHEME_LEADER)
                .mvcMatchers("/views/stock-balance").hasAnyRole(User.ROLE_ADMIN, User.ROLE_SCHEME_LEADER)
                .mvcMatchers("/views/*").hasRole(User.ROLE_ADMIN)
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout().deleteCookies("JSESSIONID").clearAuthentication(true)
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .logoutSuccessHandler(logoutHandler())
                .and()

                .sessionManagement()
                
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Bean
    AuthenticationFailureHandler failureHandler() {
        return new MyAuthenticationFailureHandler();
    }

    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(getApplicationContext().getBean(UserDetailsService.class))
                .passwordEncoder(passwordEncoder);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());

        return source;
    }

}
