﻿# Simple-SNS
Spring의 security, jpa 등 여러 기능들을 연습해보고, redis와 sse를 경험하면서 간단한 sns를 제작해 봅니다.

## 개발 환경
* Intellij IDEA Ultimate 2023.2.2
* Java 21
* Gradle 8.5
* Spring Boot 3.3.0

## 기술 세부 스택
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* MySQL
* JWT
* Lombok
* SSE
* Redis

## 요구 사항
* CRUD가 가능한 Simple-SNS를 만듭니다.
* 유저 부분은 JWT와 Security를 이용하여 만듭니다.
* 게시판이 Like 기능과 댓글 기능을 가지고 있습니다.
* 알람 기능으로 게시판에 Like와 댓글이 달릴 경우 알람이 보입니다.
* 로그인 후 유저 테이블 조회를 줄이기 위해서 Redis를 사용해봅니다.
* 게시글에 알람 기능과 연결된 Like 또는 댓글이 달리고 사용자가 알람 페이지에 있는 경우 조회가 되도록 SSE를 사용해 봅니다.

## Reference
- https://github.com/KimHyoJin/Simple-SNS/tree/sse/front-end (Front-end 사용)
- https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html (Spring Security FormLogin Docs)
- https://github.com/microsoftarchive/redis/releases (Redis Window 실행)

