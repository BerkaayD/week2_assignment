package com.example.week2_assignment

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DevClusterScreenTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun scrollTo(tag: String) {
        compose.onNodeWithTag("hardware_list").performScrollToNode(hasTestTag(tag))
    }

    private fun search(query: String) {
        scrollTo("hardware_search")
        compose.onNodeWithTag("hardware_search").performTextReplacement(query)
    }

    @Test fun queryStockAndSpendingSurviveActivityRecreation() {
        search("Pixel")
        scrollTo("allocate_103")
        compose.onNodeWithTag("allocate_103").performClick()
        compose.activityRule.scenario.recreate()
        scrollTo("hardware_search")
        compose.onNodeWithTag("hardware_search").assertTextContains("Pixel")
        scrollTo("hardware_card_103")
        compose.onNodeWithTag("stock_103", useUnmergedTree = true).assertTextEquals("7 adet stokta")
        scrollTo("total_spent")
        compose.onNodeWithTag("total_spent").assertTextEquals("$999.00")
    }

    @Test fun searchSurvivesActualLandscapeRotation() {
        compose.runOnUiThread { compose.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        compose.waitUntil(10_000) { compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT }
        scrollTo("hardware_search")
        compose.onNodeWithTag("hardware_search").performClick().performTextInput("Raspberry")
        val portraitActivity = compose.activity
        compose.runOnUiThread { compose.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        compose.waitUntil(10_000) {
            compose.activity !== portraitActivity &&
                compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }
        compose.waitForIdle()
        scrollTo("hardware_search")
        compose.onNodeWithTag("hardware_search").assertTextContains("Raspberry")
        scrollTo("result_count")
        compose.onNodeWithTag("result_count").assertTextEquals("Envanter · 1 sonuç")
        compose.runOnUiThread { compose.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
    }

    @Test fun categoryAndQueryStayCombinedAfterStockUpdateAndRecreation() {
        scrollTo("category_list")
        compose.onNodeWithTag("category_list").performScrollToNode(hasTestTag("category_Yapay Zeka / GPU"))
        compose.onNodeWithTag("category_Yapay Zeka / GPU").performClick()
        search("NVIDIA")
        scrollTo("allocate_102")
        compose.onNodeWithTag("allocate_102").performClick()
        compose.onNodeWithTag("stock_102", useUnmergedTree = true).assertTextEquals("1 adet stokta")
        compose.activityRule.scenario.recreate()
        scrollTo("result_count")
        compose.onNodeWithTag("result_count").assertTextEquals("Envanter · 1 sonuç")
        search("Pixel")
        scrollTo("result_count")
        compose.onNodeWithTag("result_count").assertTextEquals("Envanter · 0 sonuç")
    }

    @Test fun lastStockDisablesAllocationAndResetRestoresIt() {
        search("Threadripper")
        scrollTo("allocate_108")
        compose.onNodeWithTag("allocate_108").performClick()

        compose.onNodeWithTag("allocate_108", useUnmergedTree = true).assertIsNotEnabled()
        compose.onNodeWithTag("stock_108", useUnmergedTree = true).assertTextEquals("Stok tükendi")
        scrollTo("total_spent")
        compose.onNodeWithTag("total_spent").assertTextEquals("$4,999.00")
        scrollTo("reset_session")
        compose.onNodeWithTag("reset_session").performClick()
        compose.onNodeWithTag("confirm_reset").performClick()
        scrollTo("allocate_108")
        compose.onNodeWithTag("allocate_108").assertIsEnabled()
        scrollTo("total_spent")
        compose.onNodeWithTag("total_spent").assertTextEquals("$0.00")
    }

    @Test fun initiallySoldOutHardwareCannotBeAllocated() {
        search("HHKB")
        scrollTo("allocate_107")
        compose.onNodeWithTag("allocate_107").assertIsNotEnabled()
    }

    @Test fun emptySearchResultsCanBeCleared() {
        search("does-not-exist")
        scrollTo("clear_filters")
        compose.onNodeWithTag("clear_filters").performClick()
        scrollTo("result_count")
        compose.onNodeWithTag("result_count").assertTextEquals("Envanter · 8 sonuç")
    }
}
