plugins {
    id("java")
    id("maven-publish")
    id("checkstyle")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "ovh.neziw"
version = "1.0.0"

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation")
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

checkstyle {
    toolVersion = "10.23.0"
    maxWarnings = 0
}

repositories {
    gradlePluginPortal()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "neziw-repo"
            url = uri("https://repo.neziw.ovh/releases/")

            credentials {
                val usernameKey = "MVN_USER"
                val passwordKey = "MVN_PASS"
                username = if (env.isPresent(usernameKey)) env.fetch(usernameKey) else System.getenv(usernameKey)
                password = if (env.isPresent(passwordKey)) env.fetch(passwordKey) else System.getenv(passwordKey)
            }
        }
    }
}