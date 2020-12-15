import org.gradle.internal.jvm.Jvm
import org.gradle.util.GradleVersion
import java.time.Instant
import java.util.jar.Attributes.Name

val versions = mapOf("slf4j" to "1.7.30", "logback" to "1.2.3", "jackson" to "2.12.0", "classgraph" to "4.8.87",
                     "lombok" to "1.18.16", "junit" to "4.13", "sonarqube" to "3.0")

plugins {
    `java-library`
    `maven-publish`
    jacoco
    signing
    id("org.sonarqube") version "3.0"
    id("io.codearte.nexus-staging") version "0.22.0"
}
val version: String by project
val semanticVersion: String by project

project.group = "io.github.zero88"
project.version = "$version$semanticVersion"

repositories {
    mavenLocal()
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    mavenCentral()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("org.slf4j:slf4j-api:${versions["slf4j"]}")
    api("io.github.classgraph:classgraph:${versions["classgraph"]}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${versions["jackson"]}")
    compileOnly("org.projectlombok:lombok:${versions["lombok"]}")
    annotationProcessor("org.projectlombok:lombok:${versions["lombok"]}")

    testImplementation("junit:junit:${versions["junit"]}")
    testImplementation("ch.qos.logback:logback-classic:${versions["logback"]}")
    testCompileOnly("org.projectlombok:lombok:${versions["lombok"]}")
    testAnnotationProcessor("org.projectlombok:lombok:${versions["lombok"]}")
}

tasks.jacocoTestReport {
    dependsOn("test")
    reports {
        xml.isEnabled = true
        xml.destination = file("${buildDir}/reports/jacoco/report.xml")
        html.isEnabled = true
    }
}


project.tasks["sonarqube"].group = "analysis"
project.tasks["sonarqube"].dependsOn("build", "jacocoTestReport")
sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.exclusions", "**/SystemHelper.java")
    }
}

tasks {
    jar {
        manifest {
            attributes(
                mapOf(Name.IMPLEMENTATION_TITLE.toString() to project.name,
                      Name.IMPLEMENTATION_VERSION.toString() to project.version,
                      "Created-By" to GradleVersion.current(),
                      "Build-Jdk" to Jvm.current(),
                      "Build-By" to project.property("buildBy"),
                      "Build-Hash" to project.property("buildHash"),
                      "Build-Date" to Instant.now())
            )
        }
    }
}

publishing {
    publications {
        repositories {
            create<MavenPublication>("maven") {
                groupId = project.group as String?
                artifactId = project.name
                version = project.version as String?
                from(components["java"])

                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
                pom {
                    name.set(project.name)
                    description.set("Java Utilities")
                    url.set("https://github.com/zero88/java-utils")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://github.com/zero88/java-utils/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("zero88")
                            email.set("sontt246@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://git@github.com:zero88/java-utils.git")
                        developerConnection.set("scm:git:ssh://git@github.com:zero88/java-utils.git")
                        url.set("https://github.com/zero88/java-utils")
                    }
                }
            }
            maven {
                val path = if (project.hasProperty("github")) {
                    "${project.property("github.nexus.url")}/${project.property("nexus.username")}/${project.name}"
                } else {
                    val releasesRepoUrl = project.property("ossrh.release.url")
                    val snapshotsRepoUrl = project.property("ossrh.snapshot.url")
                    if (project.hasProperty("release")) releasesRepoUrl else snapshotsRepoUrl
                }
                url = path?.let { uri(it) }!!
                credentials {
                    username = project.property("nexus.username") as String?
                    password = project.property("nexus.password") as String?
                }
            }
        }
    }
}

tasks.withType<Sign>().configureEach {
    onlyIf { project.hasProperty("release") }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

nexusStaging {
    packageGroup = project.group as String?
    username = project.property("nexus.username") as String?
    password = project.property("nexus.password") as String?
}