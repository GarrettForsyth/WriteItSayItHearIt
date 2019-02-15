package com.example.android.writeitsayithearit.cues

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.writeitsayithearit.data.CueDao
import com.example.android.writeitsayithearit.data.WriteItSayItHearItDatabase
import com.example.android.writeitsayithearit.repos.utils.WSHQueryHelper
import com.example.android.writeitsayithearit.test.TestUtils
import com.example.android.writeitsayithearit.test.getValueBlocking
import com.example.android.writeitsayithearit.vo.Cue
import com.example.android.writeitsayithearit.vo.SortOrder
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@LargeTest
@RunWith(AndroidJUnit4::class)
class CueDaoTest {

    @Rule
    @JvmField
    val instantTaskExecutor = InstantTaskExecutorRule()

    private lateinit var cueDao: CueDao
    private lateinit var db: WriteItSayItHearItDatabase
    private val cues = TestUtils.listOfStartingCues

    @Before
    fun createAndSeedDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context,
                WriteItSayItHearItDatabase::class.java
        ).allowMainThreadQueries().build()

        cueDao = db.cueDao()

        // seed with starting data
        cueDao.insert(cues)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCue() {
        val cue = TestUtils.createTestCue()
        val id = cues.size + 1
        cueDao.insert(cue)

        val readCue = cueDao.cue(id).getValueBlocking()
        assertTrue(readCue.text.equals(cue.text))
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueList() {
        val readCues = cueDao.cues(WSHQueryHelper.cues()).getValueBlocking()
        for (cue in cues) {
            assert(readCues.contains(cue))
        }
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueListWithFilter() {
        val query = WSHQueryHelper.cues("Dogs")
        val readCues = cueDao.cues(query).getValueBlocking()
        assert(readCues.size == 1)
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueListWithSortOrderNew() {
        val query = WSHQueryHelper.cues("", SortOrder.NEW)
        val readCues = cueDao.cues(query).getValueBlocking()

        val expectedCueOrder = TestUtils.sortByNewIndices
        assertCorrectOrder(expectedCueOrder, readCues)
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueListWithTop() {
        val query = WSHQueryHelper.cues("", SortOrder.TOP)
        val readCues = cueDao.cues(query).getValueBlocking()

        val expectedCueOrder = TestUtils.sortByTopIndices
        assertCorrectOrder(expectedCueOrder, readCues)
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueListWithHot() {
        val query = WSHQueryHelper.cues("", SortOrder.HOT)
        val readCues = cueDao.cues(query).getValueBlocking()

        val expectedCueOrder = TestUtils.sortByHotIndices
        assertCorrectOrder(expectedCueOrder, readCues)
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadCueListWithHotWithFilter() {
        val query = WSHQueryHelper.cues("to", SortOrder.HOT)
        val readCues = cueDao.cues(query).getValueBlocking()

        val expectedCueOrder = TestUtils.sortByHotAndFilter
        assertCorrectOrder(expectedCueOrder, readCues)
    }

    private fun assertCorrectOrder(expectedOrder: List<Int>, actualOrder: List<Cue>) {
        for (i in 0 until expectedOrder.size) {
            assert(cues[expectedOrder[i]].equals(actualOrder[i]))
        }
    }

}