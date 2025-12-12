package org.fin.walley.service.user;

import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.user.Role;
import org.fin.walley.repository.user.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса ролей.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Set<String> getAllRoleNames() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean roleExists(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
}
