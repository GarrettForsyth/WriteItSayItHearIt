package com.example.android.writeitsayithearit.comments

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.writeitsayithearit.MainActivity
import com.example.android.writeitsayithearit.R
import com.example.android.writeitsayithearit.databinding.CommentListItemBinding
import com.example.android.writeitsayithearit.databinding.StoryListItemBinding
import com.example.android.writeitsayithearit.test.CustomMatchers.hasItemAtPosition
import com.example.android.writeitsayithearit.test.TestUtils.FIRST_STORY_COMMENT_ORDER
import com.example.android.writeitsayithearit.test.TestUtils.SORT_NEW_INDICES
import com.example.android.writeitsayithearit.test.data.DatabaseSeed
import com.example.android.writeitsayithearit.ui.common.DataBoundViewHolder
import com.example.android.writeitsayithearit.util.CountingAppExecutorsRule
import com.example.android.writeitsayithearit.util.DataBindingIdlingResourceRule
import com.example.android.writeitsayithearit.util.TaskExecutorWithIdlingResourceRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * As a writer
 * I want other writers to leave comments on my stories
 * So I can get feedback on my writing
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class CommentsTest {

    @Rule
    @JvmField
    val scenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val countingAppExecutorsRule = CountingAppExecutorsRule()

    private val dbSeed = DatabaseSeed(ApplicationProvider.getApplicationContext())

    private val story = dbSeed.SEED_STORIES.first()

    @Before
    fun navigateToCommentsFragment() {
        DataBindingIdlingResourceRule(scenarioRule)

        // And navigated to the stories list
        onView(withId(R.id.storiesFragment)).perform(click())

        // And clicked on the first story
        onView(withText(story.text)).perform(click())
        onView(withId(R.id.story_comments_button)).perform(click())
    }

    @Test
    fun startingCommentsAreDisplayed() {
        verifyExpectedOrder(FIRST_STORY_COMMENT_ORDER)
    }

    /**
     * Loops through a list of indices and checks that the starting
     * story associated with each index is in the correct order and
     * displayed in the list of stories.
     */
    private fun verifyExpectedOrder(expectedOrder: List<Int>) {
        Espresso.closeSoftKeyboard()
        expectedOrder.forEachIndexed { listPosition, expectedIndex ->
            val expectedComment = dbSeed.SEED_COMMENTS[expectedIndex]
            onView(withId(R.id.comments_list))
                .perform(RecyclerViewActions.scrollToPosition<DataBoundViewHolder<CommentListItemBinding>>(listPosition))
            onView(withId(R.id.comments_list))
                .check(
                    matches(
                        allOf(
                            hasItemAtPosition(hasDescendant(withText(startsWith(expectedComment.text.take(30)))), listPosition),
                            hasItemAtPosition(hasDescendant(withText(expectedComment.rating.toString())), listPosition),
                            hasItemAtPosition(hasDescendant(withText(expectedComment.author)), listPosition)
                        )
                    )
                )
        }
    }
}