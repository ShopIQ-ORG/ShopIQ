package com.iti.presentation.util

object Stopwords {
    val set = setOf(
        // Arabic stopwords
        "أريد", "عن", "من", "في", "أبحث", "اريد", "ابحث", "هل",
        "عندكم", "عندك", "عاوز", "عايز", "محتاج", "موجود", "يا",
        "ما", "ماذا", "فيه", "ده", "دي", "هو", "هي", "انا",
        // English stopwords
        "i", "want", "search", "looking", "for", "do", "you",
        "have", "need", "please", "the", "a", "an", "is", "are",
        "to", "in", "of", "and", "that", "can", "get", "show",
        "me", "any", "some", "find", "with", "like"
    )
}
