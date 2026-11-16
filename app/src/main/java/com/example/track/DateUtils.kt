package com.example.track

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun today(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())