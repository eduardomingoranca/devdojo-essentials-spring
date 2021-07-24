package study.project.essentials.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import study.project.essentials.domain.Anime;
import study.project.essentials.domain.ProjectUser;
import study.project.essentials.repository.AnimeRepository;
import study.project.essentials.repository.ProjectUserRepository;
import study.project.essentials.requests.AnimePostRequestBody;
import study.project.essentials.util.AnimeCreator;
import study.project.essentials.util.AnimePostRequestBodyCreator;
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
    // utilizar o testRestTemplateRoleUserCreator
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    // testRestTemplate vai achar a porta de foi inicializada
    @Autowired
    // utilizar o testRestTemplateRoleUserCreator
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    // criando um usuario comum
    private static final ProjectUser USER = ProjectUser.builder()
            .name("Lon Hammond")
            .password("{bcrypt}$2a$10$p6/kb8QCnI08fibnlUcdhOlntgM8fwu3NGSNfHfIDYgSQ.zriZakK")
            .username("Lon")
            .authorities("ROLE_USER")
            .build();

    // criando um usuário administrador
    private static final ProjectUser ADMIN = ProjectUser.builder()
            .name("Eduardo Mingoranca")
            .password("{bcrypt}$2a$10$p6/kb8QCnI08fibnlUcdhOlntgM8fwu3NGSNfHfIDYgSQ.zriZakK")
            .username("Eduardo")
            .authorities("ROLE_ADMIN, ROLE_USER")
            .build();

    // configurando security
    @TestConfiguration
    @Lazy
    static class Config {
        // substituindo o rest template com autowired
        @Bean(name = "testRestTemplateRoleUser")
        // criando um rest template
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    // recebendo a porta estática
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("Lon", "project");
            return new TestRestTemplate(restTemplateBuilder);
        }
        // substituindo o rest template com autowired
        @Bean(name = "testRestTemplateRoleAdmin")
        // criando um rest template
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    // recebendo a porta estática
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("Eduardo", "project");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageObjectWhenSuccessful() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        // salvando o usuário
        projectUserRepository.save(USER);

        // recebendo o nome de anime
        String expectedName = savedAnime.getName();

        // realizando uma requisição get para a lista de animes
        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
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

        // salvando o usuário
        projectUserRepository.save(USER);

        // recebendo o nome de anime
        String expectedName = savedAnime.getName();

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
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

        // salvando o usuário
        projectUserRepository.save(USER);

        // recebendo um id
        Long expectedId = savedAnime.getId();

        // procurando um anime com id
        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

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

        // salvando o usuário
        projectUserRepository.save(USER);

        // recebendo o nome
        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
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
        // salvando o usuário
        projectUserRepository.save(USER);

        // realizando uma requisição get para a lista de animes
        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=dbz", HttpMethod.GET, null,
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
        // salvando o usuário
        projectUserRepository.save(USER);

        // criando um anime
        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();
        // realizando uma requisição post de animes
        ResponseEntity<Anime> animeResponseEntity = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);

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

        // salvando o usuário
        projectUserRepository.save(ADMIN);

        // atualizando o anime
        savedAnime.setName("new name");

        // realizando uma requisição put
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin",
                HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        // verificando se o anime não é nulo
        Assertions.assertThat(animeResponseEntity).isNotNull();
        // verificando se o status é NO_CONTENT 204
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns 403 when anime is not admin")
    void replaceReturns403WhenAnimeIsNotAdmin() {
        // criando um anime e salvando
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        // salvando o usuário
        projectUserRepository.save(USER);

        // atualizando o anime
        savedAnime.setName("new name");

        // realizando uma requisição put
        ResponseEntity<Void> animeResponseEntity = testRestTemplateRoleUser.exchange("/animes/admin",
                HttpMethod.PUT, new HttpEntity<>(savedAnime), Void.class);

        // verificando se o anime não é nulo
        Assertions.assertThat(animeResponseEntity).isNotNull();
        // verificando se o status é FORBIDDEN 403
        Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}
