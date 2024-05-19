package com.example.account.controller;

import com.example.account.domain.Account;
import com.example.account.dto.AccountInfo;
import com.example.account.dto.CreateAccount;
import com.example.account.dto.DeleteAccount;
import com.example.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 계좌 관련 컨트롤러
 * 1. 계좌 생성
 * 2. 계좌 해지
 * 3. 계좌 조회
 */

@RestController // HTTP 요청을 처리하고 JSON 형태로 응답 반환하는 컨트롤러로 지정
@RequiredArgsConstructor // final 필드 포함하는 생성자를 자동 생성
public class AccountController {
    // 자동 생성자가 있어서 아래 내용을 객체를 직접 만들지 않고 사용 가능
    private final AccountService accountService;

    // 계좌 생성
    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request // JSON을 받아 request 객체로 변환 + 유효성 검사
    ) {
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    // 계좌 해지
    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request // JSON을 받아 request 객체로 변환 + 유효성 검사
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    // 계좌 조회
    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId( // 특정 사용자의 모든 계좌 정보 반환
                                                  @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByUserId(userId)
                .stream().map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount( // 특정 계좌의 상세 정보 반환
                               @PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
