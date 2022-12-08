<div align="center">

<img src="https://raw.githubusercontent.com/so1s/.github/main/static/logo.png" alt="So1s Logo" width="50%" />

# So1s Backend

Spring Boot 기반 Kubernetes MLOps 컴포넌트 프로비저닝 / 매니지먼트 API 서버

[프로젝트 소개 페이지로 돌아가기](https://github.com/so1s)

</div>

## 주요 기능

- 모델 빌드 및 버전 관리
- 인퍼런스 서버 Deployment 배포 및 Endpoint 구성
- Model / Deployment Health Check
- Istio 기반 ABN Test 인프라 구성 / Multiple Endpoints & Dynamic Weights Traffic Splitting 구현
- Cluster 내부의 Node 조회 및 Yaml등의 포맷을 통한 상세 사항 확인
- Container CPU / Memory / GPU Resource Request / Limit Template 관리


## 사용 기술

- Java 11 - Corretto JDK
- Spring Boot
- Spring Security
- Spring Data JPA
- QueryDSL
- Fabric8 Kubernetes / Istio Client
- JUnit 5 / Mockito
- Lombok
- JWT
