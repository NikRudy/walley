package org.fin.walley.service.user;


import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.user.User;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Реализация UserDetailsService для интеграции доменной сущности User
 * с подсистемой аутентификации Spring Security.
 * <p>
 * Особенности реализации:
 * - в качестве "логина" может использоваться как username, так и email;
 * - роли пользователя (Role.name) преобразуются в SimpleGrantedAuthority;
 * - флаг User.enabled используется для определения, активен ли аккаунт.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;


    /**
     * Загрузка пользователя по имени пользователя или email.
     * <p>
     * Spring Security вызывает этот метод при аутентификации, передавая
     * значение из поля username формы логина. Мы интерпретируем его
     * универсально: сначала ищем по username, затем по email.
     *
     * @param usernameOrEmail значение поля логина (username или email)
     * @return объект UserDetails для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));


        Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
// Role.name ожидается в формате "ROLE_USER", "ROLE_ADMIN" и т.п.
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());


// Здесь мы используем стандартную реализацию UserDetails из Spring Security.
// Флаги accountNonExpired, credentialsNonExpired, accountNonLocked выставлены в true,
// но при необходимости их можно связать с дополнительными полями доменной сущности User.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(), // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}