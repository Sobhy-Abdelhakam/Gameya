package dev.sobhy.gameya.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.sobhy.gameya.data.local.dao.GroupDao
import dev.sobhy.gameya.data.local.entity.GroupEntity
import dev.sobhy.gameya.domain.enums.CycleType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupDaoTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: GroupDao

    @Before
    fun setup(){
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.groupDao()
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun insertGroup_shouldSaveDataCorrectly() = runTest {
        val group = GroupEntity(
            name = "Test",
            contributionPerShare = 100.0,
            cycleType = CycleType.MONTHLY.name,
            startDate = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
        )

        dao.insertGroup(group)

        val groups = dao.getAllGroups().first()

        assert(groups.isNotEmpty())
        assert(groups.first().name == "Test")
    }
}