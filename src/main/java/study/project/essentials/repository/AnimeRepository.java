package study.project.essentials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.project.essentials.domain.Anime;

import java.util.List;

public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByName(String name);
}
