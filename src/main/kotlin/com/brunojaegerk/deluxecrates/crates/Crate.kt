package com.brunojaegerk.deluxecrates.crates

data class Crate(
    val id: String,
    val displayName: String,
    val itemData: String? = null,
    val commands: List<String> = emptyList()
)