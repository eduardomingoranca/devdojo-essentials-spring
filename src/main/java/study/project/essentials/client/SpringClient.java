package study.project.essentials.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import study.project.essentials.domain.Anime;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class,1);
        log.info(entity);

        Anime object =new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class,1);

        log.info(object);
    }
}
