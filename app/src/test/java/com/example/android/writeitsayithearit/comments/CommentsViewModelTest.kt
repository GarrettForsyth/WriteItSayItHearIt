package com.example.android.writeitsayithearit.comments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.android.writeitsayithearit.model.SortOrder
import com.example.android.writeitsayithearit.repos.CommentRepository
import com.example.android.writeitsayithearit.repos.StoryRepository
import com.example.android.writeitsayithearit.repos.utils.WSHQueryHelper.stories
import com.example.android.writeitsayithearit.test.getValueBlocking
import com.example.android.writeitsayithearit.ui.comments.CommentsViewModel
import com.example.android.writeitsayithearit.ui.stories.StoriesViewModel
import com.example.android.writeitsayithearit.ui.util.QueryParameters
import com.example.android.writeitsayithearit.util.MockUtils.mockObserverFor
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class CommentsViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val commentRepository: CommentRepository = mockk(relaxed = true)

    private lateinit var commentsViewModel: CommentsViewModel

    @Before
    fun init() {
        commentsViewModel = CommentsViewModel(commentRepository)

        mockObserverFor(commentsViewModel.comments)
        mockObserverFor(commentsViewModel.hasResultsStatus)
    }

    @Test
    fun getComments() {
        commentsViewModel.queryParameters.filterStoryId = 1
        val expectedQueryParameters = QueryParameters(_filterStoryId = 1)
        verify { commentRepository.comments(expectedQueryParameters) }
    }

    @Test
    fun getChildComments() {
        commentsViewModel.childComments(1)
        verify { commentRepository.childComments(1) }
    }

    @Test
    fun onNewCommentButtonClick(){
        commentsViewModel.onClickNewComment(-1)
        assertNotNull(commentsViewModel.shouldNavigateToNewComment.getValueBlocking().peekContent())
    }

    @Test
    fun sortByNew() {
        commentsViewModel.queryParameters.sortOrder = SortOrder.NEW
        val expectedParameters = QueryParameters()
        verify { commentRepository.comments(expectedParameters) }
    }

    @Test
    fun sortByTop() {
        commentsViewModel.queryParameters.sortOrder = SortOrder.TOP
        val expectedParameters = QueryParameters(_sortOrder = SortOrder.TOP)
        verify { commentRepository.comments(expectedParameters) }
    }

    @Test
    fun sortByHot() {
        commentsViewModel.queryParameters.sortOrder = SortOrder.HOT
        val expectedParameters = QueryParameters(_sortOrder = SortOrder.HOT)
        verify { commentRepository.comments(expectedParameters) }
    }
}

