package com.rcode.api

import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.Request
import com.varabyte.kobweb.api.http.Response
import com.varabyte.kobweb.api.http.setBodyText
import com.rcode.Constants.AUTHOR_PARAM
import com.rcode.Constants.CATEGORY_PARAM
import com.rcode.Constants.DEFAULT_CATEGORY
import com.rcode.data.MongoDB
import com.rcode.models.ApiListResponse
import com.rcode.models.ApiResponse
import com.rcode.models.Category
import com.rcode.models.Post
import com.rcode.util.Constants.POST_ID_PARAM
import com.rcode.util.Constants.QUERY_PARAM
import com.rcode.util.Constants.SKIP_PARAM
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.codecs.ObjectIdGenerator

@Api(routeOverride = "addPost")
suspend fun addPost(context: ApiContext) {
    try {
        val post = context.req.getBody<Post?>()
        val newPost = post?.copy(_id = ObjectIdGenerator().generate().toString())
        context.res.setBody(newPost?.let {
            context.data.getValue<MongoDB>().addPost(it)
        })
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "readmyposts")
suspend fun readMyPosts(context: ApiContext) {
    try {
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val author = context.req.params[AUTHOR_PARAM] ?: ""
        val posts = context.data.getValue<MongoDB>().readMyPosts(skip = skip, author = author)
        context.res.setBody(ApiListResponse.Success(posts))
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(e.message.toString())
    }
}

@Api(routeOverride = "readmainposts")
suspend fun readMainPosts(context: ApiContext) {
    try {
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val posts = context.data.getValue<MongoDB>().readMainPosts(skip = skip)
        context.res.setBody(ApiListResponse.Success(posts))
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readlatestposts")
suspend fun readLatestPosts(context: ApiContext) {
    try {
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val posts = context.data.getValue<MongoDB>().readLatestPosts(skip = skip)
        context.res.setBody(ApiListResponse.Success(posts))
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readpopularposts")
suspend fun readPopularPosts(context: ApiContext) {
    try {
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val posts = context.data.getValue<MongoDB>().readPopularPosts(skip = skip)
        context.res.setBody(ApiListResponse.Success(posts))
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readsponsoredposts")
suspend fun readSponsoredPosts(context: ApiContext) {
    try {
        val posts = context.data.getValue<MongoDB>().readSponsoredPosts()
        context.res.setBody(ApiListResponse.Success(posts))
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "updatepost")
suspend fun updatePost(context: ApiContext) {
    try {
        val updatedPost = context.req.getBody<Post>()
        context.res.setBody(
            updatedPost?.let {
                context.data.getValue<MongoDB>().updatePost(it)
            }
        )
    } catch (e: Exception) {
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "deleteselectedposts")
suspend fun deleteSelectedPosts(context: ApiContext) {
    try {
        val ids = context.req.getBody<List<String>?>()
        context.res.setBody(ids?.let {
            context.data.getValue<MongoDB>().deleteSelectedPosts(ids = it)
        })
    } catch (e: Exception) {
        e.message?.let { context.logger.error(it) }
        context.res.setBody(e.message)
    }
}

@Api(routeOverride = "searchposts")
suspend fun searchPostsByTitle(context: ApiContext) {
    try {
        val query = context.req.params[QUERY_PARAM] ?: ""
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val posts = context.data.getValue<MongoDB>().searchPostsByTittle(
            query = query,
            skip = skip
        )
        context.res.setBody(ApiListResponse.Success(data = posts))
    } catch (e: Exception) {
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "searchpostsbycategory")
suspend fun searchPostsByCategory(context: ApiContext) {
    try {
        val category = Category.valueOf(context.req.params[CATEGORY_PARAM] ?: DEFAULT_CATEGORY)
        val skip = context.req.params[SKIP_PARAM]?.toInt() ?: 0
        val posts = context.data.getValue<MongoDB>().searchPostsByCategory(
            category = category,
            skip = skip
        )
        context.res.setBody(ApiListResponse.Success(data = posts))
    } catch (e: Exception) {
        context.res.setBody(ApiListResponse.Error(message = e.message.toString()))
    }
}

@Api(routeOverride = "readselectedpost")
suspend fun readSelectedPost(context: ApiContext) {
    val postId = context.req.params[POST_ID_PARAM]
    if (!postId.isNullOrEmpty()) {
        try {
            val selectedPost = context.data.getValue<MongoDB>().readSelectedPost(id = postId)
            context.logger.info("Selected Post: $selectedPost")
            context.res.setBody(ApiResponse.Success(data = selectedPost))
        } catch (e: Exception) {
            context.res.setBody(ApiResponse.Error(message = e.message.toString()))
        }
    } else {
        context.res.setBody(ApiResponse.Error(message = "Selected Post does not exist."))
    }
}

inline fun <reified T> Response.setBody(data: T) {
    setBodyText(Json.encodeToString(data))
}

inline fun <reified T> Request.getBody(): T? {
    return body?.decodeToString()?.let { Json.decodeFromString(it) }
}