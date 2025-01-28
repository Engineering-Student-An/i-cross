# README

---

## 프로젝트 소개

---

![id.png](https://mosoobucket.s3.ap-northeast-2.amazonaws.com/icross-logo.png)

### [I-Cross](http://ec2-13-209-198-107.ap-northeast-2.compute.amazonaws.com:8082)

I-Cross 는 기존 학습 관리 시스템인 [I-Class](https://learn.inha.ac.kr/) 와 관련한 기능 및 여러 부가 기능을 더해 학교 생활에 도움을 주는 사이트입니다.

카카오톡, I-Class 계정을 통해 회원가입을 진행합니다.

I-Class를 통해 크롤링 해 온 정보들을 토대로 ChatGPT가 오늘 하루의 스케줄을 추천하고 미완료 과제, 웹강 등에 대해서 잊지 않도록 이메일로 알림을 전송합니다. 또한 강의노트 업로드를 통해 예상 문제를 받아 볼 수 있습니다.

## 개발 기간

---

2024. 5. 13 ~ 2024. 6. 18

## 기술 스택

---

### Back-end

- Java 21 : 최신 기능과 성능 개선이 포함된 Java 버전.
- Spring Boot 3.2.5 : 생산성을 극대화하고 복잡한 설정을 최소화하기 위해 채택된 프레임워크, 신속한 개발과 배포 지원함.
- Spring Security 3.2.5 : 애플리케이션 보안을 강화하고 사용자 인증 및 권한 관리를 효율적으로 처리하기 위해 채택.
- JPA 3.2.5 : 객체와 관계형 데이터베이스 간의 매핑을 간편하게 처리하여 데이터 관리의 효율성을 높이기 위해 채택.
- MySQL 8.0.39 : 안정적이고 강력한 관계형 데이터베이스, 다양한 데이터 저장 요구를 충족하기 위해 채택.
- Jsoup 1.17.2 : 웹 페이지에서 데이터를 추출하고 HTML을 파싱하기 위해 사용되는 라이브러리, 외부 데이터 수집을 용이하게 하기 위해 채택.
- Apache HttpClient 4.5.13 : RESTful API와의 통신을 쉽게 할 수 있도록 도와주는 라이브러리, 외부 서비스와의 상호작용을 원활하게 하기 위해 채택.
- Apache PDFBox 2.0.24: PDF 파일 처리를 위한 라이브러리로, PDF 의 내용을 읽어 오기 위해 채택.
- Jakarta Mail 2.0.3 : 이메일 기능을 구현하기 위한 라이브러리, 사용자에게 알림 기능을 제공하기 위해 채택.

### Front-end

- HTML, CSS, JavaScript : 웹 개발의 기본 기술, 사용자 인터페이스 구축하기 위해 필수적.
- ThymeLeaf : 서버 사이드 템플릿 엔진, Spring Boot와의 통합이 간편하며 동적 웹 페이지 생성을 용이하게 하기 위해 채택.
- JQuery 3.5.1 : DOM 조작 및 AJAX 요청을 간편하게 처리하기 위한 JavaScript 라이브러리. 사용자 경험을 향상시키기 위해 채택.

### 서비스 배포 환경

- 프로젝트 배포
    - AWS EC2 : 클라우드 기반의 가상 서버, 확장성과 유연성을 제공하여 안정적인 애플리케이션 호스팅을 가능하게 함.
        - 운영체제 : Ubuntu 22.04 LTS
    - AWS RDS : 관리형 데이터베이스 서비스, 데이터베이스 관리의 복잡성을 줄이고 안정성을 높이기 위해 채택.
        - 데이터베이스 : MySQL 8.0.39
- CI/CD
    - GitHub Actions : 코드 변경 시 자동으로 빌드, 테스트, 배포를 수행하여 개발 프로세스를 간소화하고 효율성을 높이기 위해 채택.

### 버전 및 이슈관리

- Git, GitHub, Docker Hub

### 기타

- DevTools 3.2.5 : 개발 중 자동 리로드 및 디버깅 기능을 제공, 생산성을 향상시키기 위해 채택.
- Lombok 1.18.32 : 코드의 가독성을 높이고 유지보수를 용이하게 하기 위해 보일러플레이트 코드를 줄여주는 라이브러리.

## 주요 기능

---

### 회원

- OAuth2.0 활용 카카오톡 로그인을 통해 회원가입 및 로그인 진행합니다.
- 이메일과 I-Class 계정 정보 인증 후 알림 스타일과 알림 주기를 설정해 서비스를 실행합니다.

### 스케줄

- I-Class 사이트에 Curl 명령을 전송하여 로그인 후 강의 정보를 크롤링합니다.
- 해당 정보 (강의, 남은 과제, 웹강 등)들을 토대로 Open AI API를 호출해 Chat GPT가 추천하는 오늘의 스케줄을 홈 화면에서 확인 가능합니다.
- 스케줄은 수정, 삭제가 가능하며 카카오톡 나에게 보내기 기능이 가능합니다.

### 예상 시험 문제

- 업로드한 PDF 형식의 강의 자료를 토대로 Chat GPT가 예상 시험 문제를 생성합니다.
- 문제 유형과 개수에 따라 생성되며, 하단의 정답 확인 버튼을 통해 정답 확인 가능합니다.

### 알림

- Chat GPT가 설정한 알림 스타일을 토대로 알림 메일을 생성하고 Spring Boot의 스케줄링 기능을 통해 알림 주기 대로 메일을 전송합니다.

## 상세 기능

---

[기능 명세서](https://github.com/Engineering-Student-An/i-cross/wiki/%EA%B8%B0%EB%8A%A5-%EB%AA%85%EC%84%B8%EC%84%9C)

## ERD

---

![image.png](https://mosoobucket.s3.ap-northeast-2.amazonaws.com/icross-erd.png)

## 프로젝트 구조

---

- 프로젝트 구조

    ```bash
    src
    ├── main
    │   └── java
    │       └── cross
    │           └── icross
    │               ├── config
    │               ├── controller
    │               │   └── api
    │               ├── domain
    │               │   └── dto
    │               ├── exception
    │               ├── repository
    │               └── service
    └── resources
        ├── static
        │   ├── css
        │   ├── images
        │   └── js
        └── templates
            ├── emailForm
            ├── error
            ├── fragments
            ├── home
            ├── period
            ├── quiz
            ├── student
            └── style
    ```