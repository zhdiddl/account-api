package com.example.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //  JPA 엔티티의 생성 및 수정 시점에 자동으로 타임스탬프를 기록
public class JPAAuditingConfiguration {
}
