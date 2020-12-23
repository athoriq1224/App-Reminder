package com.google.developers.lettervault.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model class holds information about the letter and defines table for the Room
 * database with auto generate primary key.
 */
@Entity(tableName = "letter")
data class Letter(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val content: String,
    val created: Long,
    val expires: Long,
    val opened: Long = 0
)