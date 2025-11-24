package com.chatguru.ai.model

data class UserProfile(
    val gender: Gender = Gender.PREFER_NOT_TO_SAY,
    val age: Int = 25,
    val preferredLanguage: Language = Language.ENGLISH
)
