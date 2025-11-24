package com.chatguru.ai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chatguru.ai.data.local.dao.ChatDao
import com.chatguru.ai.data.local.dao.MessageDao
import com.chatguru.ai.data.local.entity.ChatEntity
import com.chatguru.ai.data.local.entity.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 3,  // CHANGED from 1 to 2
    exportSchema = false
)
abstract class ChatGuruDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}

