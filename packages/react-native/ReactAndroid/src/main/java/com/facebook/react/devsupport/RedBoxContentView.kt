/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:Suppress("DEPRECATION") // AsyncTask needs to be migrated to coroutines

package com.facebook.react.devsupport

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.text.SpannedString
import android.text.method.LinkMovementMethod
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.infer.annotation.Assertions
import com.facebook.react.R
import com.facebook.react.devsupport.interfaces.DevSupportManager
import com.facebook.react.devsupport.interfaces.RedBoxHandler
import com.facebook.react.devsupport.interfaces.RedBoxHandler.ReportCompletedListener
import com.facebook.react.devsupport.interfaces.StackFrame

/** Dialog for displaying JS errors in an eye-catching form (red box). */
public class RedBoxContentView public constructor(context: Context?) :
        LinearLayout(context), OnItemClickListener {
  private var mRedBoxHandler: RedBoxHandler? = null
  private var mDevSupportManager: DevSupportManager? = null
  private var mStackView: ListView? = null
  private var mReloadJsButton: Button? = null
  private var mDismissButton: Button? = null
  private var mReportButton: Button? = null
  private var mReportTextView: TextView? = null
  private var mLoadingIndicator: ProgressBar? = null
  private var mLineSeparator: View? = null
  private var isReporting = false

  private val mReportCompletedListener: ReportCompletedListener =
          object : ReportCompletedListener {
            override fun onReportSuccess(spannedString: SpannedString?) {
              isReporting = false
              Assertions.assertNotNull(mReportButton).isEnabled = true
              Assertions.assertNotNull(mLoadingIndicator).visibility = GONE
              Assertions.assertNotNull(mReportTextView).text = spannedString
            }

            override fun onReportError(spannedString: SpannedString?) {
              isReporting = false
              Assertions.assertNotNull(mReportButton).isEnabled = true
              Assertions.assertNotNull(mLoadingIndicator).visibility = GONE
              Assertions.assertNotNull(mReportTextView).text = spannedString
            }
          }

  private val mReportButtonOnClickListener = OnClickListener { view ->
    if (mRedBoxHandler == null || !mRedBoxHandler!!.isReportEnabled() || isReporting) {
      return@OnClickListener
    }
    isReporting = true
    Assertions.assertNotNull(mReportTextView).text = "Reporting..."
    Assertions.assertNotNull(mReportTextView).visibility = VISIBLE
    Assertions.assertNotNull(mLoadingIndicator).visibility = VISIBLE
    Assertions.assertNotNull(mLineSeparator).visibility = VISIBLE
    Assertions.assertNotNull(mReportButton).isEnabled = false

    val title = Assertions.assertNotNull(mDevSupportManager?.lastErrorTitle)
    val stack = Assertions.assertNotNull(mDevSupportManager?.lastErrorStack)
    val sourceUrl = mDevSupportManager?.sourceUrl
    mRedBoxHandler?.reportRedbox(
            view.context,
            title,
            stack!!,
            sourceUrl!!,
            Assertions.assertNotNull(mReportCompletedListener)
    )
  }

  private class StackAdapter(private val mTitle: String, private val mStack: Array<StackFrame>) :
          BaseAdapter() {
    private class FrameViewHolder(v: View) {
      val mMethodView: TextView = v.findViewById<View>(R.id.rn_frame_method) as TextView
      val mFileView: TextView = v.findViewById<View>(R.id.rn_frame_file) as TextView
    }

    init {
      Assertions.assertNotNull(mTitle)
      Assertions.assertNotNull(mStack)
    }

    override fun areAllItemsEnabled(): Boolean {
      return false
    }

    override fun isEnabled(position: Int): Boolean {
      return position > 0
    }

    override fun getCount(): Int {
      return mStack.size + 1
    }

    override fun getItem(position: Int): Any {
      return if (position == 0) mTitle else mStack[position - 1]
    }

    override fun getItemId(position: Int): Long {
      return position.toLong()
    }

    override fun getViewTypeCount(): Int {
      return VIEW_TYPE_COUNT
    }

    override fun getItemViewType(position: Int): Int {
      return if (position == 0) VIEW_TYPE_TITLE else VIEW_TYPE_STACKFRAME
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      var convertView = convertView
      if (position == 0) {
        val title =
                if (convertView != null) convertView as TextView
                else
                        (LayoutInflater.from(parent.context)
                                .inflate(R.layout.redbox_item_title, parent, false) as
                                TextView)
        // Remove ANSI color codes from the title
        val titleSafe = (mTitle ?: "<unknown title>")
        title.text = titleSafe.replace("\\x1b\\[[0-9;]*m".toRegex(), "")
        return title
      } else {
        if (convertView == null) {
          convertView =
                  LayoutInflater.from(parent.context)
                          .inflate(R.layout.redbox_item_frame, parent, false)
          convertView.tag = FrameViewHolder(convertView)
        }
        val frame = mStack[position - 1]
        val holder = convertView.tag as FrameViewHolder
        holder.mMethodView.text = frame.method
        holder.mFileView.text = StackTraceHelper.formatFrameSource(frame)
        holder.mMethodView.setTextColor(if (frame.isCollapsed) -0x555556 else Color.WHITE)
        holder.mFileView.setTextColor(if (frame.isCollapsed) -0x7f7f80 else -0x4c4c4d)
        return convertView
      }
    }

    companion object {
      private const val VIEW_TYPE_COUNT = 2
      private const val VIEW_TYPE_TITLE = 0
      private const val VIEW_TYPE_STACKFRAME = 1
    }
  }

  public fun setDevSupportManager(devSupportManager: DevSupportManager?): RedBoxContentView {
    mDevSupportManager = devSupportManager
    return this
  }

  public fun setRedBoxHandler(redBoxHandler: RedBoxHandler?): RedBoxContentView {
    mRedBoxHandler = redBoxHandler
    return this
  }

  public fun init() {
    LayoutInflater.from(context).inflate(R.layout.redbox_view, this)

    mStackView = findViewById<View>(R.id.rn_redbox_stack) as ListView
    mStackView?.onItemClickListener = this

    mReloadJsButton = findViewById<View>(R.id.rn_redbox_reload_button) as Button
    mReloadJsButton?.setOnClickListener {
      Assertions.assertNotNull(mDevSupportManager).handleReloadJS()
    }
    mDismissButton = findViewById<View>(R.id.rn_redbox_dismiss_button) as Button
    mDismissButton?.setOnClickListener {
      Assertions.assertNotNull(mDevSupportManager).hideRedboxDialog()
    }

    if (mRedBoxHandler != null && mRedBoxHandler!!.isReportEnabled()) {
      mLoadingIndicator = findViewById<View>(R.id.rn_redbox_loading_indicator) as ProgressBar
      mLineSeparator = findViewById(R.id.rn_redbox_line_separator) as View
      mReportTextView = findViewById<View>(R.id.rn_redbox_report_label) as TextView
      mReportTextView?.movementMethod = LinkMovementMethod.getInstance()
      mReportTextView?.highlightColor = Color.TRANSPARENT
      mReportButton = findViewById<View>(R.id.rn_redbox_report_button) as Button
      mReportButton?.setOnClickListener(mReportButtonOnClickListener)
    }
  }

  public fun setExceptionDetails(title: String, stack: Array<StackFrame>) {
    mStackView?.adapter = StackAdapter(title, stack)
  }

  /** Show the report button, hide the report textview and the loading indicator. */
  public fun resetReporting() {
    if (mRedBoxHandler == null || !mRedBoxHandler!!.isReportEnabled()) {
      return
    }
    isReporting = false
    Assertions.assertNotNull(mReportTextView).visibility = GONE
    Assertions.assertNotNull(mLoadingIndicator).visibility = GONE
    Assertions.assertNotNull(mLineSeparator).visibility = GONE
    Assertions.assertNotNull(mReportButton).visibility = VISIBLE
    Assertions.assertNotNull(mReportButton).isEnabled = true
  }

  override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
    OpenStackFrameTask(Assertions.assertNotNull(mDevSupportManager))
            .executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    mStackView?.adapter?.getItem(position) as StackFrame
            )
  }

  /** Refresh the content view with latest errors from dev support manager */
  public fun refreshContentView() {
    val message = mDevSupportManager?.lastErrorTitle
    val stack = mDevSupportManager?.lastErrorStack
    val errorType = mDevSupportManager?.lastErrorType
    val errorInfo = mDevSupportManager?.processErrorCustomizers(Pair.create(message, stack))
    setExceptionDetails(errorInfo!!.first, errorInfo.second)

    // JS errors are reported here after source mapping.
    val redBoxHandler = mDevSupportManager?.redBoxHandler
    if (redBoxHandler != null) {
      redBoxHandler.handleRedbox(message, stack!!, errorType!!)
      resetReporting()
    }
  }
}
