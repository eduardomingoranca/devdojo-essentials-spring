package study.project.essentials.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import study.project.essentials.domain.Anime;

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

    // metodo que retorna um objeto anime
    private Anime createAnime() {
        return Anime.builder()
                .name("Hajime no Ippo")
                .build();
    }

}