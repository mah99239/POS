package com.casecode.pos.core.testing.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CoroutinesTestRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestRule {
    override fun apply(
        base: Statement?,
        description: Description?,
    ): Statement =
        object : Statement() {
            override fun evaluate() {
                beforeEach()
                try {
                    base?.evaluate()
                } finally {
                    afterEach()
                }
            }
        }

    private fun beforeEach() {
        Dispatchers.setMain(dispatcher)
    }

    private fun afterEach() {
        Dispatchers.resetMain()
    }
}