package dev.sobhy.gameya.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sobhy.gameya.data.dao.CycleDao
import dev.sobhy.gameya.data.dao.GroupDao
import dev.sobhy.gameya.data.dao.MemberDao
import dev.sobhy.gameya.data.dao.PaymentDao
import dev.sobhy.gameya.data.dao.ShareDao
import dev.sobhy.gameya.data.entity.CycleEntity
import dev.sobhy.gameya.data.entity.GroupEntity
import dev.sobhy.gameya.data.entity.MemberEntity
import dev.sobhy.gameya.data.entity.PaymentEntity
import dev.sobhy.gameya.data.entity.ShareEntity

@Database(
    entities = [
        GroupEntity::class,
        MemberEntity::class,
        ShareEntity::class,
        CycleEntity::class,
        PaymentEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun memberDao(): MemberDao
    abstract fun shareDao(): ShareDao
    abstract fun cycleDao(): CycleDao
    abstract fun paymentDao(): PaymentDao
}