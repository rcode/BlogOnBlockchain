package com.rcode

import com.rcode.models.Category

object Constants {
    const val POSTS_PER_PAGE = 4

    const val QUERY_PARAM = "query"
    const val CATEGORY_PARAM = "category"
    const val POST_ID_PARAM = "postId"
    const val SKIP_PARAM = "skip"
    const val AUTHOR_PARAM = "author"
    const val UPDATED_PARAM = "updated"

    val DEFAULT_CATEGORY = Category.Programming.name
}