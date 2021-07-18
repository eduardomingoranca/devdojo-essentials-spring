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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import study.project.essentials.domain.Anime;
import study.project.essentials.repository.AnimeRepository;
import study.project.essentials.requests.AnimePostRequestBody;
import study.project.essentials.requests.AnimePutRequestBody;
import study.project.essentials.util.AnimeCreator;
import study.project.essentials.util.AnimePostRequestBodyCreator;
import study.project.essentials.util.AnimePutRequestBodyCreator;
import study.project.essentials.wrapper.PageableResponse;

import java.util.List;

// iniciando o spring informando a porta
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// executando um bando de dados em memoria
@AutoConfigureTestDatabase
// exclui o banco de dados e o recria em cada metodo
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Test
    @DisplayName("listAll returns list of anime when successful")
    void listAllReturnsListOfAnimeWhenSuccessful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        // recebendo o nome de anime
        String expectedName = savedAnime.getName();

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        // verifica se o nome do anime não é nulo, vazio e retorna um anime
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        // verifica se o anime informado é existente
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when sucessful")
    void findByIdReturnsAnimeWhenSuccessful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        // recebendo um id
        Long expectedId = savedAnime.getId();

        // procurando um anime com id
        Anime anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);

        // verifica se o anime não é nulo
        Assertions.assertThat(anime).isNotNull();

        // verifica se o id é existente e não é nulo
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a list of anime when succesful")
    void findByNameReturnsListOfAnimeWhenSuccesful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        // recebendo o nome
        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        // verificando se o anime não é nulo, vazio e possui um
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        // verifica se o nome é existente
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByNameReturnsEmptyListOfAnimeWhenAnimeIsNotFound() {

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplate.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        // verificando se o anime não é nulo, vazio e possui um
        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful() {
        // criando um anime
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        // realizando uma requisição post de animes
        ResponseEntity<Anime> animeResponseEntity = testRestTemplate.postForEntity("/animes", animePostRequestBody, Anime.class);

        // verificando se o anime não é nulo
        Assertions.assertThat(animeResponseEntity).isNotNull();
        // verificando se o status é CREATED 201
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // verificando se o anime no corpo da requisição não e nula
        Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
        // verificando se o id do anime não é nulo
        Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replaceUpdatesAnimeWhenSuccesful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        // atualizando o anime
        savedAnime.setName("new name");

        // realizando uma requisição put
        ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/animes",
                HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        // verificando se o anime não é nulo
        Assertions.assertThat(animeResponseEntity).isNotNull();
        // verificando se o status é NO_CONTENT 204
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
