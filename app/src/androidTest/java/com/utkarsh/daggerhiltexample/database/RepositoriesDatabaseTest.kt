package com.utkarsh.daggerhiltexample.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RepositoriesDatabaseTest {

    private lateinit var dao: RepositoriesDatabaseDao
    private lateinit var db: RepositoriesDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, RepositoriesDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        dao = db.repositoriesDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testNameSortedRepositories() {
        val repos: MutableList<DatabaseRepository> = mutableListOf()
        for (i in 0..3) {
            val repo = DatabaseRepository("",
                                            "name$i",
                                            null,
                                            "url$i",
                                            null,
                                            null,
                                            null,
                                            null,
                                            null,
                                            null)
            repos.add(repo)
        }
        dao.insertAllRepositories(repos)
        val dbRepos = dao.getAllRepositoriesSortedByName()
        for (i in 1..3) {
            val previousRepo = dbRepos[i-1]
            val currentRepo = dbRepos[i]
            assertTrue(previousRepo.name < currentRepo.name)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testStarSortedRepositories() {
        val repos: MutableList<DatabaseRepository> = mutableListOf()
        for (i in 0..3) {
            val stars = 3-i
            val repo = DatabaseRepository("",
                "",
                null,
                "name$i",
                null,
                null,
                null,
                stars,
                null,
                null)
            repos.add(repo)
        }
        dao.insertAllRepositories(repos)
        val dbRepos = dao.getAllRepositoriesSortedByStars()
        for (i in 1..3) {
            val previousRepo = dbRepos[i-1]
            val currentRepo = dbRepos[i]
            assertTrue(previousRepo.stars!! > currentRepo.stars!!)
        }
    }

}