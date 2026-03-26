package dev.sobhy.gameya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sobhy.gameya.data.local.dao.CycleDao
import dev.sobhy.gameya.data.local.dao.GroupDao
import dev.sobhy.gameya.data.local.dao.MemberDao
import dev.sobhy.gameya.data.local.dao.PaymentDao
import dev.sobhy.gameya.data.local.dao.ShareDao
import dev.sobhy.gameya.data.local.entity.CycleEntity
import dev.sobhy.gameya.data.local.entity.GroupEntity
import dev.sobhy.gameya.data.local.entity.MemberEntity
import dev.sobhy.gameya.data.local.entity.PaymentEntity
import dev.sobhy.gameya.data.local.entity.ShareEntity

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