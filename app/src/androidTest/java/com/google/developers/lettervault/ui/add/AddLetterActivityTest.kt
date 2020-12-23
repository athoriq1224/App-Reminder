package com.google.developers.lettervault.ui.add

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.google.developers.lettervault.R
import com.google.developers.lettervault.ui.home.HomeActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Imam Fahrur Rofi on 19/09/2020.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class AddLetterActivityTest {

    @get:Rule
    val activityTestRule = ActivityScenarioRule(HomeActivity::class.java)

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {

    }

    @Test
    fun fromHomeToList() {
        onView(withId(R.id.action_list)).perform(ViewActions.click())
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.fab))
            .check(matches(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.edt_subject))
            .check(matches(isDisplayed()))
        onView(withId(R.id.edt_content))
            .check(matches(isDisplayed()))
    }
}