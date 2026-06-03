package by.voiteshonok.valacugi.ui.identity

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatDisplayIdTest {
    @Test
    fun formatDisplayId_whenUserIdIsNull_returnsPlaceholder() {
        val actualDisplayId: String = formatDisplayId(userId = null)
        assertEquals("—", actualDisplayId)
    }

    @Test
    fun formatDisplayId_whenUserIdIsBlank_returnsPlaceholder() {
        val actualDisplayId: String = formatDisplayId(userId = "   ")
        assertEquals("—", actualDisplayId)
    }

    @Test
    fun formatDisplayId_whenUserIdIsPresent_returnsUppercasePrefixedId() {
        val actualDisplayId: String = formatDisplayId(userId = "user_admin")
        assertEquals("ID: USER_ADMIN", actualDisplayId)
    }
}
