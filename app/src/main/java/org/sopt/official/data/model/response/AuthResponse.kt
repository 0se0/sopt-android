/*
 * MIT License
 * Copyright 2023 SOPT - Shout Our Passion Together
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.sopt.official.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.sopt.official.auth.data.remote.model.response.OAuthToken
import org.sopt.official.domain.entity.auth.Auth
import org.sopt.official.domain.entity.auth.Token
import org.sopt.official.domain.entity.auth.UserStatus

@Serializable
data class AuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("playgroundToken") val playgroundToken: String,
    @SerialName("status") val status: String
) {
    fun toEntity() = Auth(
        Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            playgroundToken = playgroundToken
        ),
        UserStatus.of(status)
    )

    fun toOAuthToken() = OAuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
        playgroundToken = playgroundToken,
        status = status
    )
}
