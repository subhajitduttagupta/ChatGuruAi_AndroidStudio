package com.chatguru.ai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.chatguru.ai.model.Gender
import com.chatguru.ai.model.Language
import com.chatguru.ai.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val PROFILE_SETUP_COMPLETED = booleanPreferencesKey("profile_setup_completed")
        val GENDER = stringPreferencesKey("gender")
        val AGE = intPreferencesKey("age")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    val isProfileSetupCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.PROFILE_SETUP_COMPLETED] ?: false
        }

    val userProfile: Flow<UserProfile> = context.dataStore.data
        .map { preferences ->
            UserProfile(
                gender = try {
                    Gender.valueOf(preferences[PreferencesKeys.GENDER] ?: Gender.PREFER_NOT_TO_SAY.name)
                } catch (e: Exception) {
                    Gender.PREFER_NOT_TO_SAY
                },
                age = preferences[PreferencesKeys.AGE] ?: 25,
                preferredLanguage = try {
                    Language.valueOf(preferences[PreferencesKeys.LANGUAGE] ?: Language.ENGLISH.name)
                } catch (e: Exception) {
                    Language.ENGLISH
                }
            )
        }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_SETUP_COMPLETED] = true
            preferences[PreferencesKeys.GENDER] = profile.gender.name
            preferences[PreferencesKeys.AGE] = profile.age
            preferences[PreferencesKeys.LANGUAGE] = profile.preferredLanguage.name
        }
    }

    suspend fun updateLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE] = language.name
        }
    }

    suspend fun resetOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = false
            preferences[PreferencesKeys.PROFILE_SETUP_COMPLETED] = false
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { it.clear() }
    }
}
