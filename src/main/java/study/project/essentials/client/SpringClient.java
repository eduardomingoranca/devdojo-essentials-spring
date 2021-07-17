package study.project.essentials.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import study.project.essentials.domain.Anime;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class SpringClient {
    public static void main(String[] args) {
        // getForEntity >> retorna o objeto dentro de um wrapper
        ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/{id}", Anime.class,1);
        log.info(entity);

        // getForObject >> retorna o objeto diretamente
        Anime object =new RestTemplate().getForObject("http://localhost:8080/animes/{id}", Anime.class,1);

        log.info(object);

        Anime[] animes = new RestTemplate().getForObject("http://localhost:8080/animes/all", Anime[].class);

        log.info(Arrays.toString(animes));

        // convertendo array para lista
        //@formatter:off
        ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        // @formatter:on
        log.info(exchange.getBody());

        // criando um objeto anime
        Anime kingdom = Anime.builder().name("Kingdom").build();
        Anime kingdomSaved = new RestTemplate().postForObject("http://localhost:8080/animes/", kingdom, Anime.class);
        log.info("saved anime {}", kingdomSaved);
        
        Anime samuraiChampion = Anime.builder().name("Samurai Champion").build();
        ResponseEntity<Anime> samuraiChampionSaved = new RestTemplate().exchange("http://localhost:8080/animes/",
                HttpMethod.POST,
                new HttpEntity<>(samuraiChampion, createJsonHeader()),
                Anime.class);

        log.info("saved anime {}", samuraiChampionSaved);
    }

    // enviando um header informando que o content-type da requisição é um application/json
    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

}
