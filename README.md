# 🛒 Order Servicve

이 프로젝트는 상품 주문, 주문 조회, 주문 취소 기능을 제공하는 Spring Boot 기반의 주문 관리 API입니다. 

## 📁프로젝트구조

src  
├── main  
│ ├── java/com/ssg/order  
│ │ ├── controller # API 엔드포인트 정의  
│ │ ├── controller/dto # Controller 요청/응답 DTO  
│ │ ├── service # 비즈니스 로직 처리  
│ │ ├── service/dto # 서비스 계층 내부 DTO  
│ │ ├── entity # 엔티티/도메인 모델  
│ │ ├── repository # 데이터 접근 (JPA Repository)  
│ │ ├── common/enums # 공통 Enum 정의  
│ │ ├── exception # 커스텀 예외 정의  
│ │ ├── global # 전역 및 공통 설정  
│ │ └── config # 보안 등 설정 클래스  
│ └── resources  
│ ├── application.yml # 환경 설정  
│ └── import.sql # 초기 테스트 데이터  

## 🚀실행 방법

./gradlew clean
./gradlew bootRun

Swagger UI: http://localhost:8080/swagger-ui/index.html  
H2 Console: http://localhost:8080/h2-console (ID: order PW:)

## 🛠 기술 스택

```markdown
- Java 17
- Spring Boot 3.3.1
- Spring Web / Spring Security
- Spring Data JPA
- H2
- Swagger (springdoc-openapi)
- JUnit 5 + Mockito (단위 테스트)
```

## 주요 기능

- 주문 생성 API (`POST /v1/order`)
- 주문 조회 API (`GET /v1/order/{ordId}`)
- 주문 취소 API (`PUT /v1/order/{ordId}/product/{prdId}/cancel`)

