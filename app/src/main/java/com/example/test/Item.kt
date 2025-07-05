package com.example.test

data class Item(val title: String,
                val text: String,
                val price: String,
                val rate: String,
                val startDate: String,
                var hasLike: Boolean = false,
                val publishDate: String)