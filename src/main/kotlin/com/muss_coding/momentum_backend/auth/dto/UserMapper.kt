package com.muss_coding.momentum_backend.auth.dto

import com.muss_coding.momentum_backend.auth.User

fun User.toUserDto(): UserDto {
    return UserDto(
        id = this.id,
        email = this.email,
        name = this.name
    )
}