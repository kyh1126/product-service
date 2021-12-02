import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    kotlin("plugin.jpa") version "1.5.10"
    // Lazy 전략 사용 시 실제 객체 호출 전까지 프록시 객체 참조
    // 프록시 객체는 Entity 클래스 확장
    // JPA final class -> 확장 불가능 -> @ManyToOne Lazy 동작 안함
    // kotlin class -> 기본 final, open 키워드 제공
    // allopen plugin -> open class 로 바꿔줌
    // 그냥 플러그인 안쓰고 클래스별로 open 붙인다고 되는게 아니다.
    // 프로퍼티와 함수에도 open 키워드가 필요하다.
    // https://spring.io/guides/tutorials/spring-boot-kotlin/ 해당 문서에서는 allopen 플로그인 사용 권장하고 있음
    kotlin("plugin.allopen") version "1.5.10"
    kotlin("kapt") version "1.5.10"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

group = "com.smartfoodnet"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven(url = "http://nexus.smartfoodnet.com:8081/repository/sfn-maven/") {
        isAllowInsecureProtocol = true
    }
}
sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}
//extra["testcontainersVersion"] = "1.15.3"
extra["testcontainersVersion"] = "1.16.2"

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("app.jar")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("mysql:mysql-connector-java")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")

    //query dsl
    implementation("com.querydsl:querydsl-jpa")
    kapt("com.querydsl:querydsl-apt:4.4.0:jpa")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.6.5")

    // @Testcontainers 관련 설정
    testImplementation("org.testcontainers:mysql")

    // sfn common
    implementation("sfn", "sfn-excel-module", "1.0.106")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
