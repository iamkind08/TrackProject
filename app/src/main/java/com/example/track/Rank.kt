package com.example.track

data class Rank(val title: String, val emoji: String, val minPoints: Int)

val RANKS = listOf(
    Rank("Новичок",     "🌱", 0),
    Rank("Любитель",    "🥉", 30),
    Rank("Продвинутый", "🥈", 80),
    Rank("Профи",       "🥇", 150),
    Rank("Мастер",      "👑", 300)
)

fun rankFor(points: Int): Rank =
    RANKS.last { points >= it.minPoints }

fun nextRankFor(points: Int): Rank? =
    RANKS.firstOrNull { points < it.minPoints }