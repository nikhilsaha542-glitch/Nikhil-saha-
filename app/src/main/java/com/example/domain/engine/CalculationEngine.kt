package com.example.domain.engine

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.*

enum class AngleMode {
    DEG, RAD
}

class CalculationEngine {

    companion object {
        const val PI = "π"
        const val E = "e"
        const val SQRT = "√"
        const val MULTIPLY = "×"
        const val DIVIDE = "÷"
        const val MINUS = "−"
        const val PLUS = "+"
        const val POWER = "^"
        const val FACTORIAL = "!"
        const val PERCENT = "%"

        val CONSTANTS = mapOf(
            "π" to Math.PI,
            "e" to Math.E,
            "c" to 299792458.0,          // speed of light (m/s)
            "h" to 6.62607015e-34,      // Planck constant (J*s)
            "G" to 6.67430e-11,         // Gravitational constant (m^3 kg^-1 s^-2)
            "N_A" to 6.02214076e23      // Avogadro constant (mol^-1)
        )
    }

    /**
     * Evaluates a mathematical expression string.
     * Returns a CalculationResult (Success with formatted string & double, or Error with message).
     */
    fun evaluate(expression: String, angleMode: AngleMode): CalculationResult {
        if (expression.isBlank()) return CalculationResult.Empty

        try {
            val sanitized = sanitizeExpression(expression)
            if (sanitized.isBlank()) return CalculationResult.Empty

            val tokens = tokenize(sanitized)
            if (tokens.isEmpty()) return CalculationResult.Empty

            val rpn = shuntingYard(tokens)
            val value = evaluateRpn(rpn, angleMode)

            if (value.isNaN()) {
                return CalculationResult.Error("Undefined")
            }
            if (value.isInfinite()) {
                return CalculationResult.Error("Cannot divide by 0")
            }

            val formatted = formatResult(value)
            return CalculationResult.Success(value, formatted)
        } catch (e: ArithmeticException) {
            return CalculationResult.Error(e.message ?: "Math Error")
        } catch (e: Exception) {
            return CalculationResult.Error("Invalid Expression")
        }
    }

    /**
     * Evaluates live expression for preview without throwing or displaying aggressive errors.
     * If incomplete, attempts to close parentheses or evaluates up to valid prefix.
     */
    fun evaluateLivePreview(expression: String, angleMode: AngleMode): String? {
        if (expression.isBlank()) return null
        val trimmed = expression.trim()

        // If the expression ends with an operator, strip trailing operators for preview
        var candidate = trimmed
        while (candidate.isNotEmpty() && candidate.last() in listOf('+', '−', '-', '×', '*', '÷', '/', '^')) {
            candidate = candidate.dropLast(1).trim()
        }
        if (candidate.isEmpty()) return null

        // Auto-close missing right parentheses for preview
        val openCount = candidate.count { it == '(' }
        val closeCount = candidate.count { it == ')' }
        if (openCount > closeCount) {
            candidate += ")".repeat(openCount - closeCount)
        }

        return when (val res = evaluate(candidate, angleMode)) {
            is CalculationResult.Success -> res.formatted
            else -> null
        }
    }

    private fun sanitizeExpression(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")
    }

    /**
     * Tokenizes mathematical expression, inserting implicit multiplication where appropriate.
     * E.g. "2π" -> ["2", "*", "π"], "3(4)" -> ["3", "*", "(", "4", ")"]
     */
    private fun tokenize(expr: String): List<String> {
        val rawTokens = mutableListOf<String>()
        var i = 0

        while (i < expr.length) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || (expr[i] == '-' && i > 0 && expr[i-1] == 'E'))) {
                        sb.append(expr[i])
                        i++
                    }
                    rawTokens.add(sb.toString())
                    continue
                }
                c in listOf('+', '-', '*', '/', '^', '%', '!', '(', ')') -> {
                    rawTokens.add(c.toString())
                    i++
                }
                c == '√' -> {
                    rawTokens.add("√")
                    i++
                }
                c == 'π' -> {
                    rawTokens.add("π")
                    i++
                }
                // Functions like sin, cos, tan, log, ln, asin, acos, atan, abs
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isLetter() || expr[i] == '_')) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString()
                    rawTokens.add(word)
                    continue
                }
                else -> {
                    i++
                }
            }
        }

        // Handle Unary minus and Implicit multiplication
        val processedTokens = mutableListOf<String>()
        for (idx in rawTokens.indices) {
            val token = rawTokens[idx]
            val prev = rawTokens.getOrNull(idx - 1)

            // Unary minus: if '-' is preceded by nothing or by an operator or '('
            if (token == "-") {
                val isUnary = prev == null || prev in listOf("+", "-", "*", "/", "^", "(")
                if (isUnary) {
                    processedTokens.add("u-")
                    continue
                }
            }

            // Check if implicit multiplication should be inserted between prev and token
            if (prev != null) {
                val prevIsNumOrConst = isNumber(prev) || isConstant(prev) || prev == ")" || prev == "!" || prev == "%"
                val currIsNumOrFunc = isNumber(token) || isConstant(token) || isFunction(token) || token == "(" || token == "√"
                
                // e.g. "2(3)" or "2π" or "(2)(3)" or "2sin(30)" or "5√9"
                if (prevIsNumOrConst && currIsNumOrFunc && token != "!" && token != "%" && prev != "u-") {
                    processedTokens.add("*")
                }
            }

            processedTokens.add(token)
        }

        return processedTokens
    }

    private fun isNumber(s: String): Boolean = s.toDoubleOrNull() != null
    private fun isConstant(s: String): Boolean = s in CONSTANTS.keys
    private fun isFunction(s: String): Boolean = s in listOf(
        "sin", "cos", "tan", "asin", "acos", "atan",
        "log", "ln", "abs", "√"
    )

    private fun precedence(op: String): Int = when (op) {
        "!" -> 5
        "%" -> 5
        "u-" -> 4
        "^" -> 3
        "*", "/" -> 2
        "+", "-" -> 1
        else -> 0
    }

    private fun isRightAssociative(op: String): Boolean = op == "^" || op == "u-"

    /**
     * Converts infix tokens into Reverse Polish Notation (RPN) using Dijkstra's Shunting-Yard algorithm.
     */
    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val opStack = Stack<String>()

        for (token in tokens) {
            when {
                isNumber(token) || isConstant(token) -> {
                    output.add(token)
                }
                isFunction(token) -> {
                    opStack.push(token)
                }
                token == "(" -> {
                    opStack.push(token)
                }
                token == ")" -> {
                    while (opStack.isNotEmpty() && opStack.peek() != "(") {
                        output.add(opStack.pop())
                    }
                    if (opStack.isNotEmpty() && opStack.peek() == "(") {
                        opStack.pop() // discard '('
                    }
                    if (opStack.isNotEmpty() && isFunction(opStack.peek())) {
                        output.add(opStack.pop())
                    }
                }
                token == "!" || token == "%" -> {
                    // Postfix operators have high precedence
                    output.add(token)
                }
                else -> {
                    // Binary or unary operator
                    val currPrec = precedence(token)
                    while (opStack.isNotEmpty()) {
                        val top = opStack.peek()
                        if (top == "(") break

                        val topPrec = precedence(top)
                        if ((isRightAssociative(token) && currPrec < topPrec) ||
                            (!isRightAssociative(token) && currPrec <= topPrec)
                        ) {
                            output.add(opStack.pop())
                        } else {
                            break
                        }
                    }
                    opStack.push(token)
                }
            }
        }

        while (opStack.isNotEmpty()) {
            val top = opStack.pop()
            if (top != "(" && top != ")") {
                output.add(top)
            }
        }

        return output
    }

    /**
     * Evaluates RPN tokens to a Double.
     */
    private fun evaluateRpn(rpn: List<String>, angleMode: AngleMode): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            when {
                isNumber(token) -> stack.push(token.toDouble())
                isConstant(token) -> stack.push(CONSTANTS[token] ?: 0.0)
                token == "u-" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for negation")
                    val a = stack.pop()
                    stack.push(-a)
                }
                token == "!" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for factorial")
                    val a = stack.pop()
                    stack.push(factorial(a))
                }
                token == "%" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for percentage")
                    val a = stack.pop()
                    stack.push(a / 100.0)
                }
                isFunction(token) -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("Missing operand for function $token")
                    val a = stack.pop()
                    stack.push(evalFunction(token, a, angleMode))
                }
                else -> {
                    // Binary operators
                    if (stack.size < 2) throw IllegalArgumentException("Missing operand for operator $token")
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(evalBinary(a, b, token))
                }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression structure")
        }

        return stack.pop()
    }

    private fun evalBinary(a: Double, b: Double, op: String): Double = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> {
            if (b == 0.0) throw ArithmeticException("Cannot divide by 0")
            a / b
        }
        "^" -> a.pow(b)
        else -> throw IllegalArgumentException("Unknown operator $op")
    }

    private fun evalFunction(func: String, arg: Double, angleMode: AngleMode): Double = when (func) {
        "sin" -> {
            val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
            val s = sin(rad)
            if (angleMode == AngleMode.DEG && abs(arg % 180.0) < 1e-9) 0.0 else s
        }
        "cos" -> {
            val rad = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
            val c = cos(rad)
            if (angleMode == AngleMode.DEG && abs((arg - 90.0) % 180.0) < 1e-9) 0.0 else c
        }
        "tan" -> {
            if (angleMode == AngleMode.DEG) {
                val mod = abs(arg % 180.0)
                if (abs(mod - 90.0) < 1e-9) throw ArithmeticException("Undefined")
                val rad = Math.toRadians(arg)
                val t = tan(rad)
                if (abs(arg % 180.0) < 1e-9) 0.0 else t
            } else {
                tan(arg)
            }
        }
        "asin" -> {
            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error")
            val rad = asin(arg)
            if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }
        "acos" -> {
            if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error")
            val rad = acos(arg)
            if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }
        "atan" -> {
            val rad = atan(arg)
            if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
        }
        "log" -> {
            if (arg <= 0.0) throw ArithmeticException("Math Error")
            log10(arg)
        }
        "ln" -> {
            if (arg <= 0.0) throw ArithmeticException("Math Error")
            ln(arg)
        }
        "√" -> {
            if (arg < 0.0) throw ArithmeticException("Math Error")
            sqrt(arg)
        }
        "abs" -> abs(arg)
        else -> throw IllegalArgumentException("Unknown function $func")
    }

    private fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial requires positive integer")
        val intN = n.toLong()
        if (intN > 170) return Double.POSITIVE_INFINITY
        var result = 1.0
        for (i in 2..intN) {
            result *= i.toDouble()
        }
        return result
    }

    /**
     * Formats results cleanly:
     * - Removes unnecessary trailing zeros (e.g., 5.0 -> 5)
     * - Handles scientific notation for very large / small numbers
     * - Limits decimal places to 10 significant digits
     */
    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Undefined"
        if (value.isInfinite()) return if (value > 0) "Infinity" else "-Infinity"
        if (abs(value) < 1e-15) return "0"

        val absVal = abs(value)
        if (absVal >= 1e12 || (absVal < 1e-5 && absVal > 0)) {
            val df = DecimalFormat("0.######E0", DecimalFormatSymbols(Locale.US))
            return df.format(value)
        }

        // Use BigDecimal for high precision rounding to avoid floating point artifact like 0.30000000000000004
        return try {
            val bd = BigDecimal(value, MathContext(12, RoundingMode.HALF_UP))
            val stripped = bd.stripTrailingZeros()
            stripped.toPlainString()
        } catch (e: Exception) {
            val df = DecimalFormat("#.##########", DecimalFormatSymbols(Locale.US))
            df.format(value)
        }
    }
}

sealed class CalculationResult {
    data class Success(val value: Double, val formatted: String) : CalculationResult()
    data class Error(val message: String) : CalculationResult()
    object Empty : CalculationResult()
}
