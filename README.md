<div align="center">

<img src="https://raw.githubusercontent.com/so1s/.github/main/static/logo.png" alt="So1s Logo" width="50%" />

# So1s Backend

Spring Boot 기반 Kubernetes MLOps 컴포넌트 프로비저닝 / 매니지먼트 API 서버

[프로젝트 소개 페이지로 돌아가기](https://github.com/so1s)

</div>

## 주요 기능

- Springfox 기반 OpenAPI 3.0 Swagger API 문서 제공
- K8s Job 기반 모델 빌드 업로드, 메타데이터 기반 버전 관리 기능 지원
- Container Image 레지스트리 인증 정보 관리 기능 지원
- 인퍼런스 서버 Deployment 배포 및 Endpoint 구성
- Model / Deployment Health Check
- Istio 기반 ABN Test 인프라 구성 / Multiple Endpoints & Dynamic Weights Traffic Splitting 구현
- Cluster 내부의 Node 조회 및 Yaml등의 포맷을 통한 상세 사항 확인
- Inference Server 생성을 위한 Container Resource Template 관리 기능 지원

## 사용 기술

- Java 17
- Gradle 7.4.2
- Spring Boot 2.7.1
- Spring Security
- Spring Data JPA
- QueryDSL
- Postgres / H2 Database
- Springfox 3.0.0 / OpenAPI 3.0
- Fabric8 Kubernetes / Istio Client
- JUnit 5 / Mockito
- Lombok
- JWT
- Github Actions / Github Packages / Jib
