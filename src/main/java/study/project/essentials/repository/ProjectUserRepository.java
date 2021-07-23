package study.project.essentials.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.project.essentials.domain.ProjectUser;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    ProjectUser findByUsername(String username);
}
