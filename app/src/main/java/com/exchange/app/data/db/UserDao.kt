package com.exchange.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.exchange.app.domain.entity.User

@Dao
interface UserDao {

    @Query("SELECT (SELECT COUNT(*) FROM User) == 0")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE name = :name ")
    suspend fun getUser(name: String): User
}
