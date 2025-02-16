plugins {
    id("crawler.kotlin-conventions")
    `java-library`
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
