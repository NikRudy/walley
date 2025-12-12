package org.fin.walley.service.finance;

import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.finance.AccountDto;
import org.fin.walley.repository.finance.AccountRepository;
import org.fin.walley.repository.finance.TransactionRepository;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса счетов пользователя.
 */
@Service
@RequiredArgsConstructor
@Transactional
class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public AccountDto createAccount(Long userId, AccountDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Account account = new Account();
        account.setName(dto.getName());
        account.setDescription(dto.getDescription());
        account.setCurrentBalance(dto.getCurrentBalance());
        account.setActive(true);
        account.setUser(user);

        Account saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Override
    public AccountDto updateAccount(Long userId, Long accountId, AccountDto dto) {
        Account account = getUserAccountOrThrow(userId, accountId);

        account.setName(dto.getName());
        account.setDescription(dto.getDescription());
        account.setCurrentBalance(dto.getCurrentBalance());

        Account saved = accountRepository.save(account);
        return toDto(saved);
    }

    @Override
    public void archiveAccount(Long userId, Long accountId) {
        Account account = getUserAccountOrThrow(userId, accountId);
        account.setActive(false);
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long userId, Long accountId) {
        Account account = getUserAccountOrThrow(userId, accountId);

        boolean hasTransactions = transactionRepository.findAll().stream()
                .anyMatch(t -> t.getAccount() != null && t.getAccount().getId().equals(accountId));

        if (hasTransactions) {
            throw new IllegalStateException("Cannot delete account with existing transactions");
        }

        accountRepository.delete(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AccountDto> getAccountById(Long userId, Long accountId) {
        return accountRepository.findById(accountId)
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getActiveAccounts(Long userId) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .filter(Account::isActive)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccounts(Long userId) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Account getUserAccountOrThrow(Long userId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        if (account.getUser() == null || !account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to user: " + userId);
        }
        return account;
    }

    private AccountDto toDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .name(account.getName())
                .description(account.getDescription())
                .currentBalance(account.getCurrentBalance())
                .active(account.isActive())
                .build();
    }
}