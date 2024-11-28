plugins {
    java
//    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
//    id("org.openapi.generator") version "7.10.0"
    id("maven-publish")
}

group = "com.core"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

//    implementation("org.springframework.boot:spring-boot-starter")
//
//    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    implementation("javax.servlet:javax.servlet-api:4.0.1")
    // https://mvnrepository.com/artifact/javax.validation/validation-api
    implementation("javax.validation:validation-api:2.0.1.Final")

    implementation("javax.annotation:javax.annotation-api:1.3.2")
    // https://mvnrepository.com/artifact/org.openapitools/jackson-databind-nullable
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")



}



//openApiGenerate {
//    generatorName.set("spring")
//    inputSpec.set("$projectDir/src/main/resources/static/openapi.yaml")
//    outputDir.set(layout.buildDirectory.dir("generated/sources").get().asFile.toString())
//    apiPackage.set("org.openapi.example.api")
//    invokerPackage.set("org.openapi.example.invoker")
//    modelPackage.set("org.openapi.example.model")
//    configOptions.set(
//        mapOf(
//            "dateLibrary" to "java21",
//            "interfaceOnly" to "true",
//            "useTags" to "true",
//            "library" to "spring-boot"
//        )
//    )
//}

tasks.named("compileJava") {
//    dependsOn(tasks.named("openApiGenerate"))
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/sources/src/main/java")) // Путь к сгенерированным файлам
        }
    }
}

java {
    withSourcesJar() // Генерация jar с исходниками
}


ext {
    // URL для Release репозитория
    set("repositoryReleaseUrl", "http://89.169.163.3:8081/repository/labaTestRepository/")
    // URL для Snapshot репозитория
    set("repositorySnapshotUrl", "http://89.169.163.3:8081/repository/labaTestRepository_SNAPSHOT/")

    // Логин пользователя, от которого будет производиться загрузка
    set("contributorUserName", "Contributor")
    // Пароль пользователя, от которого будет производиться загрузка
    set("contributorPassword", "Contributor")

    // Группа для артефакта
    set("libraryGroupId", "com.example.nexus")
    // Имя артефакта
    set("libraryArtifact", "common-lib")
    // Текущая версия
    set("libraryVersion", "0.0.1-SNAPSHOT")
}

tasks.register<Jar>("sourceJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    repositories {
        maven {
            val repositoryUrl = if (project.version.toString().endsWith("SNAPSHOT")) {
                findProperty("repositorySnapshotUrl") ?: "http://89.169.163.3:8081/repository/labaTestRepository_SNAPSHOT/"
            } else {
                findProperty("repositoryReleaseUrl") ?: "http://89.169.163.3:8081/repository/labaTestRepository/"
            }
            url = uri(repositoryUrl)
            isAllowInsecureProtocol = repositoryUrl.toString().startsWith("http://")



            credentials {
                username = findProperty("contributorUserName") as String?
                password = findProperty("contributorPassword") as String?
            }
        }
    }

    publications {
        create<MavenPublication>("AndroidLibrary") {
            groupId = findProperty("libraryGroupId") as String
            artifactId = findProperty("libraryArtifact") as String
            version = findProperty("libraryVersion") as String


            artifact(layout. buildDirectory.dir("/libs/${project.name}-${project.version}.jar"))
            artifact(tasks.getByName("sourcesJar"))


            pom {
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    configurations.getByName("runtimeClasspath")
                        .resolvedConfiguration
                        .firstLevelModuleDependencies
                        .forEach { dependency ->
                            val dependencyNode = dependenciesNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", dependency.moduleGroup)
                            dependencyNode.appendNode("artifactId", dependency.moduleName)
                            dependencyNode.appendNode("version", dependency.moduleVersion)

                        }
                }
            }
        }
    }
}


tasks.named("publishAndroidLibraryPublicationToMavenRepository") {
    dependsOn(tasks.named("assembleRelease"))
}

tasks.register("assembleRelease") {
    dependsOn("build")
}

tasks.named("sourcesJar") {
//    mustRunAfter("openApiGenerate") // Задача sourcesJar должна выполниться после openApiGenerate
}




