/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.devsupport;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.R;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.devsupport.interfaces.ErrorType;
import com.facebook.react.devsupport.interfaces.RedBoxHandler;
import com.facebook.react.devsupport.interfaces.RedBoxHandler.ReportCompletedListener;
import com.facebook.react.devsupport.interfaces.StackFrame;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONObject;

import com.facebook.react.devsupport.OpenStackFrameTask;

/* internal */ class OpenStackFrameTask extends AsyncTask<StackFrame, Void, Void> {
  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private final DevSupportManager mDevSupportManager;

  public OpenStackFrameTask(DevSupportManager devSupportManager) {
    mDevSupportManager = devSupportManager;
  }

  private static JSONObject stackFrameToJson(StackFrame frame) {
    return new JSONObject(MapBuilder.of("file", frame.getFile(), "methodName", frame.getMethod(), "lineNumber", frame.getLine(), "column", frame.getColumn()));
  }

  @Override
  protected Void doInBackground(StackFrame... stackFrames) {
    try {
      String openStackFrameUrl = Uri.parse(mDevSupportManager.getSourceUrl()).buildUpon().path("/open-stack-frame").query(null).build().toString();
      OkHttpClient client = new OkHttpClient();
      for (StackFrame frame : stackFrames) {
        String payload = stackFrameToJson(frame).toString();
        RequestBody body = RequestBody.create(JSON, payload);
        Request request = new Request.Builder().url(openStackFrameUrl).post(body).build();
        client.newCall(request).execute();
      }
    } catch (Exception e) {
      FLog.e(ReactConstants.TAG, "Could not open stack frame", e);
    }
    return null;
  }
}
