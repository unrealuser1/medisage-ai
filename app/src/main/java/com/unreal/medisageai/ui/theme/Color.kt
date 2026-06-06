package com.unreal.medisageai.ui.theme

import androidx.compose.ui.graphics.Color

// Legacy Material defaults (kept for the dark scheme fallback / previews).
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ---------------------------------------------------------------------------
// MediSage AI brand palette — a clinical navy-on-light system matching the
// high-fidelity mockups (Screen1..Screen3.1).
// ---------------------------------------------------------------------------

/** Primary brand navy — branding, CTAs, user bubbles, avatar. */
val MediNavy = Color(0xFF13294B)
val MediNavyDark = Color(0xFF0C1E38)
val MediBlueAccent = Color(0xFF1E3A6B)

/** Soft light-blue label used on top of the navy CTAs (matches the mockups' button text). */
val MediOnNavy = Color(0xFFDDE6F5)

/** Page background — the subtle blue-lavender wash behind the auth cards. */
val MediBackground = Color(0xFFF1F4FB)
val MediBackgroundTop = Color(0xFFEAEFF8)

/** Surfaces. */
val MediCard = Color(0xFFFFFFFF)
val MediFieldFill = Color(0xFFF5F6FA)
val MediFieldBorder = Color(0xFFE2E6EF)

/** Text. */
val MediTextPrimary = Color(0xFF0E1B2E)
val MediTextSecondary = Color(0xFF5B6472)

/** Chat bubbles. */
val MediAiBubble = Color(0xFFE3E9F2)
val MediOnAiBubble = Color(0xFF1A2433)
val MediUserBubble = Color(0xFF13294B)
val MediOnUserBubble = Color(0xFFEAF0FA)

/** Critical / abnormal medical values (e.g. "5.8 mmol/L (Critical)"). */
val MediCritical = Color(0xFFD32F2F)

/** Navigation drawer. */
val MediDrawerBg = Color(0xFFEDEFF4)
val MediDrawerSelected = Color(0xFFD8E2F0)

// Coherent on-brand dark scheme (so dark-mode devices don't fall back to purple).
val MediNavyLightPrimary = Color(0xFF8FB0E6)
val MediDarkBackground = Color(0xFF0B1626)
val MediDarkSurface = Color(0xFF101F33)
val MediDarkAiBubble = Color(0xFF1B2A40)
