package study.project.essentials.controller;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import study.project.essentials.domain.Anime;
import study.project.essentials.service.AnimeService;
import study.project.essentials.util.AnimeCreator;

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
        // quando executar dentro do controller uma chamada o AnimeService.listAll não importando o argumento passado retorno um AnimePage
        BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any()))
                .thenReturn(animePage);
    }

    @Test
    @DisplayName("List returns list of anime inside page object when successful")
    void listReturnsListOfAnimeInsidePageObjectWhenSuccessful() {
        // nome de anime esperado
        String expectedName = AnimeCreator.createValidAnime().getName();
        // recebendo uma lista paginada de animes
        Page<Anime> animePage = animeController.list(null).getBody();

        // verifica se o nome do anime não é nulo
        Assertions.assertThat(animePage).isNotNull();

        // verifica se o nome do anime não é vazio e retorna um anime
        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1);

        // verifica se o anime informado é igual ao anime armazenado
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

}