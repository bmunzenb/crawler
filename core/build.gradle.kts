plugins {
    id("crawler.kotlin-conventions")
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.okio)
    api(libs.jsoup)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to "${rootProject.name}-${project.name}",
            "Implementation-Version" to project.version,
        )
    }
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bmunzenb/crawler")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "${rootProject.name}-${project.name}"

            pom {
                name = "Crawler Core"
                description = "Simple web crawler"
                url = "https://github.com/bmunzenb/crawler"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }
                scm {
                    url = "https://github.com/bmunzenb/crawler"
                }
            }

            from(components["java"])
        }
    }
}
