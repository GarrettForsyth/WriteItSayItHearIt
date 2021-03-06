package com.example.android.sparkstories.cues

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.FragmentScenario.launchInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.sparkstories.R
import com.example.android.sparkstories.TestApp
import com.example.android.sparkstories.model.Resource
import com.example.android.sparkstories.model.cue.Cue
import com.example.android.sparkstories.stories.StoriesFragmentTest
import com.example.android.sparkstories.test.TestUtils.createTestCue
import com.example.android.sparkstories.ui.cues.CueFragment
import com.example.android.sparkstories.ui.cues.CueFragmentDirections
import com.example.android.sparkstories.ui.util.events.Event
import com.example.android.sparkstories.util.InstantAppExecutors
import com.example.android.sparkstories.util.ViewModelUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.*

@LargeTest
@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class CueFragmentTest {

    companion object {
        private val CUE_ID_EXTRA = "cue_id"
        private val CUE = createTestCue()
    }

    private var scenario: FragmentScenario<TestCueFragment>

    // initialize the fragment with a cue id passed as an argument
    init {
        val args = Bundle()
        args.putString(CUE_ID_EXTRA, CUE.id)
        scenario = launchInContainer(
            TestCueFragment::class.java,
            args,
            TestCueFragmentFactory()
        )
    }

    @Test
    fun getCue() {
        scenario.onFragment {
            verify { it.cueViewModel.getCue(CUE.id) }
        }
    }

    @Test
    fun newStoryButtonClicked() {
        scenario.onFragment {
            it.cue.postValue(Resource.success(CUE))
            it.newStoryButtonClick.value = Event(true)
            verify {
                it.navController.navigate(
                    CueFragmentDirections.actionCueFragmentToNewStoryFragment(CUE.id)
                )
            }
        }
    }

    @Test
    fun displayCue() {
        scenario.onFragment {
            it.cue.postValue(Resource.success(CUE))
            onView(withId(R.id.cue_text))
                .check(matches(withText(CUE.text)))
        }
    }

    @Test
    fun filterCue() {
        scenario.onFragment {
            it.cue.postValue(Resource.success(CUE))
            assertEquals(it.storiesFragment.storiesViewModel.queryParameters.filterCueId, CUE.id)
        }
    }


    class TestCueFragmentFactory : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?): Fragment {
            return (super.instantiate(classLoader, className, args)
                    as TestCueFragment).apply {
                this.cueViewModel = mockk(relaxed = true)
                this.storiesFragment = StoriesFragmentTest.TestStoriesFragment()
                this.viewModelFactory = ViewModelUtil.createFor(this.cueViewModel)

                this.storiesFragment.storiesViewModel = mockk(relaxed = true)
                this.storiesFragment.viewModelFactory = ViewModelUtil.createFor(this.storiesFragment.storiesViewModel)
                this.storiesFragment.appExecutors = InstantAppExecutors()

                every { cueViewModel.cue } returns this.cue
                every { cueViewModel.newStoryButtonClick } returns newStoryButtonClick
                every { storiesFragment.storiesViewModel.queryParameters.filterCueId } returns CUE.id
            }
        }

    }
}

class TestCueFragment : CueFragment() {
    val navController: NavController = mockk(relaxed = true)
    override fun navController() = navController

    val cue = MutableLiveData<Resource<Cue>>()
    val newStoryButtonClick = MutableLiveData<Event<Boolean>>()
}

