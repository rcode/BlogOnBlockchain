package com.rcode.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import com.rcode.data.MongoDB
import com.rcode.models.User
import com.rcode.models.UserWithoutPassword
import com.rcode.util.getHash
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Api(routeOverride = "usercheck")
suspend fun userCheck(context: ApiContext) {
    try {
        // Get user to be queried from request
        val userRequest = context.req.body?.decodeToString()?.let {
            context.logger.debug(it)
            Json.decodeFromString<User>(it)
        }
        // Query user from DB
        val user = userRequest?.let {
            context.logger.debug("Password Hash: " + it.password.getHash())
            context.data.getValue<MongoDB>().checkUserExistence(
                User(username = it.username, password = it.password.getHash())
            )
        }

        context.logger.debug("User: $user")

        // Return response based on the result from DB
        if(user != null) {
            context.logger.debug("User is not null")
            context.res.setBodyText(
                Json.encodeToString(
                    UserWithoutPassword(id = user.id, username = user.username)
                )
            )
        } else {
            context.logger.debug("User is null")
            context.res.setBodyText(Json.encodeToString("User does not exist."))
        }
    } catch(e: Exception) {
        context.logger.debug("Exception: " + e.message.toString())
        context.res.setBodyText(Json.encodeToString(e.message))
    }
}

@Api(routeOverride = "checkuserid")
suspend fun checkUserId(context: ApiContext) {
    try {
        val userId = context.req.body?.decodeToString()?.let { Json.decodeFromString<String>(it) }
        val result = userId?.let {
            context.data.getValue<MongoDB>().checkUserId(it)
        }

        if(result != null) {
            context.res.setBodyText(Json.encodeToString(result))
        } else {
            context.res.setBodyText(Json.encodeToString(false))
        }
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(false))
    }
}