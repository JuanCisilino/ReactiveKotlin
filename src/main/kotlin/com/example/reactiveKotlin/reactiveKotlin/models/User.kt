package com.example.reactiveKotlin.reactiveKotlin.models

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    val id: Int?=null,
    var firstName: String,
    var lastName: String,
    var email: String
)
