package com.facebook.react.devsupport

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.facebook.react.devsupport.interfaces.DevSupportManager
import com.facebook.react.devsupport.interfaces.RedBoxHandler

internal class ModernRedBoxContentView(context: Context) : LinearLayout(context) {

  private var devSupportManager: DevSupportManager? = null
  private var redBoxHandler: RedBoxHandler? = null
  private var redBackgroundColor = Color.parseColor("#F35369")
  private var errorTitleColor = Color.parseColor("#F35369")
  private var basePadding = 32

  init {
    orientation = VERTICAL
    setBackgroundColor(Color.parseColor("#474747"))

    val headerView =
            TextView(context).apply {
              setTextColor(Color.WHITE)
              textSize = 18f
              text = "Failed to compile"
              gravity = Gravity.CENTER
              setTypeface(typeface, android.graphics.Typeface.BOLD)
              setBackgroundColor(redBackgroundColor)
              setPadding(basePadding, 40, basePadding, 40)
            }
    addView(headerView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

    val titleAndMessageContainer =
            LinearLayout(context).apply {
              orientation = VERTICAL
              setBackgroundColor(Color.parseColor("#333333"))
              setPadding(basePadding, basePadding, basePadding, basePadding)

              val titleView =
                      TextView(context).apply {
                        setTextColor(errorTitleColor)
                        textSize = 18f
                        text = "Runtime Error"
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                      }
              addView(titleView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

              val messageView =
                      TextView(context).apply {
                        setTextColor(Color.WHITE)
                        textSize = 14f
                        text = "Something went wrong"
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                        setPadding(0, basePadding / 2, 0, basePadding / 2)
                      }
              addView(
                      messageView,
                      LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
              )
            }
    addView(
            titleAndMessageContainer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    )
  }

  fun setDevSupportManager(devSupportManager: DevSupportManager): ModernRedBoxContentView {
    this.devSupportManager = devSupportManager
    return this
  }

  fun setRedBoxHandler(@Nullable redBoxHandler: RedBoxHandler?): ModernRedBoxContentView {
    this.redBoxHandler = redBoxHandler
    return this
  }

  fun init() {
    // Placeholder for initialization logic if needed
  }

  fun refreshContentView() {
    val title = "Runtime Error"
    val message = devSupportManager?.lastErrorTitle ?: "Something went wrong"

    getChildAt(1).let {
      if (it is LinearLayout) {
        (it.getChildAt(0) as TextView).text = title
        (it.getChildAt(1) as TextView).text = message
      }
    }
  }

  fun resetReporting() {
    // No-op for now
  }
}
