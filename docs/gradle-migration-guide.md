# Maven → Gradle 마이그레이션 가이드

> 현재 `portfolio-api`는 **Maven** 기반으로 구축되어 있습니다.
> 이 문서는 Gradle로 전환할 경우 발생하는 리스크와 각각의 대응 방법을 정리합니다.

---

## 리스크 수준별 분류

### 🔴 HIGH — 반드시 확인 후 전환 (놓치면 빌드 실패)

#### 1. Lombok 어노테이션 프로세서 설정
Maven과 Gradle은 Lombok 선언 방식이 다릅니다.

**Maven (현재):**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

**Gradle (전환 후 반드시 이렇게 선언해야 함):**
```groovy
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'  // ← 이 줄이 없으면 @Getter/@Setter 컴파일 오류
}
```
`annotationProcessor` 선언을 빠뜨리면 `@Getter`, `@Setter`, `@RequiredArgsConstructor` 가
전부 컴파일 오류로 이어집니다. 가장 빈번한 실수입니다.

#### 2. Spring Boot 플러그인 버전 일치
Maven은 parent POM을 통해 플러그인 버전이 자동 관리됩니다.  
Gradle은 플러그인을 직접 선언해야 하며 Spring Boot 버전과 반드시 맞춰야 합니다.

```groovy
plugins {
    id 'org.springframework.boot' version '3.4.0'         // Spring Boot 버전과 동일
    id 'io.spring.dependency-management' version '1.1.6'  // 의존성 버전 자동 관리
    id 'java'
}
```
버전 불일치 시 의존성 충돌로 `NoSuchMethodError` 런타임 오류가 발생합니다.

#### 3. Docker 빌드 명령어 변경
현재 Dockerfile은 `mvn package`를 사용합니다.
Gradle 전환 시 명령어와 JAR 경로가 달라집니다.

**현재 (Maven):**
```dockerfile
RUN mvn package -DskipTests -q
COPY --from=builder /app/target/portfolio-api-1.0.0.jar app.jar
```

**전환 후 (Gradle):**
```dockerfile
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test
COPY --from=builder /app/build/libs/portfolio-api-1.0.0.jar app.jar
# ↑ 경로가 target/ → build/libs/ 로 바뀜
```
JAR 경로를 수정하지 않으면 Docker 빌드는 성공해도 컨테이너 실행 시 파일을 못 찾아 죽습니다.

---

### 🟡 MEDIUM — 기능에는 영향 없지만 확인 필요

#### 4. 의존성 스코프 표현 방식 차이

| Maven scope | Gradle 등가 표현 |
|---|---|
| `compile` (기본) | `implementation` |
| `runtime` | `runtimeOnly` |
| `test` | `testImplementation` |
| `provided` | `compileOnly` |
| `optional` | `compileOnly` (or `implementation(optional: true)`) |

PostgreSQL 드라이버의 `runtime` 스코프가 `runtimeOnly`로 바뀌지 않으면
컴파일 타임에 드라이버가 노출되어 결합도 문제가 생깁니다. (동작은 하지만 설계상 오염)

#### 5. 첫 빌드 속도 저하
Gradle은 첫 실행 시 Gradle Wrapper를 다운로드합니다.  
CI/CD 파이프라인에서 캐시 설정이 없으면 매 빌드마다 수십 초 추가됩니다.

**대응:** GitHub Actions 기준 캐시 설정
```yaml
- uses: actions/cache@v3
  with:
    path: ~/.gradle/caches
    key: gradle-${{ hashFiles('**/*.gradle*') }}
```

#### 6. IntelliJ IDEA 프로젝트 재임포트 필요
Maven → Gradle 전환 후 IDEA에서 프로젝트를 **닫고 다시 열어야** 합니다.  
기존 `.idea/` 폴더가 Maven 설정을 캐싱하고 있어 혼용 시 빨간 오류 라인이 나타납니다.

---

### 🟢 LOW — 코드 수정 없음, 확인만 하면 됨

#### 7. application.yml — 변경 없음
Spring Boot의 `src/main/resources/application.yml` 경로와 환경변수 주입 방식은
Maven/Gradle 무관하게 동일합니다. 이 파일은 그대로 사용 가능합니다.

#### 8. JPA 어노테이션 — 변경 없음
`@Entity`, `@Transactional`, `@OneToMany` 등 모든 JPA 어노테이션은
Maven/Gradle과 무관하게 동일합니다.

#### 9. Redis 설정 — 변경 없음
`RedisConfig.java`, `SessionManager.java` 코드는 변경이 필요하지 않습니다.

---

## 전환 시 권장 순서

```
1. pom.xml을 Git에 보존한 채로 build.gradle 작성 (병행 테스트)
2. 로컬에서 ./gradlew bootJar 성공 확인
3. Docker 이미지 빌드 테스트 (경로 변경 확인)
4. IDEA에서 프로젝트 재임포트 후 컴파일 오류 없음 확인
5. pom.xml 삭제 후 Gradle 단일화
```

---

## 전환을 서두르지 않아도 되는 이유

현재 Maven 구성은 아래 이유로 안정적입니다:
- Spring Boot 공식 문서의 기본 빌드 도구
- Lombok, JPA, Redis 모두 Maven에서 검증된 설정
- Docker 빌드 명령 단순 (`mvn package`)

Gradle 전환이 필요한 시점은:
- 멀티 모듈 프로젝트로 확장하여 빌드 캐시 성능이 필요할 때
- Kotlin DSL (`build.gradle.kts`) 기반 타입 안전 설정이 필요할 때
- 팀 내 Gradle 표준을 통일해야 하는 조직적 결정이 내려질 때
