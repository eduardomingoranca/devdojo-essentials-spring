package study.project.essentials.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

// configuração que será carregada por toda a aplicação
@EnableWebSecurity
@Log4j2
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // protegendo o protocolo http
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // autorização de requisições no protocolo http
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // criptografia de senha
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {}", passwordEncoder.encode("test"));
        // usuarios em memória
        auth.inMemoryAuthentication()
                .withUser("eduardo")
                .password(passwordEncoder.encode("project"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("abraham")
                .password(passwordEncoder.encode("project"))
                .roles("USER");
    }
}
