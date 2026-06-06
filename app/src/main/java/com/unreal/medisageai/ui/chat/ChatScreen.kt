package com.unreal.medisageai.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unreal.medisageai.data.ChatMessage
import com.unreal.medisageai.data.Sender
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedSessionId by rememberSaveable { mutableStateOf<String?>(null) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ChatHistoryDrawer(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                selectedSessionId = selectedSessionId,
                onNewChat = {
                    viewModel.startNewChat()
                    selectedSessionId = null
                    searchQuery = ""
                    scope.launch { drawerState.close() }
                },
                onSessionClick = { item ->
                    viewModel.loadSession(item.transcript)
                    selectedSessionId = item.id
                    scope.launch { drawerState.close() }
                },
                onSettings = { scope.launch { drawerState.close() } },
            )
        },
    ) {
        ChatContent(
            uiState = uiState,
            onMenuClick = { scope.launch { drawerState.open() } },
            onSend = viewModel::sendMessage,
            snackbarHostState = snackbarHostState,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatContent(
    uiState: ChatUiState,
    onMenuClick: () -> Unit,
    onSend: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("MediSage AI", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Filled.Menu, contentDescription = "Open chat history")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0,0,0,0)//WindowInsets.safeDrawing,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .navigationBarsPadding()
                .consumeWindowInsets(innerPadding),
                //.imePadding(),
        ) {
            MessageList(
                messages = uiState.messages,
                isStreaming = uiState.isStreaming,
                onSuggestionClick = onSend,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
            ChatInputBar(
                enabled = !uiState.isStreaming,
                onSend = onSend,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Navigation drawer (Screen3.1)
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatHistoryDrawer(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSessionId: String?,
    onNewChat: () -> Unit,
    onSessionClick: (ChatHistoryItem) -> Unit,
    onSettings: () -> Unit,
) {
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            mockChatHistory
        } else {
            mockChatHistory.filter { it.title.contains(searchQuery, ignoreCase = true) }
        }
    }

    ModalDrawerSheet(
        drawerShape = RectangleShape,
        drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            // Pinned header: avatar + identity.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "M",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Dr. MediSage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Medical Assistant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // High-contrast "+ New Chat" action row.
            OutlinedButton(
                onClick = onNewChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("New Chat", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(14.dp))

            // Search control.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search history…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
            )

            Spacer(Modifier.height(18.dp))

            // Section label.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.History,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "RECENT HISTORY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.height(8.dp))

            // Scrollable history feed.
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(items = filtered, key = { it.id }) { item ->
                    HistoryRow(
                        item = item,
                        selected = item.id == selectedSessionId,
                        onClick = { onSessionClick(item) },
                    )
                }
            }

            // Base sticky segment.
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSettings() }
                    .padding(vertical = 14.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Settings & Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "V1.0.4",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HistoryRow(
    item: ChatHistoryItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.dateLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Chat feed (Screen3.0)
// ---------------------------------------------------------------------------

@Composable
private fun MessageList(
    messages: List<ChatMessage>,
    isStreaming: Boolean,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, messages.lastOrNull()?.text) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = messages, key = { it.id }) { message ->
            val isTypingPlaceholder = isStreaming &&
                message.sender == Sender.AI &&
                message.text.isEmpty() &&
                message.id == messages.last().id
            MessageBubble(
                message = message,
                isTypingPlaceholder = isTypingPlaceholder,
                onSuggestionClick = onSuggestionClick,
            )
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    isTypingPlaceholder: Boolean,
    onSuggestionClick: (String) -> Unit,
) {
    val isUser = message.sender == Sender.USER
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp),
        ) {
            Surface(color = bubbleColor, shape = shape) {
                if (isTypingPlaceholder) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .size(16.dp),
                        strokeWidth = 2.dp,
                        color = textColor,
                    )
                } else {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = parseMarkdown(message.text, MaterialTheme.colorScheme.error),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (message.suggestions.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            message.suggestions.forEachIndexed { index, suggestion ->
                                if (index > 0) Spacer(Modifier.height(8.dp))
                                SuggestionChipItem(
                                    text = suggestion,
                                    onClick = { onSuggestionClick(suggestion) },
                                )
                            }
                        }
                    }
                }
            }

            if (!isTypingPlaceholder) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message.timeLabel ?: formatTime(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SuggestionChipItem(
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun ChatInputBar(
    enabled: Boolean,
    onSend: (String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    val canSend = enabled && text.isNotBlank()

    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { /* mock: attach */ }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add attachment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about symptoms, labs, dosages…", maxLines = 1) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 5,
                enabled = enabled,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onSend(text)
                            text = ""
                        },
                        enabled = canSend,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send message",
                            tint = if (canSend) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                },
            )
            Spacer(Modifier.width(6.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(48.dp),
            ) {
                IconButton(onClick = { /* mock: voice input */ }) {
                    Icon(
                        Icons.Filled.Mic,
                        contentDescription = "Voice input",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

private fun formatTime(timestamp: Long): String = timeFormatter.format(Date(timestamp))

/**
 * Lightweight inline-markdown renderer for chat bubbles. Supports:
 *  - `**bold**`
 *  - `*italic*` / `_italic_`
 *  - `[[crit]]…[[/crit]]` → bold + [criticalColor] (used for critical lab values)
 *
 * Line-leading `* ` markers are first normalized to `• ` bullets so list items aren't mistaken
 * for italics.
 */
fun parseMarkdown(
    text: String,
    criticalColor: Color = Color(0xFFD32F2F),
): AnnotatedString {
    val normalized = text.replace(Regex("(?m)^[ \\t]*\\*[ \\t]+"), "• ")

    return buildAnnotatedString {
        var i = 0
        while (i < normalized.length) {
            when {
                normalized.startsWith("[[crit]]", i) -> {
                    val end = normalized.indexOf("[[/crit]]", startIndex = i + 8)
                    if (end == -1) {
                        append(normalized[i]); i++
                    } else {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = criticalColor)) {
                            append(normalized.substring(i + 8, end))
                        }
                        i = end + 9
                    }
                }

                normalized.startsWith("**", i) -> {
                    val end = normalized.indexOf("**", startIndex = i + 2)
                    if (end == -1) {
                        append("**"); i += 2
                    } else {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(normalized.substring(i + 2, end))
                        }
                        i = end + 2
                    }
                }

                normalized[i] == '*' -> {
                    val end = normalized.indexOf('*', startIndex = i + 1)
                    if (end == -1) {
                        append('*'); i++
                    } else {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(normalized.substring(i + 1, end))
                        }
                        i = end + 1
                    }
                }

                normalized[i] == '_' -> {
                    val end = normalized.indexOf('_', startIndex = i + 1)
                    if (end == -1) {
                        append('_'); i++
                    } else {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(normalized.substring(i + 1, end))
                        }
                        i = end + 1
                    }
                }

                else -> {
                    append(normalized[i]); i++
                }
            }
        }
    }
}
