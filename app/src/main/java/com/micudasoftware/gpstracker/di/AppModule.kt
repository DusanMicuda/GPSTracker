package com.micudasoftware.gpstracker.di

import android.content.Context
import androidx.room.Room
import com.micudasoftware.gpstracker.db.TrackDatabase
import com.micudasoftware.gpstracker.other.Constants.TRACK_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTrackDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            TrackDatabase::class.java,
            TRACK_DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideTrackDao(
        db: TrackDatabase
    ) = db.getTrackDao()
}