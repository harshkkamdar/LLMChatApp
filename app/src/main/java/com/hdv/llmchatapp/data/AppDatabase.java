package com.hdv.llmchatapp.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.hdv.llmchatapp.data.dao.ChatDao;
import com.hdv.llmchatapp.data.dao.MessageDao;
import com.hdv.llmchatapp.data.entity.Chat;
import com.hdv.llmchatapp.data.entity.Message;

@Database(entities = {Chat.class, Message.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ChatDao chatDao();
    public abstract MessageDao messageDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "chat_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
} 