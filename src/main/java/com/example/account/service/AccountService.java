package com.example.account.service;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.exception.AccountException;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.account.type.AccountStatus.IN_USE;
import static com.example.account.type.ErrorCode.*;

@Service // 비즈니스 로직을 처리하는데 사용하는 클래스로 지정
@RequiredArgsConstructor // final을 포함하는 생성자 자동 생성
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 1) 사용자가 있는지 id로 조회 (없으면 지정한 예외 처리)
     * 2) 계좌 번호 생성 (내림차순 정렬에서 가장 큰 값을 불러와 +1 or 1000000000으로 지정)
     * 3) 계좌 저장 및 반환 (Account 객체를 생성해서 AccountDto 객체로 변환해서 반환)
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> String.valueOf(Integer.parseInt(account.getAccountNumber())) + 1)
                .orElse("1000000000");

        return AccountDto.fromEntity(accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build())
        );
    }

    private AccountUser getAccountUser(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
        return accountUser;
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 10) {
            throw new AccountException(MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id cannot be negative");
        }
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
    }


    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = getAccountUser(userId);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        accountRepository.save(account);

        return AccountDto.fromEntity(account);
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);
        }
        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);
        }
    }

    @Transactional
    public List<AccountDto> getAccountsByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.findByAccountUser(accountUser);

        return accounts.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
}
