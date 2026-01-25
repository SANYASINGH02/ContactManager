package com.scm.config;

import java.io.IOException;
import java.net.PasswordAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.scm.services.impl.SecurityCustomUserDetailsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    /*
     * // user create and login using java code with in-memory service
     * 
     * @Bean
     * public UserDetailsService userDetailsService() {
     * 
     * //each password must have a password encoding prefix.
     * UserDetails user1 = User
     * .withDefaultPasswordEncoder() // not safe for production to use default
     * encoder
     * .username("admin123")
     * .password("admin123")
     * .roles("ADMIN", "USER")
     * .build();
     * 
     * System.out.println("encoded password: " + user1.getPassword());
     * 
     * UserDetails user2 = User
     * .withUsername("user123")
     * .password("user123")
     * .roles("USER")
     * .build();
     * 
     * // if you'll login with above user/pass without encoder then it will fail
     * with invalid credentails error, we've not mapped the password encoder i.e.
     * strategy of how we're encoding password
     * var inMemoryUserDetailsManager = new InMemoryUserDetailsManager(user1,
     * user2);
     * return inMemoryUserDetailsManager;
     * 
     */

    @Autowired
    private SecurityCustomUserDetailsService userDetailsService;

    @Autowired
    private OAuthAuthenticationSuccessHandler oauthHandler;

    // Configuration of authenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // object of user details service -> service which interact with users(from
        // database)
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        // object of password encoder
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean // object of password encoder
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // To filter the urls or pages - which one would be public and which one would
    // be private
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // configuration
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home", "/register").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        httpSecurity.exceptionHandling(ex -> {
            ex.authenticationEntryPoint((request, response, authException) -> {
                response.sendRedirect("/login");
            });
        });

        /** form default login */
        // httpSecurity.formLogin(Customizer.withDefaults());

        /** Customized Login Page */
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login"); // now default login page redirects to "/login"
            // after submitting login form, it will be submitted at "/authenticate"
            formLogin.loginProcessingUrl("/authenticate"); 
            formLogin.defaultSuccessUrl("/user/profile", true); // Use defaultSuccessUrl for proper redirect

            // formLogin.failureForwardUrl("/login?error=true"); - due to this error page is
            // not coming on entering wrong login info, instead below method works
            formLogin.failureUrl("/login?error=true");

            formLogin.usernameParameter("email"); // username field of login page named as `email`(by default its
                                                  // 'username') -> write name="email" in input tag for username field
                                                  // in login form(login.html)
            formLogin.passwordParameter("password"); // name of password field of login page is password

            /** which handler/class/method should run on successful login */
            // formLogin.successHandler(new AuthenticationSuccessHandler() {

            // @Override
            // public void onAuthenticationSuccess(HttpServletRequest request,
            // HttpServletResponse response,
            // Authentication authentication) throws IOException, ServletException {
            // // write logic here and redirect the page wherever you want
            // }

            // });

            /** which handler should run on failure of login */
            // formLogin.failureHandler(new AuthenticationFailureHandler() {

            // @Override
            // public void onAuthenticationFailure(HttpServletRequest request,
            // HttpServletResponse response,
            // AuthenticationException exception) throws IOException, ServletException {
            // // write logic here and redirect the page wherever you want
            // }

            // });

        });

        // by default csrf token is enabled & when it is enabled, hit post request on
        // logoutUrl(here '/logout')
        // OR disable the csrf token & by default request is of get category
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        /** OAUTH configuration */
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(oauthHandler);
        });

        /* customizing the logout url */
        httpSecurity.logout(logoutForm -> {
            logoutForm.logoutUrl("/do-logout");
            logoutForm.logoutSuccessUrl("/login?logout=true");
        });

        return httpSecurity.build();
    }

}
