package study.project.essentials.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// configuração global
@Configuration
public class EssentialsWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // configurando a quantidade de elementos em uma página
        PageableHandlerMethodArgumentResolver argumentResolver = new PageableHandlerMethodArgumentResolver();
        argumentResolver.setFallbackPageable(PageRequest.of(0, 5));
        resolvers.add(argumentResolver);
    }

}
