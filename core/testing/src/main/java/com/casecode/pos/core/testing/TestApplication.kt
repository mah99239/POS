package com.casecode.pos.core.testing

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltTestApplication

open class TestApplication : Application()

/**
 * A custom runner to set up the instrumented application class for tests.
 */
@CustomTestApplication(TestApplication::class)
class PosTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        name: String?,
        context: Context?,
    ): Application = super.newApplication(cl, HiltTestApplication::class.java.name, context)
}