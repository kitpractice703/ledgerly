# 💰 Ledgerly (개인 맞춤형 스마트 가계부)

> **개발 기간:** 2026.03 ~ 2026.04
>
> **한 줄 소개:** 단순 기록을 넘어 예산 통제 기능을 제공하는 백엔드 중심 가계부 프로젝트

---

## 1. 프로젝트 소개
**Ledgerly**는 사용자가 카테고리별로 월 예산을 설정하고 지출을 관리할 수 있는 가계부 서비스입니다.  
단순한 기능 구현을 넘어, 사용자 간 데이터 격리(IDOR 방어)와 철저한 유효성 검증을 통해 **데이터의 무결성과 보안을 확보**하는 데 집중했습니다.

## 2. 기술 스택 (Tech Stack)

### 🖥 Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3.5.13
- **Security:** Spring Security (인증/인가, BCrypt 암호화)
- **Data:** Spring Data JPA, Hibernate
- **Validation:** Jakarta Bean Validation

### 🎨 Frontend & DB
- **Template Engine:** Thymeleaf
- **Database:** MySQL (운영), H2 (테스트용 인메모리 DB)
- **Deployment:** Cloudtype

---

## 3. 핵심 기능 및 설계 디테일

### ✅ 사용자 데이터 완벽 격리 (보안 강화)
- 모든 거래 내역과 예산의 수정/삭제 로직에서 로그인한 사용자의 ID와 데이터 소유자의 ID를 대조하여 **타인의 데이터 접근을 원천 차단**했습니다.

### ✅ 스마트 예산 관리 시스템
- 카테고리별 예산을 설정하면 지출 합계를 자동으로 계산하여 **예산 초과 여부(Exceeded)**를 대시보드에 직관적으로 표시합니다.

### ✅ 입구 컷 데이터 검증 (Fail-Fast)
- DTO와 `@Valid`, `BindingResult`를 활용하여 띄어쓰기 공백(`" "`) 같은 비정상적인 데이터가 DB에 도달하기 전 컨트롤러 단에서 즉시 차단하도록 설계했습니다.