/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.uimanager.events

import com.facebook.react.uimanager.events.Event

/** Interface used to intercept events dispatched by {#link EventDispatcher} */
public fun interface EventDispatcherListener {
  /**
   * Called on every time an event is dispatched using {#link EventDispatcher#dispatchEvent}. Will
   * be called from the same thread that the event is being dispatched from.
   *
   * @param event Event that was dispatched
   */
  public fun onEventDispatch(event: Event<*>)
}
