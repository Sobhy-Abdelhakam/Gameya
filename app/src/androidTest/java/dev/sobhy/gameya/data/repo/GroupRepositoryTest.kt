package dev.sobhy.gameya.data.repo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.sobhy.gameya.data.local.AppDatabase
import dev.sobhy.gameya.data.repository_impl.GroupRepositoryImpl
import dev.sobhy.gameya.domain.enums.CycleType
import dev.sobhy.gameya.domain.model.Group
import dev.sobhy.gameya.domain.repository.GroupRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repository: GroupRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = GroupRepositoryImpl(db)
    }
    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun createGroup_shouldInsertInroDatabase() = runTest {
        val group = Group(
            name = "Sobhy Group",
            contributionPerShare = 100.0,
            cycleType = CycleType.MONTHLY,
            startDate = System.currentTimeMillis(),
        )

        repository.createGroup(
            group = group,
            members = emptyList(),
        )

        val result = db.groupDao().getAllGroups().first()

        assert(result.size == 1)
        assert(result.first().name == "Sobhy Group")
    }
}