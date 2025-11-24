package com.chatguru.ai.di

import android.content.Context
import androidx.room.Room
import com.chatguru.ai.data.local.ChatGuruDatabase
import com.chatguru.ai.data.local.UserPreferencesManager
import com.chatguru.ai.data.local.dao.ChatDao
import com.chatguru.ai.data.local.dao.MessageDao
import com.chatguru.ai.data.remote.ChatApiService
import com.chatguru.ai.data.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Supabase Configuration
    private const val SUPABASE_URL = "https://fbsldvzxfssgmfxlmcwg.supabase.co/functions/v1/"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZic2xkdnp4ZnNzZ21meGxtY3dnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxOTE3MDgsImV4cCI6MjA3ODc2NzcwOH0.2l2cN2BF-gveklbWfE4ep_Wftfza6yVLwGvkJUfxlLE"

    @Provides
    @Singleton
    fun provideChatGuruDatabase(
        @ApplicationContext context: Context
    ): ChatGuruDatabase {
        return Room.databaseBuilder(
            context,
            ChatGuruDatabase::class.java,
            "chatguru_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: ChatGuruDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: ChatGuruDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        @ApplicationContext context: Context
    ): UserPreferencesManager {
        return UserPreferencesManager(context)
    }

    // Network providers
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)  // Increased for Edge Functions
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Add Supabase authentication headers
                val request = chain.request().newBuilder()
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        apiService: ChatApiService,
        chatDao: ChatDao,
        messageDao: MessageDao,
        @ApplicationContext context: Context
    ): ChatRepository {
        return ChatRepository(apiService, chatDao, messageDao, context)
    }
}
