package com.example.android.writeitsayithearit.comments

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.writeitsayithearit.data.CommentDao
import com.example.android.writeitsayithearit.data.StoryDao
import com.example.android.writeitsayithearit.data.WriteItSayItHearItDatabase
import com.example.android.writeitsayithearit.test.TestUtils.createTestComment
import com.example.android.writeitsayithearit.test.TestUtils.createTestStory
import com.example.android.writeitsayithearit.test.data.DatabaseSeed
import com.example.android.writeitsayithearit.test.getValueBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@LargeTest
@RunWith(AndroidJUnit4::class)
class CommentDaoTest {

    @Rule
    @JvmField
    val instantTaskExecutor = InstantTaskExecutorRule()

    private val dbSeed = DatabaseSeed(ApplicationProvider.getApplicationContext())
    private val authors = dbSeed.SEED_AUTHORS
    private val stories = dbSeed.SEED_STORIES
    private val comments = dbSeed.SEED_COMMENTS

    private lateinit var commentDao: CommentDao
    private lateinit var db: WriteItSayItHearItDatabase

    @Before
    fun createAndSeedDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            WriteItSayItHearItDatabase::class.java
        ).allowMainThreadQueries().build()

        commentDao = db.commentDao()

        // seed with starting data
        db.authorDao().insert(authors)
        db.storyDao().insert(stories)
        db.commentDao().insert(comments)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun writeAndReadComment() {
        val comment = createTestComment().apply {
            author = authors.first().name
            storyId = stories.first().id
        }
        val id = comments.size + 1
        commentDao.insert(comment)

        val readComment = commentDao.comment(id).getValueBlocking()
        assertTrue(readComment.text.equals(comment.text))
    }
}

