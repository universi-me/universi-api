package me.universi.user.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.session.ConcurrentSessionFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailedHandler authenticationFailedHandler;
    private final JWTFilter jwtFilter;
    private final SessionRegistry sessionRegistry;
    private final AuthenticationProvider preAuthUserProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;

    @Value( "${server.servlet.context-path}" )
    private String contextPath;

    public SecurityConfig(
            CustomAccessDeniedHandler customAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailedHandler authenticationFailedHandler,
            JWTFilter jwtFilter,
            SessionRegistry sessionRegistry,
            AuthenticationProvider preAuthUserProvider,
            DaoAuthenticationProvider daoAuthenticationProvider
    ) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailedHandler = authenticationFailedHandler;
        this.jwtFilter = jwtFilter;
        this.sessionRegistry = sessionRegistry;
        this.preAuthUserProvider = preAuthUserProvider;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ConcurrentSessionFilter concurrentSessionFilter, SessionAuthenticationStrategy sessionAuthenticationStrategy, CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter) throws Exception {

        String[] publicEndpoints = new String[]{
                "/",
                "/health/**",
                "/login",
                "/signin",
                "/login/google",
                "/login/keycloak",
                "/login/keycloak/auth",
                "/signup",
                "/account",
                "/departments",
                "/departments/**",

                "/available/**",
                "/recovery-password",
                "/new-password",
                "/confirm-account/*",
                "/groups/current-organization",

                "/img",
                "/img/**",

                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
        };

        HttpSecurity urls = http

                .cors(cors -> cors.configurationSource(CustomCorsFilter.corsConfigurationSource()))

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/**")
                )

                .exceptionHandling(exception -> exception
                    .accessDeniedHandler(customAccessDeniedHandler)
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                )

                .authorizeHttpRequests((auth) -> auth
                    .requestMatchers(publicEndpoints).permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/h2-console/**").access(new WebExpressionAuthorizationManager(
                        "hasRole('ADMIN') and @environment.getProperty('spring.profiles.active') == 'test'"
                    ))
                    .anyRequest().authenticated()
                )

                .formLogin(form -> form
                    .loginPage("/login")
                    .failureUrl("/login")
                    .successHandler(authenticationSuccessHandler)
                )


                .logout(logout -> logout
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                )

                .sessionManagement(session -> session
                    .sessionAuthenticationStrategy(sessionAuthenticationStrategy)
                )

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );



        urls
                .addFilter(concurrentSessionFilter)
                .addFilterBefore(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return urls.build();
    }

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter() {
        return new ConcurrentSessionFilter(sessionRegistry, "/login?session=expired");
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_DEV > ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler(RoleHierarchyImpl roleHierarchy) {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        ConcurrentSessionControlAuthenticationStrategy concurrentSession = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        concurrentSession.setMaximumSessions(-1);
        concurrentSession.setExceptionIfMaximumExceeded(true);
        return new CompositeSessionAuthenticationStrategy(
            List.of(
                    concurrentSession,
                    new SessionFixationProtectionStrategy(),
                    new RegisterSessionAuthenticationStrategy(sessionRegistry)
            )
        );
    }



    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(daoAuthenticationProvider)
                .authenticationProvider(preAuthUserProvider)
                .build();
    }


    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(SessionAuthenticationStrategy sessionAuthenticationStrategy, AuthenticationManager authenticationManager) throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailedHandler);
        filter.setFilterProcessesUrl("/signin");
        filter.setUsernameParameter("username");
        filter.setPasswordParameter("password");
        return filter;
    }

}
