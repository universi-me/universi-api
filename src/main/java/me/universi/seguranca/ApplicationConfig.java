package me.universi.seguranca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ApplicationConfig
{
    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http
            .csrf().disable()
            .authorizeRequests().antMatchers("/registrar**").permitAll()
            .antMatchers("/css/*").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/logar").permitAll()
            .and()
            .logout().invalidateHttpSession(true)
            .clearAuthentication(true).permitAll();
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    // desabilitar SpringSecurity no H2 console
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer()
    {
        return (web) -> web
            .ignoring()
            .antMatchers("/h2-console/**");
    }
}