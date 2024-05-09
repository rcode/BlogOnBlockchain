package com.rcode.pages.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.CSSTransition
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TransitionProperty
import com.varabyte.kobweb.compose.css.Visibility
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxSize
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.visibility
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.forms.Switch
import com.varabyte.kobweb.silk.components.forms.SwitchSize
import com.varabyte.kobweb.silk.components.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import com.rcode.components.AdminPageLayout
import com.rcode.components.SearchBar
import com.rcode.models.ApiListResponse
import com.rcode.models.PostWithoutDetails
import com.rcode.navigation.Screen
import com.rcode.network.deleteSelectedPosts
import com.rcode.network.fetchMyPosts
import com.rcode.network.searchPostsByTitle
import com.rcode.ui.Theme
import com.rcode.util.Constants.FONT_FAMILY
import com.rcode.Constants.POSTS_PER_PAGE
import com.rcode.Constants.QUERY_PARAM
import com.rcode.components.PostsView
import com.rcode.util.Constants.SIDE_PANEL_WIDTH
import com.rcode.util.Id
import com.rcode.util.isUserLoggedIn
import com.rcode.util.noBorder
import com.rcode.util.parseSwitchText
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.w3c.dom.HTMLInputElement

@Page
@Composable
fun MyPostsPage() {
    isUserLoggedIn {
        MyPostsScreen()
    }
}

@Composable
fun MyPostsScreen() {

    val context = rememberPageContext()
    val scope = rememberCoroutineScope()
    val breakpoint = rememberBreakpoint()
    val myPosts = remember { mutableStateListOf<PostWithoutDetails>() }

    var postsToSkip by remember { mutableStateOf(0) }
    var showMoreVisibility by remember { mutableStateOf(false) }
    val selectedPosts = remember { mutableStateListOf<String>() }

    var selectableMode by remember { mutableStateOf(false) }
    var switchText by remember { mutableStateOf("Select") }

    val hasParams = remember(key1 = context.route) { context.route.params.containsKey(QUERY_PARAM) }
    val query = remember(key1 = context.route) {context.route.params[QUERY_PARAM] ?: ""}

    LaunchedEffect(context.route) {
        postsToSkip = 0
        if(hasParams) {
            (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value = query.replace("%20", " ")
            searchPostsByTitle(
                query = query,
                skip = postsToSkip,
                onSuccess = {
                    if(it is ApiListResponse.Success) {
                        myPosts.clear()
                        myPosts.addAll(it.data)
                        postsToSkip += POSTS_PER_PAGE
                        showMoreVisibility = it.data.size >= POSTS_PER_PAGE
                    }
                },
                onError = {
                    println(it)
                }
            )
        } else {
            fetchMyPosts(
                skip = postsToSkip,
                onSuccess = {
                    if (it is ApiListResponse.Success) {
                        myPosts.clear()
                        myPosts.addAll(it.data)
                        postsToSkip += POSTS_PER_PAGE
                        showMoreVisibility = it.data.size >= POSTS_PER_PAGE
                    }
                },
                onError = {
                    println(it)
                }
            )
        }
    }

    AdminPageLayout {
        Column(
            modifier = Modifier
                .margin(topBottom = 50.px)
                .fillMaxSize()
                .padding(left = if (breakpoint > Breakpoint.MD) SIDE_PANEL_WIDTH.px else 0.px),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (breakpoint > Breakpoint.MD) 30.percent
                        else 50.percent
                    )
                    .margin(bottom = 24.px),
                contentAlignment = Alignment.Center
            ) {
                SearchBar(
                    breakpoint = breakpoint,
                    modifier = Modifier
                        .visibility(if(selectableMode) Visibility.Hidden else Visibility.Visible)
                        .transition(CSSTransition(property = TransitionProperty.All, duration = 200.ms)),
                    onEnterClick = {
                        val myPostQuery = (document.getElementById(Id.adminSearchBar) as HTMLInputElement).value
                        if(myPostQuery.isNotEmpty()) {
                            context.router.navigateTo(Screen.AdminMyPosts.searchByTitle(query = myPostQuery))
                        } else {
                            context.router.navigateTo(Screen.AdminMyPosts.route)
                        }
                    },
                    onSearchIconClick = {}
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(
                        if (breakpoint > Breakpoint.MD) 80.percent
                        else 90.percent
                    )
                    .margin(bottom = 24.px),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        modifier = Modifier.margin(right = 8.px),
                        size = SwitchSize.LG,
                        checked = selectableMode,
                        onCheckedChange = {
                            selectableMode = it
                            if (!selectableMode) {
                                switchText = "Select"
                                selectedPosts.clear()
                            } else {
                                switchText = "0 Posts Selected"
                            }
                        }
                    )
                    SpanText(
                        modifier = Modifier.color(if (selectableMode) Colors.Black else Theme.HalfBlack.rgb),
                        text = switchText
                    )
                }
                Button(
                    attrs = Modifier
                        .margin(right = 20.px)
                        .height(54.px)
                        .padding(leftRight = 24.px)
                        .backgroundColor(Theme.Red.rgb)
                        .color(Colors.White)
                        .noBorder()
                        .borderRadius(r = 4.px)
                        .fontFamily(FONT_FAMILY)
                        .fontSize(14.px)
                        .fontWeight(FontWeight.Medium)
                        .visibility(if (selectedPosts.isNotEmpty()) Visibility.Visible else Visibility.Hidden)
                        .onClick {
                            println("Clicked: Delete button")
                            scope.launch {
                                val result = deleteSelectedPosts(ids = selectedPosts)
                                if (result) {
                                    selectableMode = false
                                    switchText = "Select"
                                    postsToSkip -= selectedPosts.size
                                    selectedPosts.forEach { deletedPostId ->
                                        myPosts.removeAll {
                                            it._id == deletedPostId
                                        }
                                    }
                                    selectedPosts.clear()
                                }
                            }
                        }
                        .toAttrs()
                ) {
                    SpanText(text = "Delete")
                }
            }
            PostsView(
                breakpoint = breakpoint,
                posts = myPosts,
                selectableMode = selectableMode,
                onSelect = {
                    selectedPosts.add(it)
                    switchText = parseSwitchText(selectedPosts.toList())
                },
                onDeselect = {
                    selectedPosts.remove(it)
                    switchText = parseSwitchText(selectedPosts.toList())
                },
                showMoreVisibility = showMoreVisibility,
                onShowMore = {
                    scope.launch {
                        if(hasParams) {
                            searchPostsByTitle(
                                query = query,
                                skip = postsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        if (it.data.isNotEmpty()) {
                                            //myPosts.clear()
                                            myPosts.addAll(it.data)
                                            postsToSkip += POSTS_PER_PAGE
                                            if (it.data.size < POSTS_PER_PAGE)
                                                showMoreVisibility = false
                                            //showMoreVisibility = it.data.size >= POSTS_PER_PAGE
                                        } else {
                                            showMoreVisibility = false
                                        }
                                    }
                                },
                                onError = {
                                    println(it)
                                }
                            )
                        } else {
                            fetchMyPosts(
                                skip = postsToSkip,
                                onSuccess = {
                                    if (it is ApiListResponse.Success) {
                                        if (it.data.isNotEmpty()) {
                                            //myPosts.clear()
                                            myPosts.addAll(it.data)
                                            postsToSkip += POSTS_PER_PAGE
                                            if (it.data.size < POSTS_PER_PAGE)
                                                showMoreVisibility = false
                                            //showMoreVisibility = it.data.size >= POSTS_PER_PAGE
                                        } else {
                                            showMoreVisibility = false
                                        }
                                    }
                                },
                                onError = {
                                    println(it)
                                }
                            )
                        }
                    }
                },
                onClick = {
                    context.router.navigateTo(Screen.AdminCreate.passPostId(id = it))
                }
            )
        }
    }
}
