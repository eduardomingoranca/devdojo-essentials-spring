package study.project.essentials.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import study.project.essentials.domain.Anime;

import javax.validation.ConstraintViolationException;
import java.util.List;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save creates anime when Successful")
    void savePersistAnimeWhenSuccessful() {
        Anime animeToBeSaved = createAnime();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);
        // verifica se o anime salvo no bando de dados não é nulo
        Assertions.assertThat(animeSaved).isNotNull();
        // verifica se o id do anime salvo no bando de dados não é nulo
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        // verifica se o anime salvo é igual ao valor enviado
        Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Save updates anime when Succesful")
    void saveUpdateAnimeWhenSuccesful() {
        Anime animeToBeSaved = createAnime();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);
        // enviando um nome de anime
        animeSaved.setName("Overlord");
        // salvando o novo nome do anime no banco de dados
        Anime animeUpdated = this.animeRepository.save(animeSaved);

        Assertions.assertThat(animeUpdated).isNotNull();
        Assertions.assertThat(animeUpdated.getId()).isNotNull();
        Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Find By Name returns list of anime when Succesful")
    void findByNameReturnsListOfAnimeWhenSuccesful() {
        Anime animeToBeSaved = createAnime();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        // obtendo o nome do anime salvo no banco de dados
        String name = animeSaved.getName();
        // recebendo uma lista de animes do banco de dados
        List<Anime> animes = this.animeRepository.findByName(name);

        /* verifica se a lista de animes não é vazia e
           se o banco de dados contem um anime salvo */
        Assertions.assertThat(animes)
                .isNotEmpty()
                .contains(animeSaved);
    }

    @Test
    @DisplayName("Find By Name returns empty list when no anime is found")
    void findByNameReturnsEmptyListWhenAnimeIsNotFound() {
        List<Anime> animes = this.animeRepository.findByName("invalid");
        // verifica se o nome do anime é invalido/vazio
        Assertions.assertThat(animes).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void saveThrowsConstraintViolationExceptionWhenNameIsEmpty() {
        // instanciando o objeto anime
        Anime anime = new Anime();

        // verifica se acontece uma exception quando o nome é vazio/invalido
        Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
                  .isInstanceOf(ConstraintViolationException.class);
    }

    // metodo que retorna um objeto anime
    private Anime createAnime() {
        return Anime.builder()
                .name("Hajime no Ippo")
                .build();
    }

}