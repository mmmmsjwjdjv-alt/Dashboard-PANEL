package com.example

import com.example.data.model.ConnectionConfig
import com.example.data.model.UserLicense
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testParseRawUrl_standardRefHeads() {
        val url = "https://raw.githubusercontent.com/octocat/Hello-World/refs/heads/main/README.md"
        val info = ConnectionConfig.parseRawUrl(url)
        assertEquals("octocat", info.owner)
        assertEquals("Hello-World", info.repo)
        assertEquals("main", info.branch)
        assertEquals("README.md", info.path)
    }

    @Test
    fun testParseRawUrl_shortBranchFormat() {
        val url = "https://raw.githubusercontent.com/octocat/Hello-World/master/folder/sub/data.json"
        val info = ConnectionConfig.parseRawUrl(url)
        assertEquals("octocat", info.owner)
        assertEquals("Hello-World", info.repo)
        assertEquals("master", info.branch)
        assertEquals("folder/sub/data.json", info.path)
    }

    @Test
    fun testLicenseExpiryCalculations() {
        val expiry = UserLicense.calculateExpiry(30)
        assertNotNull(expiry)
        val days = UserLicense.parseDaysLeft(expiry)
        assertNotNull(days)
        assertTrue(days!! in 29..31)

        val user = UserLicense(
            deviceId = "dev_123",
            key = "Mahan",
            expiryDate = expiry,
            allowOffline = true,
            isDisabled = false
        )
        assertTrue(user.isActive)
        assertEquals(false, user.isDisabled)
    }

    @Test
    fun testDisabledUserLicenseState() {
        val user = UserLicense(
            deviceId = "dev_999",
            key = "TestUser",
            expiryDate = UserLicense.calculateExpiry(10),
            allowOffline = true,
            isDisabled = true
        )
        assertFalse(user.isActive)
        assertEquals("Paused", user.statusText)
    }
}
