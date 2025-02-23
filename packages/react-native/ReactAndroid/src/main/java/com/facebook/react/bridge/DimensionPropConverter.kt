/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.bridge

import com.facebook.yoga.YogaUnit
import com.facebook.yoga.YogaValue

internal object DimensionPropConverter {
  fun getDimension(value: Any?): YogaValue? {
    if (value == null) {
      return null
    }

    if (value is Double) {
      return YogaValue(value.toFloat(), YogaUnit.POINT)
    }

    if (value is String) {
      return YogaValue.parse(value)
    }

    throw JSApplicationCausedNativeException(
      "DimensionValue: the value must be a number or string."
    )
  }
}
