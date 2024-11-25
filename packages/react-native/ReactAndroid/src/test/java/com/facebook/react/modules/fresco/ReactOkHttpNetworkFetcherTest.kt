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
import org.junit.Before
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
  private lateinit var okHttpClient: OkHttpClient
  private lateinit var dispatcher: Dispatcher
  private lateinit var executorService: ExecutorService
  private lateinit var fetcher: ReactOkHttpNetworkFetcher
  private lateinit var fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState
  private lateinit var callback: NetworkFetcher.Callback
  private lateinit var producerContext: ProducerContext
  private lateinit var imageRequest: ReactNetworkImageRequest

  @Captor private lateinit var requestArgumentCaptor: ArgumentCaptor<Request>

  @Before
  fun prepareModules() {
    okHttpClient = mock(OkHttpClient::class.java)
    dispatcher = mock(Dispatcher::class.java)
    executorService = mock(ExecutorService::class.java)
    requestArgumentCaptor = ArgumentCaptor.forClass(Request::class.java)

    whenever(okHttpClient.dispatcher).thenReturn(dispatcher)
    whenever(dispatcher.executorService).thenReturn(executorService)
    whenever(okHttpClient.newCall(anyOrNull(Request::class.java))).thenAnswer {
      val callMock = mock(Call::class.java)
      callMock
    }

    fetcher = ReactOkHttpNetworkFetcher(okHttpClient)
    fetchState = mock(OkHttpNetworkFetcher.OkHttpNetworkFetchState::class.java)
    callback = mock(NetworkFetcher.Callback::class.java)

    val mockUri = Uri.parse("http://www.facebook.com")
    whenever(fetchState.uri).thenReturn(mockUri)

    producerContext = mock(ProducerContext::class.java)
    imageRequest = mock(ReactNetworkImageRequest::class.java)

    whenever(fetchState.context).thenReturn(producerContext)
    whenever(producerContext.imageRequest).thenReturn(imageRequest)
  }

  @Test
  fun testFetch() {
    whenever(imageRequest.cacheControl).thenReturn(ImageCacheControl.ONLY_IF_CACHED)
    whenever(imageRequest.headers).thenReturn(null)

    fetcher.fetch(fetchState, callback)

    verify(okHttpClient, times(1)).newCall(anyOrNull(Request::class.java))
    verify(okHttpClient).newCall(capture(requestArgumentCaptor))

    val capturedRequest = requestArgumentCaptor.value

    val cacheControlInRequest = capturedRequest.cacheControl
    assert(cacheControlInRequest != null) { "CacheControl is null in the Request" }

    println("CacheControl Details:")
    println("noCache: ${cacheControlInRequest.noCache}")
    println("noStore: ${cacheControlInRequest.noStore}")
    println("maxAgeSeconds: ${cacheControlInRequest.maxAgeSeconds}")
    println("maxStaleSeconds: ${cacheControlInRequest.maxStaleSeconds}")
    println("onlyIfCached: ${cacheControlInRequest.onlyIfCached}")
    println("noTransform: ${cacheControlInRequest.noTransform}")
    println("immutable: ${cacheControlInRequest.immutable}")

    val requestUri = capturedRequest.url
    println("Request URL: $requestUri")

    if (capturedRequest != null) {
      println("Captured Request Fields:")
      capturedRequest.javaClass.declaredFields.forEach { field ->
        field.isAccessible = true // Make sure we can access private fields
        println("${field.name}: ${field.get(capturedRequest)}")
      }
    } else {
      println("Captured Request is null")
    }
  }

  // TODO: this can be used to add more test cases for the headers only.
  // Create headers as a JavaOnlyMap
  // val headers = JavaOnlyMap()
  // headers.putString("key1", "value1")
  // headers.putString("key2", "value2")

  // // Set the headers on the imageRequest
  // whenever(imageRequest.headers).thenReturn(headers)
}
