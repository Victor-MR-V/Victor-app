package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Victor App", appName)
  }

  @Test
  fun `test bengali formatting and taka formatting`() {
    val digits = com.example.util.FormatUtils.toBengaliDigits("12345")
    assertEquals("১২৩৪৫", digits)

    val taka = com.example.util.FormatUtils.formatTaka(25000.0)
    assertEquals("৳ ২৫,০০০", taka)
  }
}

