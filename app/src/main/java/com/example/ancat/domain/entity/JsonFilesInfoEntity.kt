package com.example.ancat.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class JsonFilesInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "file_name")
    val fileName: String,
    @ColumnInfo(name = "file_path")
    val filePath: String
)