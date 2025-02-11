package com.waffle22.wafflytime.network.dto

import com.squareup.moshi.Json

/*
    백엔드 api를 참고하셔서
    data class를 생성하시면 됩니다.
 */

data class LoginRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class SignUpRequest(
    @Json(name = "id") val userId: String,
    @Json(name = "password") val userPassword: String
)

data class TokenContainer(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "refreshToken") val refreshToken: String
)