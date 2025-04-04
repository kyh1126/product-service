import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.spring") version "1.5.30"
    kotlin("plugin.jpa") version "1.5.30"
    kotlin("plugin.allopen") version "1.5.30"
    kotlin("kapt") version "1.5.30"
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

extra["springCloudVersion"] = "2020.0.3"
extra["testcontainersVersion"] = "1.16.2"
extra["springCloudAWSVersion"] = "2.3.3"


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

    //kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

    //HttpClient
    implementation("org.apache.httpcomponents:httpclient")

    // feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("io.github.openfeign:feign-httpclient")

    // @Testcontainers 관련 설정
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:localstack")

    // aws
    implementation("io.awspring.cloud:spring-cloud-aws-messaging")
    implementation("io.awspring.cloud:spring-cloud-aws-autoconfigure")
    implementation("cloud.localstack:localstack-utils:0.2.17")

    // sfn common
    implementation("sfn", "sfn-excel-module", "1.0.106")

    // slack 로깅 설정
    implementation("com.github.maricn:logback-slack-appender:1.4.0")

    // HTML parser
    implementation("org.jsoup:jsoup:1.14.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:${property("springCloudAWSVersion")}")
    }
}

ext["log4j2.version"] = "2.15.0"

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
