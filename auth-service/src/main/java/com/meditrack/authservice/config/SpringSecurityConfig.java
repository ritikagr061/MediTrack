package com.meditrack.authservice.config;

//import com.meditrack.authservice.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserDetailsService customAuthentication;
//    @Autowired
//    private JwtFilter jwtFilter;
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//        http.csrf(customizer->customizer.disable());
//        http.authorizeHttpRequests(customizer->customizer
//                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .requestMatchers("/auth/login","/auth/register-user")
//                .permitAll()
//                .anyRequest().authenticated())
//        ;
//        //to allow clients like postman to interact
//        http.httpBasic(Customizer.withDefaults());
//        http.sessionManagement(customizer->customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//        //we are going to add jwtFilter(custom filter) before UserName
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }


    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); //daoAuthenticationProvider is responsible for checking user thru db.
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(11));
        authProvider.setUserDetailsService(customAuthentication);

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


}
