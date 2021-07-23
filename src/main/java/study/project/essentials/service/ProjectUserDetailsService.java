package study.project.essentials.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.project.essentials.repository.ProjectUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectUserDetailsService implements UserDetailsService {
    private final ProjectUserRepository projectUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        // procurando o usuario caso não encontre explode uma exception
        return Optional.ofNullable(projectUserRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Project user not found"));
    }
}
