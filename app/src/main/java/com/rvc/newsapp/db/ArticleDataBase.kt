package com.rvc.newsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rvc.newsapp.model.Article


@Database(entities = [Article::class],
    version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDataBase :RoomDatabase() {

    abstract fun getArticleDao():ArticleDao

    companion object{

        private var instance : ArticleDataBase?=null
        private val  LOCK = Any()

        operator fun invoke(context:Context) = instance?:
        synchronized(LOCK){
            instance?: createDataBase(context).also {
                instance=it
            }
        }

        private fun createDataBase(context: Context):ArticleDataBase
        {
            return Room.databaseBuilder(context.applicationContext,
                ArticleDataBase::class.java,"article_db").build()
        }

    }
}