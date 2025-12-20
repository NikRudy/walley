package org.fin.walley.service;
import org.fin.walley.domain.AppUser;
import org.fin.walley.domain.Role;
import org.fin.walley.repo.AppUserRepository;
import org.fin.walley.repo.CategoryRepository;
import org.fin.walley.repo.SubcategoryRepository;
import org.fin.walley.repo.TransactionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;


@Service
public class UserService {


    private final AppUserRepository userRepo;
    private final TransactionRepository txRepo;
    private final SubcategoryRepository subRepo;
    private final CategoryRepository catRepo;
    private final PasswordEncoder passwordEncoder;


    public UserService(AppUserRepository userRepo,
                       TransactionRepository txRepo,
                       SubcategoryRepository subRepo,
                       CategoryRepository catRepo,
                       PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.txRepo = txRepo;
        this.subRepo = subRepo;
        this.catRepo = catRepo;
        this.passwordEncoder = passwordEncoder;
    }


// -----------------
// READ
// -----------------


    public List<AppUser> findAll() {
        return userRepo.findAll();
    }


    public AppUser findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + id));
    }


// -----------------
// VALIDATION
// -----------------


    public boolean usernameTaken(String username) {
        if (username == null) return false;
        String u = username.trim();
        if (u.isEmpty()) return false;
        return userRepo.existsByUsername(u);
    }


// -----------------
// CREATE
// -----------------


    /** Создание пользователя админом (как в AdminUserController). */
    @Transactional
    public AppUser createByAdmin(String username, String rawPassword, Role role, boolean enabled) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        String u = username.trim();


        if (userRepo.existsByUsername(u)) {
            throw new IllegalArgumentException("Username is already taken");
        }


        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }


        AppUser user = new AppUser();
        user.setUsername(u);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role == null ? Role.USER : role);
        user.setEnabled(enabled);


        return userRepo.save(user);
    }
    /** Регистрация обычного пользователя (как в AuthController). */
    @Transactional
    public AppUser registerUser(String username, String rawPassword) {
// простая обёртка: всегда Role.USER и enabled=true
        return createByAdmin(username, rawPassword, Role.USER, true);
    }


// -----------------
// UPDATE
// -----------------


    /**
     * Обновление пользователя админом.
     * Username НЕ меняем (у вас в форме это поле выводится, но менять обычно запрещают).
     * Password меняем только если пришёл непустой.
     */
    @Transactional
    public AppUser updateByAdmin(Long id, String rawPassword, Role role, boolean enabled) {
        AppUser user = findById(id);


        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
        }


        if (role != null) {
            user.setRole(role);
        }


        user.setEnabled(enabled);


        return userRepo.save(user);
    }


// -----------------
// DELETE (важно для FK)
// -----------------


    /**
     * Удаляет пользователя и его данные, чтобы не было ошибки FK (SQLState 23503).
     * Порядок критичен.
     */
    @Transactional
    public void delete(Long userId) {
        if (userId == null) return;
        if (!userRepo.existsById(userId)) return;


// 1) transactions
        txRepo.deleteAllForUser(userId);


// 2) subcategories
        subRepo.deleteAllForUser(userId);


// 3) categories
        catRepo.deleteAllForUser(userId);


// 4) user
        userRepo.deleteById(userId);
    }
}