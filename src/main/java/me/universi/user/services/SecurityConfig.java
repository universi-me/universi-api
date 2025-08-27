package me.universi.user.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            JWTFilter jwtFilter,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailedHandler authenticationFailedHandler
    ) throws Exception {

        CustomUsernamePasswordAuthenticationFilter customFilter = new CustomUsernamePasswordAuthenticationFilter();
        customFilter.setAuthenticationManager(authenticationManager());
        customFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customFilter.setAuthenticationFailureHandler(authenticationFailedHandler);
        customFilter.setFilterProcessesUrl("/signin");
        customFilter.setUsernameParameter("username");
        customFilter.setPasswordParameter("password");

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
                "/v3/api-docs/**",
                "/v3/api-docs.yaml/**"
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
                    .requestMatchers("/admin/**", "/h2-console/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                )

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                );

        urls
                .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return urls.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        PreAuthenticatedAuthenticationProvider preAuthUserProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthUserProvider.setPreAuthenticatedUserDetailsService( new UserDetailsByNameServiceWrapper<>(userService) );

        return new ProviderManager(preAuthUserProvider, daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("DEV").implies("ADMIN")
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler(RoleHierarchy roleHierarchy) {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }
}
