package com.rcode.pages.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import com.rcode.Constants.CATEGORY_PARAM
import com.rcode.Constants.POSTS_PER_PAGE
import com.rcode.Constants.QUERY_PARAM
import com.rcode.components.CategoryNavigationItems
import com.rcode.components.LoadingIndicator
import com.rcode.components.OverflowSidePanel
import com.rcode.models.ApiListResponse
import com.rcode.models.Category
import com.rcode.models.PostWithoutDetails
import com.rcode.navigation.Screen
import com.rcode.network.searchPostsByCategory
import com.rcode.network.searchPostsByTitle
import com.rcode.sections.FooterSection
import com.rcode.sections.HeaderSection
import com.rcode.sections.PostsSection
import com.rcode.util.Constants.FONT_FAMILY
import com.rcode.util.Id
import com.rcode.util.Res
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.w3c.dom.HTMLInputElement

@Page(routeOverride = "query")
@Composable
fun SearchPage() {
    val breakpoint = rememberBreakpoint()
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()

    var apiResponse by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    var overflowOpened by remember { mutableStateOf(false) }
    val searchedPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    var postsToSkip by remember { mutableStateOf(0) }
    var showMorePosts by remember { mutableStateOf(false) }

    val hasCategoryParam = remember(key1 = context.route) {
        context.route.params.containsKey(CATEGORY_PARAM)
    }
    val hasQueryParam = remember(key1 = context.route) {
        context.route.params.containsKey(QUERY_PARAM)
    }
    val value = remember(key1 = context.route) {
        if (hasCategoryParam) {
            context.route.params.getValue(CATEGORY_PARAM)
        } else if (hasQueryParam) {
            context.route.params.getValue(QUERY_PARAM)
        } else {
            ""
        }
    }

    LaunchedEffect(key1 = context.route) {
        (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value = ""
        showMorePosts = false
        postsToSkip = 0
        if (hasCategoryParam) {
            searchPostsByCategory(
                category = runCatching { com.rcode.models.Category.valueOf(value) }
                    .getOrElse { com.rcode.models.Category.Programming },
                skip = postsToSkip,
                onSuccess = { response ->
                    apiResponse = response
                    if (response is ApiListResponse.Success) {
                        searchedPosts.clear()
                        searchedPosts.addAll(response.data)
                        postsToSkip += POSTS_PER_PAGE
                        if (response.data.size >= POSTS_PER_PAGE) showMorePosts = true
                    }
                },
                onError = {
                    println(it.message)
                }
            )
        } else if (hasQueryParam) {
            (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value = value
            searchPostsByTitle(
                query = value,
                skip = postsToSkip,
                onSuccess = { response ->
                    apiResponse = response
                    if (response is ApiListResponse.Success) {
                        searchedPosts.clear()
                        searchedPosts.addAll(response.data)
                        postsToSkip += POSTS_PER_PAGE
                        if (response.data.size >= POSTS_PER_PAGE) showMorePosts = true
                    }
                },
                onError = {
                    println(it.message)
                }
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (overflowOpened) {
            OverflowSidePanel(
                onMenuClose = { overflowOpened = false },
                content = {
                    CategoryNavigationItems(
                        selectedCategory = if (hasCategoryParam) runCatching {
                            com.rcode.models.Category.valueOf(value)
                        }.getOrElse { com.rcode.models.Category.Programming } else null,
                        vertical = true
                    )
                }
            )
        }
        HeaderSection(
            breakpoint = breakpoint,
            selectedCategory = if (hasCategoryParam) runCatching {
                com.rcode.models.Category.valueOf(value)
            }.getOrElse { com.rcode.models.Category.Programming } else null,
            logo = Res.Image.logo,
            onMenuOpen = { overflowOpened = true }
        )
        if (apiResponse is ApiListResponse.Success) {
            if (hasCategoryParam) {
                SpanText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .textAlign(TextAlign.Center)
                        .margin(top = 100.px, bottom = 40.px)
                        .fontFamily(FONT_FAMILY)
                        .fontSize(36.px),
                    text = value.ifEmpty { com.rcode.models.Category.Programming.name }
                )
            }
            if (searchedPosts.isNotEmpty()) {
                PostsSection(
                    breakpoint = breakpoint,
                    posts = searchedPosts,
                    showMoreVisibility = showMorePosts,
                    onShowMore = {
                        scope.launch {
                            if (hasCategoryParam) {
                                searchPostsByCategory(
                                    category = runCatching { com.rcode.models.Category.valueOf(value) }
                                        .getOrElse { Category.Programming },
                                    skip = postsToSkip,
                                    onSuccess = { response ->
                                        if (response is ApiListResponse.Success) {
                                            if (response.data.isNotEmpty()) {
                                                if (response.data.size < POSTS_PER_PAGE) {
                                                    showMorePosts = false
                                                }
                                                searchedPosts.addAll(response.data)
                                                postsToSkip += POSTS_PER_PAGE
                                            } else {
                                                showMorePosts = false
                                            }
                                        }
                                    },
                                    onError = {}
                                )
                            } else if (hasQueryParam) {
                                searchPostsByTitle(
                                    query = value,
                                    skip = postsToSkip,
                                    onSuccess = { response ->
                                        if (response is ApiListResponse.Success) {
                                            if (response.data.isNotEmpty()) {
                                                if (response.data.size < POSTS_PER_PAGE) {
                                                    showMorePosts = false
                                                }
                                                searchedPosts.addAll(response.data)
                                                postsToSkip += POSTS_PER_PAGE
                                            } else {
                                                showMorePosts = false
                                            }
                                        }
                                    },
                                    onError = {}
                                )
                            }
                        }
                    },
                    onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
                )
            } else {
                Box(
                    modifier = Modifier.height(100.vh),
                    contentAlignment = Alignment.Center
                ) {
                    SpanText(
                        modifier = Modifier
                            .fontFamily(FONT_FAMILY)
                            .fontSize(16.px)
                            .fontWeight(FontWeight.Medium),
                        text = "Post not found."
                    )
                }
            }
        } else {
            LoadingIndicator()
        }
        FooterSection()
    }
}