/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.modules.fresco

import android.net.Uri
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import java.util.concurrent.ExecutorService
import okhttp3.Call
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when` as whenever
import org.robolectric.RobolectricTestRunner

/**
 * Returns Mockito.any() as nullable type to avoid java.lang.IllegalStateException when null is
 * returned.
 */
private fun <T> anyOrNull(type: Class<T>): T = any(type)

/**
 * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException when
 * null is returned.
 */
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

@RunWith(RobolectricTestRunner::class)
class ReactOkHttpNetworkFetcherTest {
  @Captor private lateinit var requestArgumentCaptor: ArgumentCaptor<Request>

  @Test
  fun testFetch() {
    val okHttpClient = mock(OkHttpClient::class.java)
    val dispatcher = mock(Dispatcher::class.java)
    val executorService = mock(ExecutorService::class.java)
    requestArgumentCaptor = ArgumentCaptor.forClass(Request::class.java)

    whenever(okHttpClient.dispatcher).thenReturn(dispatcher)
    whenever(dispatcher.executorService).thenReturn(executorService)
    whenever(okHttpClient.newCall(anyOrNull(Request::class.java))).thenAnswer {
      val callMock = mock(Call::class.java)
      callMock
    }

    val fetcher = ReactOkHttpNetworkFetcher(okHttpClient)
    val fetchState = mock(OkHttpNetworkFetcher.OkHttpNetworkFetchState::class.java)
    val callback = mock(NetworkFetcher.Callback::class.java)

    val mockUri = Uri.parse("http://www.facebook.com")
    whenever(fetchState.uri).thenReturn(mockUri)

    val producerContext = mock(ProducerContext::class.java)
    val imageRequest = mock(ReactNetworkImageRequest::class.java)

    whenever(fetchState.context).thenReturn(producerContext)
    whenever(producerContext.imageRequest).thenReturn(imageRequest)
    whenever(imageRequest.cacheControl).thenReturn(ImageCacheControl.FORCE_CACHE)

    fetcher.fetch(fetchState, callback)

    verify(okHttpClient, times(1)).newCall(anyOrNull(Request::class.java))
    verify(okHttpClient).newCall(capture(requestArgumentCaptor))

    val capturedRequest = requestArgumentCaptor.value

    // Check if the capturedRequest is not null
    if (capturedRequest != null) {
      // Print all fields of the Request object using reflection
      println("Captured Request Fields:")
      capturedRequest.javaClass.declaredFields.forEach { field ->
        field.isAccessible = true // Make sure we can access private fields
        println("${field.name}: ${field.get(capturedRequest)}")
      }
    } else {
      println("Captured Request is null")
    }
  }
}
