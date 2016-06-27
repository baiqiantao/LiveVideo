package com.example.liveview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class ViERenderer {
	public static SurfaceView CreateRenderer(Context context) {
		Log.i("cr", "createRender");
		return CreateRenderer(context, false, null);
	}

	public static ViEAndroidGLES20 CreateRenderer(Context context, boolean useOpenGLES2, AttributeSet attr) {
		if (useOpenGLES2 == true && ViEAndroidGLES20.IsSupported(context)) {
			Log.i("a", "Create ");
			//return new ViEAndroidGLES20(context);
			return new ViEAndroidGLES20(context, attr);

		} else return null;
	}
}
