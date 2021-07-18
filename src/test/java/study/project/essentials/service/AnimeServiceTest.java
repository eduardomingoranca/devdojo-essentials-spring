package study.project.essentials.service;

import org.assertj.core.api.Assertions;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.project.essentials.domain.Anime;
import study.project.essentials.exception.BadRequestException;
import study.project.essentials.repository.AnimeRepository;
import study.project.essentials.requests.AnimePostRequestBody;
import study.project.essentials.requests.AnimePutRequestBody;
import study.project.essentials.util.AnimeCreator;
import study.project.essentials.util.AnimePostRequestBodyCreator;
import study.project.essentials.util.AnimePutRequestBodyCreator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        // quando retorna uma lista o anime já esta salvo no bando de dados
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao listAll paginado não importando o argumento passado retorno um AnimePage
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        // quando executar dentro do controller uma chamada ao listAll sem paginação retornando uma lista de anime com id valido
        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao findById procurando um anime com id valido
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao findByName procurando o nome do anime e retornando uma lista de anime com id valido
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        // quando executar dentro do controller uma chamada ao save passando qualquer tipo de objeto retorna uma anime valido
        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("list returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageObjectWhenSuccessful() {
        // recebendo o nome de anime
        String expectedName = AnimeCreator.createValidAnime().getName();
        // recebendo uma lista paginada de animes
        Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

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
        // recebendo um nome de anime
        String expectedName = AnimeCreator.createValidAnime().getName();

        // recebendo uma lista de animes
        List<Anime> animes = animeService.listAllNonPageable();

        // verificando se os animes não são nulos, vazios ou se possui um
        Assertions.assertThat(animes)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        // verifica se o anime é existente
        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when sucessful")
    void findByIdOrThrowBadRequestExceptionReturnsAnimeWhenSuccessful() {
        // recebendo um id
        Long expectedId = AnimeCreator.createValidAnime().getId();

        // procurando um anime com id
        Anime anime = animeService.findByIdOrThrowBadRequestException(1);

        // verifica se o anime não é nulo
        Assertions.assertThat(anime).isNotNull();

        // verifica se o id é existente e não é nulo
        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when anime is not found")
    void findByIdOrThrowBadRequestExceptionThrowsBadRequestExceptionWhenAnimeIsNotFound() {
        // quando executar dentro do controller findById recebendo qualque valor long e a opção for vazia
        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        // retorna um BadRequestException que será lançado quando o id não for encontrado
        Assertions.assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> animeService.findByIdOrThrowBadRequestException(1));
    }

    @Test
    @DisplayName("findByName returns a list of anime when succesful")
    void findByNameReturnsListOfAnimeWhenSuccesful() {
        // recebendo um nome de anime
        String expectedName = AnimeCreator.createValidAnime().getName();

        // procurando um nome de anime
        List<Anime> animes = animeService.findByName("anime");

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
        /* quando executar o metodo findByName com qualquer tipo de
         * string retorna um lista vazia. */
        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        // procurando um nome de anime
        List<Anime> animes = animeService.findByName("anime");

        // verificando se o nome de anime é nulo e vazio
        Assertions.assertThat(animes)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("save returns anime when successful")
    void saveReturnsAnimeWhenSuccessful() {
        // salvando um anime
        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        // verificando se anime salvo não é nulo, e é existente
        Assertions.assertThat(anime)
                .isNotNull()
                .isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace updates anime when successful")
    void replaceUpdatesAnimeWhenSuccesful() {
        // verifica se o anime alterado foi com sucesso, caso contrario executa uma exception
        Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
                .doesNotThrowAnyException();
    }

}