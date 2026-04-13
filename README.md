# 💰 Ledgerly (개인 맞춤형 스마트 가계부)

> **개발 기간:** 2026.03 ~ 
> **한 줄 소개:** 단순 기록을 넘어 예산 통제 기능을 제공하는 백엔드 중심 가계부 프로젝트

---

## 1. 프로젝트 소개
**Ledgerly**는 사용자가 카테고리별로 월 예산을 설정하고 지출을 관리할 수 있는 **스프링 부트 기반 가계부 서비스**입니다.  
단순한 데이터 저장을 넘어, 사용자 간 데이터 격리와 철저한 유효성 검증(Validation)을 통한 데이터 무결성 확보에 집중했습니다.

## 2. 기술 스택 (Tech Stack)
### 🖥 Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3.5.13
- **Security:** Spring Security (인증/인가, BCrypt 암호화)
- **Data:** Spring Data JPA, Hibernate
- **Validation:** Jakarta Bean Validation (DTO 검증)

### 🎨 Frontend
- **Template Engine:** Thymeleaf
- **Styling:** CSS3

### 💾 Database & Infra
- **DB:** MySQL (운영), H2 (테스트용 인메모리 DB)
- **Deployment:** Cloudtype

---

## 3. 핵심 기능 및 설계 디테일
### ✅ 데이터 보안 및 무결성
- **IDOR 보안 취약점 방어:** 모든 내역 수정/삭제 시 로그인한 사용자의 ID와 데이터 소유자의 ID를 대조하여 타인의 데이터 접근을 원천 차단했습니다.
- **Fail-Fast 검증:** DTO와 `@Valid`를 활용하여 컨트롤러 입구에서 부적절한 데이터(공백, 이메일 형식 오류 등)를 즉시 차단합니다.

### ✅ 스마트 예산 관리
- **예산 초과 알림:** 카테고리별 예산 대비 현재 지출 합계를 계산하여, 초과 시 대시보드에서 직관적인 경고 아이콘을 표시합니다.
- **유연한 카테고리:** 수입과 지출 유형을 구분하여 사용자가 직접 카테고리를 관리할 수 있습니다.

---

## 4. 데이터베이스 구조 (ERD)
```mermaid
erDiagram
    USER ||--o{ TRANSACTION : "작성"
    USER ||--o{ BUDGET : "설정"
    CATEGORY ||--o{ TRANSACTION : "분류"
    CATEGORY ||--o{ BUDGET : "목표"

    USER {
        Long id PK
        String email
        String password
        String username
    }
    CATEGORY {
        Long id PK
        String name
        String type
    }
    TRANSACTION {
        Long id PK
        Integer amount
        String description
        LocalDate transactionDate
    }
    BUDGET {
        Long id PK
        Integer limitAmount
        Integer year
        Integer month
    }

