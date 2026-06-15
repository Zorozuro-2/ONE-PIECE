package com.example.ui.components

import android.util.Log

object SecurityValidator {
    
    /**
     * Enforces strict input validation and sanitization.
     * Prevents Script Injection, HTML/XSS, and restricts maximum string lengths.
     */
    fun sanitizeInput(input: String, maxLength: Int = 150): String {
        val trimmed = input.trim()
        val bounded = if (trimmed.length > maxLength) {
            Log.w("SECURITY_AUDIT", "Input overflow truncated from ${trimmed.length} to $maxLength characters.")
            trimmed.substring(0, maxLength)
        } else {
            trimmed
        }
        
        // Neutralize scripts and common code injection elements
        val sanitized = bounded
            .replace("<script>", "", ignoreCase = true)
            .replace("</script>", "", ignoreCase = true)
            .replace("javascript:", "", ignoreCase = true)
            .replace("<[^>]*>".toRegex(), "") // Strip generic HTML elements
            .replace("['\";]".toRegex(), "")  // Neutralize potential SQL escape characters
        
        if (sanitized != trimmed) {
            Log.w("SECURITY_AUDIT", "UNUSUAL TRAFFIC PATTERN DETECTED: Script/Markup injection neutralized. Input: '$trimmed' -> Sanitized: '$sanitized'")
        }
        return sanitized
    }

    /**
     * Validates ages, enforcing numeric correctness and protecting against overflows.
     */
    fun sanitizeAge(ageText: String, maxAge: Int = 150): Int {
        val sanitizedText = ageText.trim().filter { it.isDigit() }
        val parsed = sanitizedText.toIntOrNull() ?: 19
        return parsed.coerceIn(1, maxAge)
    }
    
    /**
     * Client-side security logging auditing helper
     */
    fun logSecurityAttempt(action: String, success: Boolean, details: String) {
        val status = if (success) "SUCCESS" else "FAILURE"
        Log.i("SECURITY_AUDIT", "Audit Event: [$action] - Status: $status - Context: $details")
    }
}
