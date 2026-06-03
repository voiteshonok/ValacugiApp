package by.voiteshonok.valacugi.access

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import by.voiteshonok.valacugi.domain.User
import by.voiteshonok.valacugi.ui.theme.ValacugiTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun accessScreen_displaysPrimaryLabels() {
        composeTestRule.setContent {
            ValacugiTheme {
                AccessScreen(authenticate = { null })
            }
        }
        composeTestRule.onNodeWithText("[  VALACUGI  ]").assertIsDisplayed()
        composeTestRule.onNodeWithText("ACCESS SYSTEM").assertIsDisplayed()
        composeTestRule.onNodeWithText("ENTER CREDENTIALS TO PROCEED").assertIsDisplayed()
        composeTestRule.onNodeWithText("IDENTIFICATION").assertIsDisplayed()
        composeTestRule.onNodeWithText("CREDENTIAL").assertIsDisplayed()
        composeTestRule.onNodeWithText("CONTINUE  →").assertIsDisplayed()
    }

    @Test
    fun accessScreen_whenAuthenticationFails_showsErrorMessage() {
        composeTestRule.setContent {
            ValacugiTheme {
                AccessScreen(authenticate = { null })
            }
        }
        composeTestRule.onNodeWithTag(AccessTestTags.IdentificationField).performTextInput("admin")
        composeTestRule.onNodeWithTag(AccessTestTags.CredentialField).performTextInput("wrong")
        composeTestRule.onNodeWithTag(AccessTestTags.ContinueButton).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("WRONG LOGIN / PASSWORD")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("WRONG LOGIN / PASSWORD").assertIsDisplayed()
    }

    @Test
    fun accessScreen_whenAuthenticationSucceeds_invokesOnContinue() {
        val expectedUser: User = User(
            id = "user_admin",
            login = "admin",
            displayName = "Administrator",
            isPushNotificationsEnabled = true
        )
        var continuedUser: User? = null
        composeTestRule.setContent {
            ValacugiTheme {
                AccessScreen(
                    authenticate = { credentials: AccessCredentials ->
                        if (credentials.identification == "admin" && credentials.credential == "admin") {
                            expectedUser
                        } else {
                            null
                        }
                    },
                    onContinue = { user: User -> continuedUser = user }
                )
            }
        }
        composeTestRule.onNodeWithTag(AccessTestTags.IdentificationField).performTextInput("admin")
        composeTestRule.onNodeWithTag(AccessTestTags.CredentialField).performTextInput("admin")
        composeTestRule.onNodeWithTag(AccessTestTags.ContinueButton).performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            continuedUser != null
        }
        assertEquals(expectedUser, continuedUser)
    }
}
