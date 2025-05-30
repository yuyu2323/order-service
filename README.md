# 🛒 Order Servicve

이 프로젝트는 상품 주문, 주문 조회, 주문 취소 기능을 제공하는 Spring Boot 기반의 주문 관리 API입니다. 

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
- 주문 취소 API (`DELETE /v1/order/{ordId}/product/{prdId}`)

