plugins {
    id("io.github.zero88.gradle.oss") version "2.0.0"
    id("io.github.zero88.gradle.root") version "2.0.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    `java-test-fixtures`
}

object Libs {

    const val slf4j = "org.slf4j:slf4j-api:1.7.32"
    const val jetbrainsAnnotation = "org.jetbrains:annotations:23.0.0"
    const val logback = "ch.qos.logback:logback-classic:1.2.10"
    const val jacksonAnnotation = "com.fasterxml.jackson.core:jackson-annotations:2.13.1"
    const val junit4 = "junit:junit:4.13"
    const val junit5Vintage = "org.junit.vintage:junit-vintage-engine:5.7.0"
}

group = "io.github.zero88"

repositories {
    mavenLocal()
    maven { url = uri("https://maven-central-asia.storage-download.googleapis.com/maven2/") }
    jcenter()
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(Libs.slf4j)
    implementation(Libs.jacksonAnnotation)
    implementation(Libs.jetbrainsAnnotation)

    testImplementation(Libs.junit4)
    testImplementation(Libs.junit5Vintage)
    testImplementation(Libs.logback)
}

oss {
    zero88.set(true)
    publishingInfo {
        homepage.set("https://github.com/zero88/java-utils")
        description.set("Java Utils")
        license {
            name.set("The Apache License, Version 2.0")
            url.set("https://github.com/zero88/java-utils/blob/master/LICENSE")
        }
        scm {
            connection.set("scm:git:git://git@github.com:zero88/qwe.git")
            developerConnection.set("scm:git:ssh://git@github.com:zero88/java-utils.git")
            url.set("https://github.com/zero88/java-utils")
        }
    }
}

nexusPublishing {
    packageGroup.set("io.github.zero88")
    repositories {
        sonatype {
            username.set(project.property("nexus.username") as String?)
            password.set(project.property("nexus.password") as String?)
        }
    }
}
