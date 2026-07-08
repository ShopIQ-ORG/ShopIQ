package com.iti.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CollectionTranslationFieldsDto(
    val title: String? = null,
    val body_html: String? = null
)

@Serializable
data class CollectionTranslationDataDto(
    val original: CollectionTranslationFieldsDto? = null,
    val translated: CollectionTranslationFieldsDto? = null
)

@Serializable
data class CollectionTranslationResponseDto(
    val ok: Boolean,
    val data: CollectionTranslationDataDto? = null
)

@Serializable
data class SaveCollectionTranslationRequestDto(
    val locale: String,
    val fields: CollectionTranslationFieldsDto
)
