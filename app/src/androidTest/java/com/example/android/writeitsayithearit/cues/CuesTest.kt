package com.example.android.writeitsayithearit.cues

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.writeitsayithearit.test.CustomMatchers.Companion.hasItemAtPosition
import com.example.android.writeitsayithearit.MainActivity
import com.example.android.writeitsayithearit.R
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_AUTHOR
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_AUTHOR_NEW_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_SORT_HOT_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_SORT_NEW_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_SORT_TOP_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.CUE_FILTER_TEXT
import com.example.android.writeitsayithearit.test.TestUtils.FILTER_STRING_NO_MATCHES
import com.example.android.writeitsayithearit.test.TestUtils.SORT_HOT_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.SORT_NEW_INDICES
import com.example.android.writeitsayithearit.test.TestUtils.SORT_TOP_INDICES
import com.example.android.writeitsayithearit.test.data.DatabaseSeed
import com.example.android.writeitsayithearit.ui.cues.CueViewHolder
import com.example.android.writeitsayithearit.util.CountingAppExecutorsRule
import com.example.android.writeitsayithearit.util.DataBindingIdlingResourceRule
import com.example.android.writeitsayithearit.util.TaskExecutorWithIdlingResourceRule
import kotlinx.android.synthetic.main.fragment_cues.*
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * As a writer
 * I want to browse a list of cues
 * To find a cue that inspires me to write
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class CuesTest {

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

    @Before
    fun launchApp() {
        DataBindingIdlingResourceRule(scenarioRule)
    }

    @Test
    fun startingCuesAreDisplayed() {
        verifyExpectedOrder(SORT_NEW_INDICES)
    }

    @Test
    fun userFiltersByCueAuthor() {
        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(CUE_FILTER_AUTHOR))

        verifyExpectedOrder(CUE_FILTER_AUTHOR_NEW_INDICES)
    }

    @Test
    fun noResults() {
        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(FILTER_STRING_NO_MATCHES))

        onView(withId(R.id.no_results)).check(matches(isDisplayed()))

        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(""))
            .perform(replaceText(CUE_FILTER_TEXT))

        onView(withId(R.id.no_results)).check(matches(not(isDisplayed())))
    }

    @Test
    fun sortCuesByNew() {
        selectSpinnerEntry("New")
        verifyExpectedOrder(SORT_NEW_INDICES)
    }

    @Test
    fun sortCuesByTop() {
        selectSpinnerEntry("Top")
        verifyExpectedOrder(SORT_TOP_INDICES)
    }

    @Test
    fun sortCuesByHot() {
        // When I chooses order by 'hot' on the spinner
        selectSpinnerEntry("Hot")
        verifyExpectedOrder(SORT_HOT_INDICES)
    }

    @Test
    fun sortCuesByHotAndFilter() {
        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(CUE_FILTER_TEXT))

        selectSpinnerEntry("Hot")
        verifyExpectedOrder(CUE_FILTER_SORT_HOT_INDICES)
    }

    @Test
    fun sortCuesByNewAndFilter() {
        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(CUE_FILTER_TEXT))

        selectSpinnerEntry("New")
        verifyExpectedOrder(CUE_FILTER_SORT_NEW_INDICES)
    }

    @Test
    fun sortCuesByTopAndFilter() {
        onView(withId(R.id.filter_cues_edit_text))
            .perform(replaceText(CUE_FILTER_TEXT))

        selectSpinnerEntry("Top")
        verifyExpectedOrder(CUE_FILTER_SORT_TOP_INDICES)
    }

    private fun selectSpinnerEntry(entry: String) {
        onView(withId(R.id.sort_order_spinner))
            .perform(click())
        onData(
            allOf(
                `is`(instanceOf(String::class.java)),
                `is`(entry)
            )
        ).perform(click())
    }

    /**
     * Loops through a list of indices and checks that the starting
     * cue associated with each index is in the correct order and
     * displayed in the list of cues.
     */
    private fun verifyExpectedOrder(expectedOrder: List<Int>) {
        Espresso.closeSoftKeyboard()
        expectedOrder.forEachIndexed { listPosition, expectedIndex ->
            val expectedCue = dbSeed.SEED_CUES[expectedIndex]
            onView(withId(R.id.cues_list))
                .perform(RecyclerViewActions.scrollToPosition<CueViewHolder>(listPosition))

            onView(withId(R.id.cues_list))
                .check(
                    matches(
                        allOf(
                            hasItemAtPosition(hasDescendant(withText(startsWith(expectedCue.text))), listPosition),
                            hasItemAtPosition(hasDescendant(withText(expectedCue.rating.toString())), listPosition),
                            hasItemAtPosition(hasDescendant(withText(expectedCue.author)), listPosition),
                            hasItemAtPosition(hasDescendant(withText(expectedCue.formattedDate())), listPosition)
                        )
                    )
                )
        }
    }
}