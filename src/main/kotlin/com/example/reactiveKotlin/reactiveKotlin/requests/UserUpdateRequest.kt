package com.example.reactiveKotlin.reactiveKotlin.requests

import javax.validation.constraints.Email

data class UserUpdateRequest(
    val firstName: String?,
    val lastName: String?,

    @field:Email
    val email: String
)
