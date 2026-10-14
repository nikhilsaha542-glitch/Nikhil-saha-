package com.example

import com.example.domain.engine.AngleMode
import com.example.domain.engine.CalculationEngine
import com.example.domain.engine.CalculationResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalculationEngineTest {

    private lateinit var engine: CalculationEngine

    @Before
    fun setUp() {
        engine = CalculationEngine()
    }

    @Test
    fun testBasicArithmetic() {
        val res1 = engine.evaluate("12 + 34", AngleMode.DEG)
        assertTrue(res1 is CalculationResult.Success)
        assertEquals("46", (res1 as CalculationResult.Success).formatted)

        val res2 = engine.evaluate("100 − 42", AngleMode.DEG)
        assertTrue(res2 is CalculationResult.Success)
        assertEquals("58", (res2 as CalculationResult.Success).formatted)

        val res3 = engine.evaluate("7 × 8", AngleMode.DEG)
        assertTrue(res3 is CalculationResult.Success)
        assertEquals("56", (res3 as CalculationResult.Success).formatted)

        val res4 = engine.evaluate("84 ÷ 4", AngleMode.DEG)
        assertTrue(res4 is CalculationResult.Success)
        assertEquals("21", (res4 as CalculationResult.Success).formatted)
    }

    @Test
    fun testOrderOfOperations() {
        val res = engine.evaluate("2 + 3 × 4", AngleMode.DEG)
        assertTrue(res is CalculationResult.Success)
        assertEquals("14", (res as CalculationResult.Success).formatted)

        val resParen = engine.evaluate("(2 + 3) × 4", AngleMode.DEG)
        assertTrue(resParen is CalculationResult.Success)
        assertEquals("20", (resParen as CalculationResult.Success).formatted)
    }

    @Test
    fun testDecimalPrecision() {
        // Floating point artifact test
        val res = engine.evaluate("0.1 + 0.2", AngleMode.DEG)
        assertTrue(res is CalculationResult.Success)
        assertEquals("0.3", (res as CalculationResult.Success).formatted)
    }

    @Test
    fun testScientificFunctions() {
        // Trigonometry in DEG
        val sin30 = engine.evaluate("sin(30)", AngleMode.DEG)
        assertTrue(sin30 is CalculationResult.Success)
        assertEquals("0.5", (sin30 as CalculationResult.Success).formatted)

        val cos90 = engine.evaluate("cos(90)", AngleMode.DEG)
        assertTrue(cos90 is CalculationResult.Success)
        assertEquals("0", (cos90 as CalculationResult.Success).formatted)

        // Trigonometry in RAD
        val sinPiOver2 = engine.evaluate("sin(π / 2)", AngleMode.RAD)
        assertTrue(sinPiOver2 is CalculationResult.Success)
        assertEquals("1", (sinPiOver2 as CalculationResult.Success).formatted)

        // Power
        val pow = engine.evaluate("2^8", AngleMode.DEG)
        assertTrue(pow is CalculationResult.Success)
        assertEquals("256", (pow as CalculationResult.Success).formatted)

        // Square root
        val sqrtVal = engine.evaluate("√(144)", AngleMode.DEG)
        assertTrue(sqrtVal is CalculationResult.Success)
        assertEquals("12", (sqrtVal as CalculationResult.Success).formatted)

        // Factorial
        val fact = engine.evaluate("5!", AngleMode.DEG)
        assertTrue(fact is CalculationResult.Success)
        assertEquals("120", (fact as CalculationResult.Success).formatted)

        // Logarithm
        val log100 = engine.evaluate("log(100)", AngleMode.DEG)
        assertTrue(log100 is CalculationResult.Success)
        assertEquals("2", (log100 as CalculationResult.Success).formatted)
    }

    @Test
    fun testImplicitMultiplication() {
        val res1 = engine.evaluate("2(3 + 4)", AngleMode.DEG)
        assertTrue(res1 is CalculationResult.Success)
        assertEquals("14", (res1 as CalculationResult.Success).formatted)

        val res2 = engine.evaluate("5√4", AngleMode.DEG)
        assertTrue(res2 is CalculationResult.Success)
        assertEquals("10", (res2 as CalculationResult.Success).formatted)
    }

    @Test
    fun testDivisionByZero() {
        val res = engine.evaluate("10 ÷ 0", AngleMode.DEG)
        assertTrue(res is CalculationResult.Error)
        assertEquals("Cannot divide by 0", (res as CalculationResult.Error).message)
    }

    @Test
    fun testLivePreview() {
        val preview1 = engine.evaluateLivePreview("25 × 4 + ", AngleMode.DEG)
        assertEquals("100", preview1)

        val preview2 = engine.evaluateLivePreview("sin(30", AngleMode.DEG)
        assertEquals("0.5", preview2)
    }
}
