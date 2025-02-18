/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.uimanager.events

import android.view.MotionEvent.*
import android.view.View
import com.facebook.react.uimanager.events.PointerEventHelper.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PointerEventHelperTest {

  @Test
  fun testGetButtons() {
    // exit events
    assertThat(getButtons(POINTER_UP, "", 1)).isEqualTo(0)
    assertThat(getButtons(POINTER_LEAVE, "", 1)).isEqualTo(0)
    assertThat(getButtons(POINTER_OUT, "", 1)).isEqualTo(0)

    // touch pointer type
    assertThat(getButtons(CLICK, POINTER_TYPE_TOUCH, 1)).isEqualTo(1)

    // defaults to return to buttonState
    assertThat(getButtons(POINTER_MOVE, "", 1)).isEqualTo(1)
    assertThat(getButtons(POINTER_MOVE, "", 0)).isEqualTo(0)
  }

  @Test
  fun testGetButtonChange() {
    // pointer type touch
    assertThat(getButtonChange(POINTER_TYPE_TOUCH, 1, 1)).isEqualTo(0)

    // no change in button state
    assertThat(getButtonChange("", BUTTON_PRIMARY, BUTTON_PRIMARY)).isEqualTo(-1)

    // supported button state changes
    assertThat(getButtonChange("", 0, BUTTON_PRIMARY)).isEqualTo(0)
    assertThat(getButtonChange("", BUTTON_PRIMARY, 0)).isEqualTo(0)

    assertThat(getButtonChange("", 0, BUTTON_TERTIARY)).isEqualTo(1)
    assertThat(getButtonChange("", BUTTON_TERTIARY, 0)).isEqualTo(1)

    assertThat(getButtonChange("", 0, BUTTON_SECONDARY)).isEqualTo(2)
    assertThat(getButtonChange("", BUTTON_SECONDARY, 0)).isEqualTo(2)

    assertThat(getButtonChange("", 0, BUTTON_BACK)).isEqualTo(3)
    assertThat(getButtonChange("", BUTTON_BACK, 0)).isEqualTo(3)

    assertThat(getButtonChange("", 0, BUTTON_FORWARD)).isEqualTo(4)
    assertThat(getButtonChange("", BUTTON_FORWARD, 0)).isEqualTo(4)

    // unsupported button state
    val unsupportedButtonMask = 0x10000
    assertThat(getButtonChange("", 0, unsupportedButtonMask)).isEqualTo(-1)
  }

  @Test
  fun testGetW3CPointerType() {
    assertThat(getW3CPointerType(TOOL_TYPE_FINGER)).isEqualTo(POINTER_TYPE_TOUCH)
    assertThat(getW3CPointerType(TOOL_TYPE_STYLUS)).isEqualTo(POINTER_TYPE_PEN)
    assertThat(getW3CPointerType(TOOL_TYPE_MOUSE)).isEqualTo(POINTER_TYPE_MOUSE)

    assertThat(getW3CPointerType(BUTTON_BACK)).isEqualTo(POINTER_TYPE_UNKNOWN)
    assertThat(getW3CPointerType(BUTTON_FORWARD)).isEqualTo(POINTER_TYPE_UNKNOWN)
  }

  @Test
  fun testIsListening() {
    val view = mock(View::class.java)

    assertThat(isListening(null, EVENT.CANCEL)).isEqualTo(true)

    assertThat(isListening(view, EVENT.DOWN)).isEqualTo(true)
    assertThat(isListening(view, EVENT.DOWN_CAPTURE)).isEqualTo(true)
    assertThat(isListening(view, EVENT.UP)).isEqualTo(true)
    assertThat(isListening(view, EVENT.UP_CAPTURE)).isEqualTo(true)
    assertThat(isListening(view, EVENT.CANCEL)).isEqualTo(true)
    assertThat(isListening(view, EVENT.CANCEL_CAPTURE)).isEqualTo(true)
    assertThat(isListening(view, EVENT.CLICK)).isEqualTo(true)
    assertThat(isListening(view, EVENT.CLICK_CAPTURE)).isEqualTo(true)

    assertThat(isListening(view, EVENT.OVER)).isEqualTo(false)
    assertThat(isListening(view, EVENT.OVER_CAPTURE)).isEqualTo(false)
  }

  @Test
  fun testGetEventCategory() {
    assertThat(getEventCategory(POINTER_DOWN)).isEqualTo(EventCategoryDef.DISCRETE)
    assertThat(getEventCategory(POINTER_CANCEL)).isEqualTo(EventCategoryDef.DISCRETE)
    assertThat(getEventCategory(POINTER_UP)).isEqualTo(EventCategoryDef.DISCRETE)

    assertThat(getEventCategory(POINTER_MOVE)).isEqualTo(EventCategoryDef.CONTINUOUS)
    assertThat(getEventCategory(POINTER_ENTER)).isEqualTo(EventCategoryDef.CONTINUOUS)
    assertThat(getEventCategory(POINTER_LEAVE)).isEqualTo(EventCategoryDef.CONTINUOUS)
    assertThat(getEventCategory(POINTER_OVER)).isEqualTo(EventCategoryDef.CONTINUOUS)
    assertThat(getEventCategory(POINTER_OUT)).isEqualTo(EventCategoryDef.CONTINUOUS)

    assertThat(getEventCategory(CLICK)).isEqualTo(EventCategoryDef.UNSPECIFIED)
    assertThat(getEventCategory("unknown_event")).isEqualTo(EventCategoryDef.UNSPECIFIED)
  }

  @Test
  fun testIsExitEvent() {
    assertThat(isExitEvent(POINTER_UP)).isEqualTo(true)
    assertThat(isExitEvent(POINTER_LEAVE)).isEqualTo(true)
    assertThat(isExitEvent(POINTER_OUT)).isEqualTo(true)

    assertThat(isExitEvent(POINTER_MOVE)).isEqualTo(false)
    assertThat(isExitEvent("unknown_event")).isEqualTo(false)
  }

  @Test
  fun testGetPressure() {
    // exit events
    assertThat(getPressure(1, POINTER_UP)).isEqualTo(0.0)
    assertThat(getPressure(1, POINTER_LEAVE)).isEqualTo(0.0)
    assertThat(getPressure(1, POINTER_OUT)).isEqualTo(0.0)

    // non-active button state and non-exit events
    assertThat(getPressure(0, POINTER_MOVE)).isEqualTo(0.0)
    assertThat(getPressure(0, POINTER_CANCEL)).isEqualTo(0.0)

    // active button state and non-exit events
    assertThat(getPressure(1, POINTER_MOVE)).isEqualTo(0.5)
    assertThat(getPressure(1, POINTER_CANCEL)).isEqualTo(0.5)
  }

  @Test
  fun testIsBubblingEvent() {
    assertThat(isBubblingEvent(POINTER_UP)).isEqualTo(true)
    assertThat(isBubblingEvent(POINTER_DOWN)).isEqualTo(true)
    assertThat(isBubblingEvent(POINTER_OVER)).isEqualTo(true)
    assertThat(isBubblingEvent(POINTER_OUT)).isEqualTo(true)
    assertThat(isBubblingEvent(POINTER_MOVE)).isEqualTo(true)
    assertThat(isBubblingEvent(POINTER_CANCEL)).isEqualTo(true)

    assertThat(isBubblingEvent(CLICK)).isEqualTo(false)
    assertThat(isBubblingEvent("unknown_event")).isEqualTo(false)
  }
}
