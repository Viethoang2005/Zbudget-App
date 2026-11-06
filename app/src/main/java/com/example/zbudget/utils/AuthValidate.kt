package com.example.zbudget.utils

import android.util.Patterns

object AuthValidate {

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() ->
                "Vui lòng nhập địa chỉ email."

            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                "Địa chỉ email không đúng định dạng."

            else -> null
        }
    }

    fun validateLoginPassword(password: String): String? {
        return when {
            password.isBlank() ->
                "Vui lòng nhập mật khẩu."

            else -> null
        }
    }

    fun validateRegisterPassword(password: String): String? {
        return when {
            password.isBlank() ->
                "Vui lòng nhập mật khẩu."

            password.length < 6 ->
                "Mật khẩu phải có ít nhất 6 ký tự."

            else -> null
        }
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            confirmPassword.isBlank() ->
                "Vui lòng xác nhận mật khẩu."

            password != confirmPassword ->
                "Mật khẩu xác nhận không khớp."

            else -> null
        }
    }
}
