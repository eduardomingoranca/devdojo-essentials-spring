package study.project.essentials.util;

import study.project.essentials.domain.Anime;

public class AnimeCreator {

    // quando o metodo for chamado o anime não tem o id
    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Hajime no Ippo")
                .build();
    }

    // quando o metodo for chamado o anime que o id for valido
    public static Anime createValidAnime() {
        return Anime.builder()
                .name("Hajime no Ippo")
                .id(1L)
                .build();
    }

    // quando o metodo for chamado o anime tem o mesmo id porem o nome é diferente
    public static Anime createValidUpdatedAnime() {
        return Anime.builder()
                .name("Hajime no Ippo 2")
                .id(1L)
                .build();
    }

}