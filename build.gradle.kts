plugins {
    id("java")
}

group = "dev.idank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-sync:${findProperty("mongo.driver.version")}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${findProperty("jackson.version")}")
    implementation("org.jetbrains:annotations:${findProperty("jetbrains.version")}")

    testImplementation(platform("org.junit:junit-bom:${findProperty("junit.version")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}