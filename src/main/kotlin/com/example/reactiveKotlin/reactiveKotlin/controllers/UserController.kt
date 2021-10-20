package com.example.reactiveKotlin.reactiveKotlin.controllers

import com.example.reactiveKotlin.reactiveKotlin.models.User
import com.example.reactiveKotlin.reactiveKotlin.repositories.UserRepository
import com.example.reactiveKotlin.reactiveKotlin.requests.UserCreateRequest
import com.example.reactiveKotlin.reactiveKotlin.requests.UserUpdateRequest
import com.example.reactiveKotlin.reactiveKotlin.responses.PagingResponse
import com.example.reactiveKotlin.reactiveKotlin.responses.UserCreateResponse
import com.example.reactiveKotlin.reactiveKotlin.responses.UserUpdateResponse
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrElse
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

    @Operation(operationId = "createUser", summary = "Create user")
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

    @Operation(operationId = "listUsers", summary = "List user")
    @GetMapping("")
    suspend fun listUsers(@RequestParam pageNo: Int, @RequestParam pageSize: Int = 10): PagingResponse<User> {
        val offset = (pageSize * pageNo) - pageSize
        val list = userRepository.findAllUsers(pageSize, offset).collectList().awaitFirst()
        val total = userRepository.count().awaitFirst()
        return PagingResponse(total, list)
    }

    @Operation(operationId = "updateUser", summary = "Update user")
    @PatchMapping("/{userId}")
    suspend fun updateUser(@PathVariable userId: Int,
                           @RequestBody @Valid userUpdateRequest: UserUpdateRequest): UserUpdateResponse {
        val dbUser = userRepository.findById(userId).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$userId doesn't exist")
        }

        val duplicateUser = userRepository.findByEmail(userUpdateRequest.email).awaitFirstOrNull()
        duplicateUser?.let {
            if (it.id != userId)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User #$userId already exist") }

        val updatedUser = try {
            dbUser.email = userUpdateRequest.email
            dbUser.firstName = userUpdateRequest.firstName ?: dbUser.firstName
            dbUser.lastName = userUpdateRequest.lastName ?: dbUser.lastName
            userRepository.save(dbUser).awaitFirst()
        }catch (e: Exception){
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update user", e)
        }

        return UserUpdateResponse(
            id = updatedUser.id,
            firstName = updatedUser.firstName,
            lastName = updatedUser.lastName,
            email = updatedUser.email
        )
    }

    @Operation(operationId = "deleteUser", summary = "Delete user")
    @DeleteMapping("/{userId}")
    suspend fun deleteUser(@PathVariable userId: Int){
        val existingUser = userRepository.findById(userId).awaitFirstOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User #$userId doesn't exist")
        }
        userRepository.delete(existingUser).subscribe()
    }



}
