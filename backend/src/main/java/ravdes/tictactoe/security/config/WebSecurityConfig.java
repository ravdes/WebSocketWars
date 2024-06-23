package ravdes.tictactoe.security.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@AllArgsConstructor
@EnableWebSecurity

public class WebSecurityConfig {

	@Bean
	public PasswordEncoder getPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				    .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
				    .authorizeHttpRequests(auth -> auth
					.requestMatchers("/registration/**", "/login/**").permitAll()
					.anyRequest().authenticated())
				    .build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
		corsConfiguration.setAllowedMethods(List.of("GET", "POST"));
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedHeaders(List.of("*"));
		corsConfiguration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}
}
