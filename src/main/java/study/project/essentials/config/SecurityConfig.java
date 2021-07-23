package study.project.essentials.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import study.project.essentials.service.ProjectUserDetailsService;

// configuração que será carregada por toda a aplicação
@EnableWebSecurity
@Log4j2
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@SuppressWarnings("java:55344")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ProjectUserDetailsService projectUserDetailsService;

    /**
     * basicAuthenticationFilter
     * UsernamePasswordAuthenticationFilter
     * DefaultLoginPageGeneratingFilter
     * DefaultLogoutPageGeneratingFilter
     * FilterSecurityInterceptor
     * Authentication -> Authorization
     * @param http
     * @throws Exception
     * */
    // protegendo o protocolo http
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // autorização de requisições no protocolo http
        http.csrf().disable()
              //  .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .authorizeRequests()
                .antMatchers("/animes/admin/**").hasRole("ADMIN")
                .antMatchers("/animes/**").hasRole("USER")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // criptografia de senha
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {}", passwordEncoder.encode("project"));
        // usuarios em memória
        auth.inMemoryAuthentication()
                .withUser("Noah")
                .password(passwordEncoder.encode("project"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("Tyler")
                .password(passwordEncoder.encode("project"))
                .roles("USER");

        // usuarios no banco de dados
        auth.userDetailsService(projectUserDetailsService)
        .passwordEncoder(passwordEncoder);
    }
}
