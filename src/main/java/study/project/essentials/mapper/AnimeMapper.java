package study.project.essentials.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import study.project.essentials.domain.Anime;
import study.project.essentials.requests.AnimePostRequestBody;
import study.project.essentials.requests.AnimePutRequestBody;

@Mapper(componentModel = "spring")
public abstract class AnimeMapper {
    // criando uma instancia
    public static final AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);
    /*
       convertendo automaticamente de todos
       os atributos dentro das dto's
    */
    public abstract Anime toAnime(AnimePostRequestBody animePostRequestBody);
    public abstract Anime toAnime(AnimePutRequestBody animePutRequestBody);

}
