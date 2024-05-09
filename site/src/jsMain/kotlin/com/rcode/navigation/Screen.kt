package com.rcode.navigation

import com.rcode.Constants.CATEGORY_PARAM
import com.rcode.Constants.POST_ID_PARAM
import com.rcode.Constants.QUERY_PARAM
import com.rcode.Constants.UPDATED_PARAM
import com.rcode.models.Category

sealed class Screen(val route: String) {
    object AdminHome : Screen(route = "/admin/")
    object AdminLogin : Screen(route = "/admin/login")
    object AdminCreate: Screen(route = "/admin/create") {
        fun passPostId(id: String) = "$route?${POST_ID_PARAM}=$id"
    }
    object AdminMyPosts: Screen(route = "/admin/myposts") {
        fun searchByTitle(query: String): String {
            return "$route?${QUERY_PARAM}=$query"
        }

    }
    object AdminSuccess: Screen(route = "/admin/success") {
        fun postUpdate() = "$route?$UPDATED_PARAM=true"
    }

    object SearchPage : Screen(route = "/search/query") {
        fun searchByCategory(category: Category) = "$route?${CATEGORY_PARAM}=${category.name}"
        fun searchByTitle(query: String) = "$route?${QUERY_PARAM}=$query"
    }

    object HomePage : Screen(route = "/")

    object PostPage : Screen(route = "/posts/post") {
        fun getPost(id: String) = "$route?${POST_ID_PARAM}=$id"
    }
}
