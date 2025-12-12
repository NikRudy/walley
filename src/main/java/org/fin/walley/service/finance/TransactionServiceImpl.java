package org.fin.walley.service.finance;

import lombok.RequiredArgsConstructor;
import org.fin.walley.domain.finance.Account;
import org.fin.walley.domain.finance.Category;
import org.fin.walley.domain.finance.Subcategory;
import org.fin.walley.domain.finance.Transaction;
import org.fin.walley.domain.finance.TransactionType;
import org.fin.walley.domain.user.User;
import org.fin.walley.dto.finance.AccountDto;
import org.fin.walley.dto.finance.CategoryDto;
import org.fin.walley.dto.finance.SubcategoryDto;
import org.fin.walley.dto.finance.TransactionDto;
import org.fin.walley.repository.finance.AccountRepository;
import org.fin.walley.repository.finance.CategoryRepository;
import org.fin.walley.repository.finance.SubcategoryRepository;
import org.fin.walley.repository.finance.TransactionRepository;
import org.fin.walley.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса транзакций.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    @Override
    public TransactionDto createTransaction(Long userId, TransactionDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + dto.getAccountId()));
        if (account.getUser() == null || !account.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Account does not belong to user: " + userId);
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));

        Subcategory subcategory = null;
        if (dto.getSubcategoryId() != null) {
            subcategory = subcategoryRepository.findById(dto.getSubcategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Subcategory not found: " + dto.getSubcategoryId()));
            if (subcategory.getCategory() == null || !subcategory.getCategory().getId().equals(category.getId())) {
                throw new IllegalArgumentException("Subcategory does not belong to the given category");
            }
        }

        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setAccount(account);
        tx.setCategory(category);
        tx.setSubcategory(subcategory);
        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setOccurredAt(dto.getOccurredAt());
        tx.setDescription(dto.getDescription());
        tx.setDeleted(false);

        Transaction saved = transactionRepository.save(tx);
        return toDto(saved);
    }

    @Override
    public TransactionDto updateTransaction(Long userId, Long transactionId, TransactionDto dto) {
        Transaction tx = getUserTransactionOrThrow(userId, transactionId);

        if (!tx.getAccount().getId().equals(dto.getAccountId())) {
            Account newAccount = accountRepository.findById(dto.getAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found: " + dto.getAccountId()));
            if (newAccount.getUser() == null || !newAccount.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Account does not belong to user: " + userId);
            }
            tx.setAccount(newAccount);
        }

        if (!tx.getCategory().getId().equals(dto.getCategoryId())) {
            Category newCategory = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));
            tx.setCategory(newCategory);
        }

        if (dto.getSubcategoryId() != null) {
            if (tx.getSubcategory() == null || !tx.getSubcategory().getId().equals(dto.getSubcategoryId())) {
                Subcategory newSubcategory = subcategoryRepository.findById(dto.getSubcategoryId())
                        .orElseThrow(() -> new IllegalArgumentException("Subcategory not found: " + dto.getSubcategoryId()));
                tx.setSubcategory(newSubcategory);
            }
        } else {
            tx.setSubcategory(null);
        }

        tx.setAmount(dto.getAmount());
        tx.setType(dto.getType());
        tx.setOccurredAt(dto.getOccurredAt());
        tx.setDescription(dto.getDescription());

        Transaction saved = transactionRepository.save(tx);
        return toDto(saved);
    }

    @Override
    public void softDeleteTransaction(Long userId, Long transactionId) {
        Transaction tx = getUserTransactionOrThrow(userId, transactionId);
        tx.setDeleted(true);
        transactionRepository.save(tx);
    }

    @Override
    public void hardDeleteTransaction(Long userId, Long transactionId) {
        Transaction tx = getUserTransactionOrThrow(userId, transactionId);
        transactionRepository.delete(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionDto> getTransactionById(Long userId, Long transactionId) {
        return transactionRepository.findById(transactionId)
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(userId))
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactions(Long userId,
                                                LocalDateTime from,
                                                LocalDateTime to,
                                                TransactionType type,
                                                Long accountId,
                                                Long categoryId,
                                                Long subcategoryId,
                                                boolean includeDeleted) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getUser() != null && t.getUser().getId().equals(userId))
                .filter(t -> from == null || !t.getOccurredAt().isBefore(from))
                .filter(t -> to == null || !t.getOccurredAt().isAfter(to))
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> accountId == null || (t.getAccount() != null && t.getAccount().getId().equals(accountId)))
                .filter(t -> categoryId == null || (t.getCategory() != null && t.getCategory().getId().equals(categoryId)))
                .filter(t -> subcategoryId == null || (t.getSubcategory() != null && t.getSubcategory().getId().equals(subcategoryId)))
                .filter(t -> includeDeleted || !t.isDeleted())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private Transaction getUserTransactionOrThrow(Long userId, Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        if (tx.getUser() == null || !tx.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Transaction does not belong to user: " + userId);
        }

        return tx;
    }

    private TransactionDto toDto(Transaction tx) {
        return TransactionDto.builder()
                .id(tx.getId())
                .userId(tx.getUser() != null ? tx.getUser().getId() : null)
                .accountId(tx.getAccount() != null ? tx.getAccount().getId() : null)
                .categoryId(tx.getCategory() != null ? tx.getCategory().getId() : null)
                .subcategoryId(tx.getSubcategory() != null ? tx.getSubcategory().getId() : null)
                .amount(tx.getAmount())
                .type(tx.getType())
                .occurredAt(tx.getOccurredAt())
                .description(tx.getDescription())
                .deleted(tx.isDeleted())
                .build();
    }
}
