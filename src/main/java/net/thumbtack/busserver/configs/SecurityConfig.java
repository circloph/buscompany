package net.thumbtack.busserver.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import net.thumbtack.busserver.security.CustomAccessDeniedHandler;
import net.thumbtack.busserver.security.CustomAuthenticationFailureHandler;
import net.thumbtack.busserver.security.CustomAuthenticationSuccessHandler;
import net.thumbtack.busserver.security.CustomUserDetailsService;
import net.thumbtack.busserver.security.CustomUsernamePasswordAuthenticationFilter;
import net.thumbtack.busserver.security.SessionFilter;
import net.thumbtack.busserver.security.SessionProvider;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private SessionProvider sessionProvider;
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception { 
        SessionFilter sessionFilter = new SessionFilter(sessionProvider);
        http
            .httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
            .and()
            .addFilterBefore(customUsernamePasswordAuthentication(), LogoutFilter.class)
            .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin().disable()
            .authorizeRequests()
                .antMatchers("/authenticated").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/sessions").authenticated()
                .antMatchers(HttpMethod.DELETE, "/api/accounts").authenticated()
                .antMatchers(HttpMethod.GET, "/api/sessions").authenticated()
                .antMatchers(HttpMethod.GET, "/api/accounts").authenticated()
                .antMatchers(HttpMethod.GET, "/api/clients").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/admins").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/clients").hasRole("USER")

                .antMatchers(HttpMethod.GET, "/api/buses").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/trips").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/trips/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/trips/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/trips/**/approve").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/orders").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/api/orders/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/orders").authenticated()
                .antMatchers(HttpMethod.GET, "/api/trips").authenticated()
                
                .antMatchers(HttpMethod.GET, "/api/settings").permitAll()
                .antMatchers(HttpMethod.POST, "/api/clients").permitAll()
                .antMatchers(HttpMethod.POST, "/api/admins").permitAll();
    }


    @Bean(name = "customUsernamePasswordAuthentication")
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthentication() throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(successLoginHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        return filter;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public CustomAuthenticationSuccessHandler successLoginHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }


    
}
