package com.rcode.data

import com.rcode.models.Category
import com.rcode.models.Newsletter
import com.rcode.models.Post
import com.rcode.models.PostWithoutDetails
import com.rcode.models.User

interface MongoRepository {

    suspend fun checkUserExistence(user: User): User?
    suspend fun checkUserId(id: String): Boolean

    suspend fun addPost(post: Post): Boolean
    suspend fun updatePost(post: Post): Boolean
    suspend fun readMainPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readMyPosts(skip: Int, author: String): List<PostWithoutDetails>
    suspend fun readLatestPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readPopularPosts(skip: Int): List<PostWithoutDetails>
    suspend fun readSponsoredPosts(): List<PostWithoutDetails>
    suspend fun deleteSelectedPosts(ids: List<String>): Boolean
    suspend fun searchPostsByTittle(query: String, skip: Int): List<PostWithoutDetails>
    suspend fun searchPostsByCategory(category: Category, skip: Int): List<PostWithoutDetails>
    suspend fun readSelectedPost(id: String): Post
    suspend fun subscribe(newsletter: Newsletter): String
}