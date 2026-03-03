package ru.netology.nmedia_practice.dto

enum class AttachmentType{
    IMAGE
}

data class Attachment(
    val url: String,
    val description: String? = null,
    val type: AttachmentType
)