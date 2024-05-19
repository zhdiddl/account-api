package com.example.account.aop;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 메소드에 적용하도록 설정
@Retention(RetentionPolicy.RUNTIME) // 런타임동안 적용하도록 설정
@Documented // 문서화되도록 지정
@Inherited // 클래스 레벨에 적용할 때 하위 클래스도 포함하도록 설정
public @interface AccountLock {
    long tryLockTime() default 5000L;
}
