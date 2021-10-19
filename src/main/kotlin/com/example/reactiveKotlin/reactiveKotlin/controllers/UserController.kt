package com.example.reactiveKotlin.reactiveKotlin.controllers

import com.example.reactiveKotlin.reactiveKotlin.models.User
import com.example.reactiveKotlin.reactiveKotlin.repositories.UserRepository
import com.example.reactiveKotlin.reactiveKotlin.requests.UserCreateRequest
import com.example.reactiveKotlin.reactiveKotlin.responses.PagingResponse
import com.example.reactiveKotlin.reactiveKotlin.responses.UserCreateResponse
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/users", produces = [MediaType.APPLICATION_JSON_VALUE])
class UserController {

    @Autowired
    lateinit var userRepository: UserRepository

    @PostMapping("")
    suspend fun createUser(@RequestBody @Valid request: UserCreateRequest): UserCreateResponse{
        val existingUser = userRepository.findByEmail(request.email).awaitFirstOrNull()
        existingUser?.let { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate user") }

        val user = User(
            id = null,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName
        )

        val createdUser = try {
            userRepository.save(user).awaitFirstOrNull()
        }catch (e: Exception){
            throw throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create user", e)
        }

        val id = createdUser?.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing id from user")

        return UserCreateResponse(
            id = id,
            firstName = createdUser.firstName,
            lastName = createdUser.lastName,
            email = createdUser.email
        )
    }

    @GetMapping("")
    suspend fun listUsers(@RequestParam pageNo: Int, @RequestParam pageSize: Int = 10): PagingResponse<User> {
        val offset = (pageSize * pageNo) - pageSize
        val list = userRepository.findAllUsers(pageSize, offset).collectList().awaitFirst()
        val total = userRepository.count().awaitFirst()
        return PagingResponse(total, list)
    }
}
