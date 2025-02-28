@file:Suppress("UnstableApiUsage")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "POS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:domain")
include(":core:model")
include(":core:common")
include(":core:data")
include(":core:datastore")
include(":core:datastore_proto")
include(":core:datastore-test")
include(":core:testing")
include(":core:designsystem")
include(":core:ui")
include(":core:printer")

include(":feature:login-employee")
include(":feature:stepper")
include(":feature:employee")
include(":feature:sales-report")
include(":feature:item")
include(":feature:profile")
include(":feature:sale")
include(":feature:setting")
include(":feature:signout")
include(":feature:reports")
include(":ui-test-hilt-manifest")