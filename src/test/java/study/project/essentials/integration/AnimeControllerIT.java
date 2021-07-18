package study.project.essentials.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import study.project.essentials.domain.Anime;
import study.project.essentials.repository.AnimeRepository;
import study.project.essentials.util.AnimeCreator;
import study.project.essentials.wrapper.PageableResponse;

// iniciando o spring informando a porta
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// executando um bando de dados em memoria
@AutoConfigureTestDatabase
class AnimeControllerIT {

    // testRestTemplate vai achar a porta de foi inicializada
    @Autowired
    private TestRestTemplate testRestTemplate;

    // recebendo a porta
    @LocalServerPort
    private int port;

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageObjectWhenSuccessful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        // recebendo o nome de anime
        String expectedName = savedAnime.getName();

        // realizando uma requisição get para a lista de animes
        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        // verifica se o nome do anime não é nulo
        Assertions.assertThat(animePage).isNotNull();

        // verifica se o nome do anime não é vazio e retorna um anime
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        // verifica se o anime informado é existente
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }
}
