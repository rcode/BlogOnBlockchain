package com.rcode.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import com.rcode.Constants.POSTS_PER_PAGE
import com.rcode.components.CategoryNavigationItems
import com.rcode.components.OverflowSidePanel
import com.rcode.models.ApiListResponse
import com.rcode.models.PostWithoutDetails
import com.rcode.navigation.Screen
import com.rcode.network.fetchLatestPosts
import com.rcode.network.fetchMainPosts
import com.rcode.network.fetchPopularPosts
import com.rcode.network.fetchSponsoredPosts
import com.rcode.sections.FooterSection
import com.rcode.sections.HeaderSection
import com.rcode.sections.MainSection
import com.rcode.sections.NewsletterSection
import com.rcode.sections.PostsSection
import com.rcode.sections.SponsoredPostsSection
import kotlinx.coroutines.launch

@Page
@Composable
fun HomePage() {
    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    var overflowMenuOpened by remember { mutableStateOf(false) }
    var mainPosts by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Idle) }
    val latestPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val sponsoredPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    val popularPosts = remember { mutableStateListOf<PostWithoutDetails>() }
    var latestPostsToSkip by remember { mutableStateOf(0) }
    var popularPostsToSkip by remember { mutableStateOf(0) }
    var showMoreLatestPosts by remember { mutableStateOf(false) }
    var showMorePopularPosts by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        fetchMainPosts(
            skip = 0,
            onSuccess = {
                mainPosts = it
            },
            onError = {}
        )
        fetchLatestPosts(
            skip = latestPostsToSkip,
            onSuccess = {
                if (it is ApiListResponse.Success) {
                    latestPosts.addAll(it.data)
                    latestPostsToSkip += POSTS_PER_PAGE
                    if (it.data.size >= POSTS_PER_PAGE) showMoreLatestPosts = true
                }
            },
            onError = {}
        )
        fetchSponsoredPosts(
            onSuccess = {
                if (it is ApiListResponse.Success) {
                    sponsoredPosts.addAll(it.data)
                }
            },
            onError = {}
        )
        fetchPopularPosts(
            skip = popularPostsToSkip,
            onSuccess = {
                if (it is ApiListResponse.Success) {
                    popularPosts.addAll(it.data)
                    popularPostsToSkip += POSTS_PER_PAGE
                    if (it.data.size >= POSTS_PER_PAGE) showMorePopularPosts = true
                }
            },
            onError = {}
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (overflowMenuOpened) {
            OverflowSidePanel(
                onMenuClose = { overflowMenuOpened = false },
                content = { CategoryNavigationItems(vertical = true) }
            )
        }
        HeaderSection(
            breakpoint = breakpoint,
            selectedCategory = null,
            onMenuOpen = { overflowMenuOpened = true }
        )
        MainSection(
            breakpoint = breakpoint,
            posts = mainPosts,
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        PostsSection(
            breakpoint = breakpoint,
            posts = latestPosts,
            title = "Latest Posts",
            showMoreVisibility = true,
            onShowMore = {
                scope.launch {
                    fetchLatestPosts(
                        skip = latestPostsToSkip,
                        onSuccess = { response ->
                            if (response is ApiListResponse.Success) {
                                if (response.data.isNotEmpty()) {
                                    if (response.data.size < POSTS_PER_PAGE) showMoreLatestPosts =
                                        false

                                    latestPosts.addAll(response.data)
                                    latestPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMoreLatestPosts = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it)) }
        )
        SponsoredPostsSection(
            breakpoint = breakpoint,
            posts = sponsoredPosts,
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it))}
        )
        PostsSection(
            breakpoint = breakpoint,
            posts = popularPosts,
            title = "Popular Posts",
            showMoreVisibility = true,
            onShowMore = {
                scope.launch {
                    fetchPopularPosts(
                        skip = popularPostsToSkip,
                        onSuccess = { response ->
                            if (response is ApiListResponse.Success) {
                                if (response.data.isNotEmpty()) {
                                    if (response.data.size < POSTS_PER_PAGE) showMorePopularPosts = false

                                    popularPosts.addAll(response.data)
                                    popularPostsToSkip += POSTS_PER_PAGE
                                } else {
                                    showMorePopularPosts = false
                                }
                            }
                        },
                        onError = {}
                    )
                }
            },
            onClick = { context.router.navigateTo(Screen.PostPage.getPost(id = it))}
        )
        NewsletterSection(breakpoint = breakpoint)
        FooterSection()
    }
}
