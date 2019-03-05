package com.example.android.writeitsayithearit.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.android.writeitsayithearit.repos.CueRepository
import com.example.android.writeitsayithearit.repos.StoryRepository
import com.example.android.writeitsayithearit.test.TestUtils.createTestCue
import com.example.android.writeitsayithearit.test.TestUtils.createTestStory
import com.example.android.writeitsayithearit.test.asLiveData
import com.example.android.writeitsayithearit.test.getValueBlocking
import com.example.android.writeitsayithearit.ui.stories.StoryViewModel
import com.example.android.writeitsayithearit.util.MockUtils.mockObserverFor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@SmallTest
@RunWith(JUnit4::class)
class StoryViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val storyRepository: StoryRepository = mockk(relaxed = true)
    private val cueRepository: CueRepository = mockk(relaxed = true)

    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun init() {
        storyViewModel = StoryViewModel(storyRepository, cueRepository)

        mockObserverFor(storyViewModel.story)
        mockObserverFor(storyViewModel.cue)
        mockObserverFor(storyViewModel.topMenuStatus)
    }

    @Test()
    fun getStory(){
        val cue = createTestCue().apply{ id = 12}
        every { cueRepository.cue(cue.id) } returns cue.asLiveData()

        val story = createTestStory().apply { cueId = cue.id }
        every { storyRepository.story(story.id) } returns story.asLiveData()

        // Call get story
        storyViewModel.getStory(story.id)

        verify { storyRepository.story(story.id)}
        verify { cueRepository.cue(cue.id) }
        assertEquals(storyViewModel.story.getValueBlocking(), story)
        assertEquals(storyViewModel.cue.getValueBlocking(), cue)
    }

    @Test
    fun toggleMenu() {
        // initial state should be true
        assertTrue(storyViewModel.topMenuStatus.getValueBlocking().peekContent())

        storyViewModel.onToggleMenu()
        assertFalse(storyViewModel.topMenuStatus.getValueBlocking().peekContent())

        storyViewModel.onToggleMenu()
        assertTrue(storyViewModel.topMenuStatus.getValueBlocking().peekContent())
    }

    @Test
    fun likeStory() {
        val story = createTestStory().apply { id = 10 }
        every { storyRepository.story(story.id) } returns story.asLiveData()
        storyViewModel.getStory(story.id)

        storyViewModel.onLikeStoryClick()
        val updatedStory = story.copy().apply { rating++ }
        verify { storyRepository.update(updatedStory) }
    }

}