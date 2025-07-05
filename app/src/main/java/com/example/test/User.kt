package com.example.test

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val text: String,
    val price: String,
    val rate: String,
    val startDate: String,
    var hasLike: Boolean,
    val publishDate: String
)