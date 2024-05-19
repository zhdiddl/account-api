package com.example.account.repository;

import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // DB와 상호작용하는 컴포넌트
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc();

    Integer countByAccountUser(AccountUser accountUser);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByAccountUser(AccountUser accountUser);
}


// AccountRepository: DB에서 Account 엔티티를 관리하는 인터페이스
// JpaRepository: DB에서 CRUD 작업을 하도록 기능을 제공하는 인터페이스
// Optional<Account> findFirstByOrderByIdDesc(): Account 엔티티의 ID를 기준으로
// 내림차순 정렬하여 첫 번째 엔티티를 반환하고 없으면 빈 내용을 반환
// Account: DB 테이블과 매핑되는 엔티티 클래스
// Long: Account 엔티티의 기본키 타입
