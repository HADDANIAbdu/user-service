package com.fooddelivery.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter scopesConverter =
                new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                    Collection<GrantedAuthority> authorities = new ArrayList<>(scopesConverter.convert(jwt));

                    Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
                    if(realmAccess != null){
                        Object rolesObject = realmAccess.get("roles");
                        if(rolesObject instanceof List<?> roles){
                            for(Object role : roles){
                                if(role instanceof String roleName){
                                    authorities.add(new SimpleGrantedAuthority("ROLE_"+roleName.toUpperCase()));
                                }
                            }
                        }
                    }
                    return authorities;
        }
        );
        return converter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(sm -> sm.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                ))
                .csrf(c -> c.disable())
                .authorizeHttpRequests(
                req -> req.requestMatchers("/actuator/health")
                        .permitAll().anyRequest().authenticated()
                ).oauth2ResourceServer(
                        oa -> oa.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                ).build();
    }
}
