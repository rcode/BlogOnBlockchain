package com.rcode.network

import com.varabyte.kobweb.browser.api
import com.varabyte.kobweb.compose.http.http
import com.rcode.Constants.AUTHOR_PARAM
import com.rcode.Constants.CATEGORY_PARAM
import com.rcode.Constants.POST_ID_PARAM
import com.rcode.Constants.QUERY_PARAM
import com.rcode.Constants.SKIP_PARAM
import com.rcode.models.ApiListResponse
import com.rcode.models.ApiResponse
import com.rcode.models.Category
import com.rcode.models.Newsletter
import com.rcode.models.Post
import com.rcode.models.RandomJoke
import com.rcode.models.User
import com.rcode.models.UserWithoutPassword
import com.rcode.util.Constants
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.js.Date

suspend fun checkUserExistence(user: User): UserWithoutPassword? {
    return try {
        val result = window.api.tryPost(
            apiPath = "usercheck",
            body = Json.encodeToString(user).encodeToByteArray()
        )
        result?.decodeToString()?.let { Json.decodeFromString<UserWithoutPassword>(it) }
    } catch (e: Exception) {
        println(e.message)
        null
    }
}

suspend fun checkUserId(id: String): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "checkuserid",
            body = Json.encodeToString(id).encodeToByteArray()
        )?.decodeToString()?.parseData<Boolean>() ?: false
    } catch (e: Exception) {
        println(e.message.toString())
        false
    }
}

suspend fun fetchRandomJokes(onComplete: (RandomJoke) -> Unit) {
    val date = localStorage["date"]
    if (date != null) {
        val difference = (Date.now() - date.toDouble())
        val dayHasPassed = difference >= 86400000
        if (dayHasPassed) {
            try {
                val result = window.http.get(Constants.HUMOR_API_URL).decodeToString()
                println(result)
                onComplete(result.parseData())
                localStorage["date"] = Date.now().toString()
                localStorage["joke"] = result
            } catch (e: Exception) {
                onComplete(RandomJoke(id = -1, joke = e.message.toString()))
                println(e.message)
            }

        } else {
            try {
                localStorage["joke"]?.parseData<RandomJoke>()?.let { onComplete(it) }
            } catch (e: Exception) {
                onComplete(RandomJoke(id = -1, joke = e.message.toString()))
                println(e.message)
            }
        }
    } else {
        try {
            val result = window.http.get(Constants.HUMOR_API_URL).decodeToString()
            println(result)
            onComplete(result.parseData())
            localStorage["date"] = Date.now().toString()
            localStorage["joke"] = result
        } catch (e: Exception) {
            onComplete(RandomJoke(id = -1, joke = e.message.toString()))
            println(e.message)
        }
    }
}

suspend fun addPost(post: Post): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "addPost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    } catch (e: Exception) {
        println(e.message.toString())
        false
    }
}

suspend fun fetchMyPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readmyposts?$SKIP_PARAM=$skip&$AUTHOR_PARAM=${localStorage["username"]}",
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchMainPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readmainposts?$SKIP_PARAM=$skip",
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchLatestPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readlatestposts?$SKIP_PARAM=$skip",
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchPopularPosts(
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readpopularposts?$SKIP_PARAM=$skip",
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun fetchSponsoredPosts(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "readsponsoredposts",
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e.message)
        onError(e)
    }
}

suspend fun updatePost(post: Post): Boolean {
    return try {
        window.api.tryPost(
            apiPath = "updatepost",
            body = Json.encodeToString(post).encodeToByteArray()
        )?.decodeToString().toBoolean()
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun deleteSelectedPosts(ids: List<String>): Boolean {
    return try {
        val result = window.api.tryPost(
            apiPath = "deleteselectedposts",
            body = Json.encodeToString(ids).encodeToByteArray()
        )?.decodeToString()
        result.toBoolean()
    } catch (e: Exception) {
        println(e.message)
        false
    }
}

suspend fun searchPostsByTitle(
    query: String,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "searchposts?${QUERY_PARAM}=$query&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e.message)
        onError(e)
    }
}

suspend fun searchPostsByCategory(
    category: Category,
    skip: Int,
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(
            apiPath = "searchpostsbycategory?${CATEGORY_PARAM}=${category.name}&${SKIP_PARAM}=$skip"
        )?.decodeToString()
        onSuccess(result.parseData())
    } catch (e: Exception) {
        println(e.message)
        onError(e)
    }
}


suspend fun fetchSelectedPost(id: String): ApiResponse {
    return try {
        val result = window.api.tryGet(
            apiPath = "readselectedpost?${POST_ID_PARAM}=$id"
        )?.decodeToString()
        result?.parseData() ?: ApiResponse.Error(message = "Result is null")
    } catch (e: Exception) {
        println(e)
        ApiResponse.Error(message = e.message.toString())
    }
}

suspend fun subscribeToNewsletter(newsletter: Newsletter): String {

    return try {
        val result = window.api.tryPost(
            apiPath = "subscribe-newsletter",
            body = Json.encodeToString(newsletter).encodeToByteArray()
        )?.decodeToString()
        result.toString().replace("\"", "")
    } catch (e: Exception) {
        println(e.message)
        e.message.toString()
    }
}

inline fun <reified T> String?.parseData(): T {
    return Json.decodeFromString(this.toString())
}