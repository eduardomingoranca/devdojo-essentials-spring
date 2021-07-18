package study.project.essentials.controller;

import org.assertj.core.api.Assertions;
import org.hibernate.validator.constraints.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.project.essentials.domain.Anime;
import study.project.essentials.requests.AnimePostRequestBody;
import study.project.essentials.requests.AnimePutRequestBody;
import study.project.essentials.service.AnimeService;
import study.project.essentials.util.AnimeCreator;
import study.project.essentials.util.AnimePostRequestBodyCreator;
import study.project.essentials.util.AnimePutRequestBodyCreator;

import java.util.Collections;
import java.util.List;

// informa que será utilizado o JUnit com Spring
@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    // @InjectMocks >> é utilizada quando se quer testar a classe em si
    @InjectMocks
    private AnimeController animeController;

    // @Mock >> é utilizada para todas as classes que estão sendo usadas dentro da classe injetada
    @Mock
    private AnimeService animeServiceMock;

    @BeforeEach
    void setUp() {
        // quando retorna uma lista o anime já esta salvo no bando de dados
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao listAll paginado não importando o argumento passado retorno um AnimePage
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);

        // quando executar dentro do controller uma chamada ao listAll sem paginação retornando uma lista de anime com id valido
        BDDMockito.when(animeServiceMock.listAllNonPageable())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao findById procurando um anime com id valido
        BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                  .thenReturn(AnimeCreator.createValidAnime());

        // quando executar dentro do controller uma chamada ao findByName procurando o nome do anime e retornando uma lista de anime com id valido
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao save passando qualquer tipo de objeto retorna uma anime valido
        BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        // não faça nada quando executar dentro do controller uma chamada ao replace qualquer tipo de objeto
        BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageObjectWhenSuccessful() {
        // informando o nome de anime
        String expectedName = AnimeCreator.createValidAnime().getName();
        // recebendo uma lista paginada de animes
        Page<Anime> animePage = animeController.list(null).getBody();

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
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.listAll().getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findById returns anime when sucessful")
    void findByIdReturnsAnimeWhenSuccessful() {
        // informando um id
        Long expectedId = AnimeCreator.createValidAnime().getId();

        // procurando um anime com id
        Anime anime = animeController.findById(1).getBody();

        // verifica se o anime não é nulo
        Assertions.assertThat(anime).isNotNull();

        // verifica se o id informado é existente e não é nulo
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByName returns a list of anime when succesful")
    void findByNameReturnsListOfAnimeWhenSuccesful() {
        String expectedName = AnimeCreator.createValidAnime().getName();

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns an empty list of anime when anime is not found")
    void findByNameReturnsEmptyListOfAnimeWhenAnimeIsNotFound() {
         /* quando executar o metodo findByName com qualquer tipo de
          * string retorna um lista vazia. */
        BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animeController.findByName("anime").getBody();

        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful() {
        // salvando um anime
        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

        // verificando se anime salvo não é nulo, e é existente
        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replaceUpdatesAnimeWhenSuccesful() {
        // recebe o anime alterado
        ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

        // verifica se o anime não é nulo
        Assertions.assertThat(entity).isNotNull();

        // verifica se o status retornado é NO_CONTENT
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

}