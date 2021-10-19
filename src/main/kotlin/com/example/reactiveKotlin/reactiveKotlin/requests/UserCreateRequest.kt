package com.example.reactiveKotlin.reactiveKotlin.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

data class UserCreateRequest(
    @field:NotEmpty
    val firstName: String,
    @field:NotEmpty
    val lastName: String,
    @field:Email
    val email: String
)
