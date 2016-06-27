package com.example.liveview;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

public class livestream {

	public static native void init(String url, SurfaceView sf);

	public static native boolean setSurfaceView(SurfaceView s);

	public static native void setVideoPath(String url);

	public static native void free();

	public static native int playVideo();

	public static native void stopVideo();

	public static native void OnDestroy();

	public static native int getLiveViewStatus();

	public static native int getLiveViewStatus2();

	public static native void setCacheTime(int cache_time_in);

	public static native void sdlResume();

	public static native void sdlPause();

	//public static native void nativePause();
	//public static native void nativeResume();
	public static native void onNativeSurfaceChanged();

	public static native void setEmptyTime(int empty_time_in);

	private static boolean mIsPaused;
	private static Surface mSurface;
	private ViEAndroidGLES20 mGlView = null;
	private String mUrl = null;
	private static Timer mTimer;
	private static Handler mHandler;

	protected static AudioTrack mAudioTrack;

	static {
		mHandler = null;
		mIsPaused = false;
		mTimer = new Timer();
		//stateListeners = new ArrayList<PlayerStateListener>();
		System.loadLibrary("liveview");
	}

	public livestream() {
		//livestream.mAudioTrack = null;
	}

	public TaskSubThread CreateTaskThread() {
		return new TaskSubThread();
	}

	public void On_Create(String sUrl, ViEAndroidGLES20 GlViewIn) {
		mGlView = GlViewIn;
		mUrl = sUrl;
	}

	public class TaskSubThread implements Runnable {

		public Handler mHandler = null;

		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler() {
				public void handleMessage(Message msg) {

					int iMsg = msg.what;

					switch (iMsg) {

					case 0:
						Log.v("app_livestream", "startplay");
						livestream.initialize(mUrl, mGlView);
						//livestream.setSurfaceView(mGlView);
						//livestream.playVideo();
						break;
					case 1:
						livestream.setSurfaceView(mGlView);
						livestream.playVideo();
						break;
					case 2:
						livestream.stopVideo();
						Log.v("app_livestream", "StopVideo");
						break;
					case 3:
						livestream.free();
						Log.v("app_livestream", "freeVideo");
						break;
					}
				}
			};
			Looper.loop();
		}

	}

	public static void initialize(String url, SurfaceView surface)
	// public static void initialize(String url, Surface surface) 
	{
		mIsPaused = false;
		//mSurface = surface;
		init(url, surface);
	}

	/** Called by onPause or surfaceDestroyed. Even if surfaceDestroyed
	 *  is the first to be called, mIsSurfaceReady should still be set
	 *  to 'true' during the call to onPause (in a usual scenario).
	 */
	public static void handlePause() {
		if (!mIsPaused) {
			mIsPaused = true;
			sdlPause();
		}
	}

	/** Called by onResume or surfaceCreated. An actual resume should be done only when the surface is ready.
	 * Note: Some Android variants may send multiple surfaceChanged events, so we don't need to resume
	 * every time we get one of those events, only if it comes after surfaceDestroyed
	 */
	public static void handleResume() {
		if (mIsPaused) {
			mIsPaused = false;
			sdlPause();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					sdlResume();
				}
			}, 100);
		}
	}

	public static void setPlayerStateHandler(Handler handler) {
		mHandler = handler;
	}

	public static Surface getNativeSurface() {
		return mSurface;
	}

	//����״̬�ı�ʱ��ͨ��ýӿ�֪ͨAPP
	/*
	 * typedef enum { STAT_NULL = 0, STAT_INIT = 1, STAT_CONNECTING = 2,
	 * STAT_CONNECTED = 3, STAT_PLAYING = 6, STAT_DISCONNECTED = 7, STAT_STOPPED
	 * = 9, STAT_FREE = 10, STAT_NO_DATA = 11, }LIVE_STAT;
	 */
	public static void onPlayerStateChanged(int state) {
		switch (state) {

		case 0:
			//showStat = @"st:Not init";
			break;
		case 1:
			// showStat = @"st:Inited";

			break;
		case 6:
			// showStat = @"st:Playing";
			break;
		case 9:
			// showStat = @"st:Stopped";
			break;
		case 7:
			//  showStat = @"st:Disconnected";
			break;
		case 3:
			// showStat = @"st:Connected";
			break;
		case 10:
			//  showStat = @"st:Freeded";
			break;
		case 11:
			//  showStat = @"st:Empty data";
			break;
		case 2:
			//  showStat = @"st:Connecting";
			break;
		default:
			//  showStat = @"err";
			break;

		}

		if (mHandler != null) {
			mHandler.sendEmptyMessage(state);
			//mHandler.send
		}
	}

	//------------------------------------------audio------------------------------------------------------
	// audio 
	public static int audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
		//int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int channelConfig = isStereo ? AudioFormat.CHANNEL_OUT_STEREO : AudioFormat.CHANNEL_OUT_MONO;
		int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
		int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

		Log.w("SDL_APP", " SDL audio: wanted" + (isStereo ? "stereo" : "mono ") + " " + (is16Bit ? " 16-bit" : "8-bit") + " " + (sampleRate / 1000f) + "kHz, "
				+ desiredFrames + " frames buffer");

		// Let the user pick a largbuffer if they really want -- but ye
		// gods they probably shouldn't, the minimums are horrifyingly high        
		// latency already

		desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);
		if (mAudioTrack == null) {//
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);
			// Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
			// Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
			// Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()

			if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
				Log.e("SDL_APP", "Failed during initialization of Audio Track");
				mAudioTrack = null;
				return -1;
			}

			mAudioTrack.play();
		}

		Log.v("SDL_APP", "SDL audio: got" + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " "
				+ ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + (mAudioTrack.getSampleRate() / 1000f)
				+ "kHz, " + desiredFrames + " frames buffer");
		return 0;
	}

	// audio 
	public static void audioWriteShortBuffer(short[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom                	
				}
			} else {
				Log.w("SDL_APP", "SDL  audio:error return fr write(short)");
				return;
			}
		}
	}

	//write audio data
	public static void audioWriteByteBuffer(byte[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
					//Log.w("SDL_APP", "SDL audio: error return from write(byte)");
				}
			} else {
				Log.w("SDL_APP", "SDL audio: error return from write(byte)");
				return;
			}
		}

	}

	//audio quit 
	public static void audioQuit() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			Log.w("SDL", "SDL audio:quit");
			mAudioTrack = null;
		}

	}
	//-----------------------------audio end-----------------------------------------------
}