package com.xantrix.webapp.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JWTWebSecurityConfig 
{
	private static String REALM = "REAME";
	
	/*
	@Autowired
	private JwtUnAuthorizedResponseAuthenticationEntryPoint jwtUnAuthorizedResponseAuthenticationEntryPoint;
	 */

	@Autowired
	@Qualifier("customUtenteDetailsService")
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenAuthorizationOncePerRequestFilter jwtAuthenticationTokenFilter;
	
	@Bean
	static PasswordEncoder passwordEncoderBean() 
	{
		return new BCryptPasswordEncoder();
	}

	private static final String[] USER_MATCHER = { "/utenti/cerca/tutti", "/utenti/inserisci/"};
	private static final String[] ADMIN_MATCHER = { "/utenti/cerca/userid/**", "/utenti/admin/**", "utenti/elimina/**"};

	@Bean
	@SneakyThrows
    SecurityFilterChain securityFilterChain(HttpSecurity http) 
	{
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(e -> e.realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint()))
			.addFilterBefore(jwtAuthenticationTokenFilter,  UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(authz ->
						authz
								.requestMatchers("/auth").permitAll()
								.requestMatchers(ADMIN_MATCHER).hasRole("ADMIN")
								.requestMatchers(USER_MATCHER).hasRole("USER")
								.anyRequest().permitAll()
				);
		
		return http.build();
	}

	@Bean
	JwtUnAuthorizedResponseAuthenticationEntryPoint getBasicAuthEntryPoint()
	{
		return new JwtUnAuthorizedResponseAuthenticationEntryPoint();
	}
	
	@Autowired
	@SneakyThrows
	public void configureGlobal(AuthenticationManagerBuilder auth) 
	{
	    	auth
	    		.userDetailsService(userDetailsService)
	    		.passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() 
	{
		  List<String> allowedHeaders = new ArrayList<String>();
		  allowedHeaders.add("Authorization");
		  allowedHeaders.add("Content-Type");
		  allowedHeaders.add("Accept");
		  allowedHeaders.add("x-requested-with");
		  allowedHeaders.add("Cache-Control");

	      CorsConfiguration configuration = new CorsConfiguration();
	      configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080/"));
	      configuration.setAllowedMethods(Arrays.asList("GET","POST","OPTIONS","DELETE","PUT"));
	      configuration.setMaxAge((long) 3600);
	      configuration.setAllowedHeaders(allowedHeaders);

	      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	      source.registerCorsConfiguration("/**", configuration);
	      
	      return source;
	 }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
}
