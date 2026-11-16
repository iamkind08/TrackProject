package com.example.track

data class AvatarItem(
    val name: String,
    val minPoints: Int
)

val ALL_AVATARS = listOf(
    AvatarItem("avatar_1", 0),     // 🌱 Новичок
    AvatarItem("avatar_2", 30),    // 🥉 Любитель
    AvatarItem("avatar_3", 30),    // 🥉 Любитель
    AvatarItem("avatar_4", 80),    // 🥈 Продвинутый
    AvatarItem("avatar_5", 150),   // 🥇 Профи
    AvatarItem("avatar_6", 300)    // 👑 Мастер
)