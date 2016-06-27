package com.bqt.test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

/**AsyncHttp访问网络的工具类*/
public class AsyncHttpHelper {
	public static final int HTTP_OK = 200;
	public static final String HTTP_ERROR = "系统繁忙，请稍后再试";

	//**************************************************************************************************************************
	//	                                                                                                   最核心的两个方法
	//**************************************************************************************************************************

	/**
	 * get 请求
	 */
	public static void get(final String serverName, final String relativeUrl, RequestParams params, final OnHttpListener<JSONObject> onHttpListner) {
		if (params == null) params = new RequestParams();
		AsyncHttpClient asyncHttpClient = new com.loopj.android.http.AsyncHttpClient();
		asyncHttpClient.setTimeout(5000);
		try {
			String url = "http://" + serverName + relativeUrl;
			asyncHttpClient.get(url, params, new JsonHttpResponseHandler("UTF-8") {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					throwable.printStackTrace();
					if (onHttpListner != null) onHttpListner.onHttpListener(false, getErrorJson(HTTP_ERROR));
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					if (onHttpListner != null) {
						if (statusCode == HTTP_OK) {
							response = (response == null ? new JSONObject() : response);
							onHttpListner.onHttpListener(true, response);
						} else onHttpListner.onHttpListener(false, response);
					}
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					super.onSuccess(statusCode, headers, responseString);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (onHttpListner != null) onHttpListner.onHttpListener(false, getErrorJson(HTTP_ERROR));
		}
	}

	/**
	 * post 请求
	 */
	public static void post(final String serverName, final String relativeUrl, RequestParams params, final OnHttpListener<JSONObject> onHttpListner) {
		if (params == null) params = new RequestParams();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.setTimeout(5000);
		try {
			String url = "http://" + serverName + relativeUrl;
			asyncHttpClient.post(url, params, new JsonHttpResponseHandler("UTF-8") {
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					super.onFailure(statusCode, headers, responseString, throwable);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
					super.onFailure(statusCode, headers, throwable, errorResponse);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
					throwable.printStackTrace();
					if (onHttpListner != null) onHttpListner.onHttpListener(false, getErrorJson(HTTP_ERROR));
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
					super.onSuccess(statusCode, headers, response);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					if (onHttpListner != null) {
						if (statusCode == HTTP_OK) {
							response = (response == null ? new JSONObject() : response);
							onHttpListner.onHttpListener(true, response);
						} else onHttpListner.onHttpListener(false, response);
					}
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					super.onSuccess(statusCode, headers, responseString);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (onHttpListner != null) onHttpListner.onHttpListener(false, getErrorJson(HTTP_ERROR));
		}
	}

	//**************************************************************************************************************************
	//	                                                                                              其他访问服务器的方法
	//**************************************************************************************************************************
	/**
	 * get请求，返回String
	 */
	public static void get_AbsoluteUrl_String(final String url, RequestParams params, final OnHttpListener<String> onHttpListner) {
		if (params == null) params = new RequestParams();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.setTimeout(5000);
		try {
			asyncHttpClient.get(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (onHttpListner != null) onHttpListner.onHttpListener(false, arg2 == null ? "error" : new String(arg2));
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if (arg0 == HTTP_OK) {
						if (onHttpListner != null) onHttpListner.onHttpListener(true, arg2 == null ? "error" : new String(arg2));
					} else {
						if (onHttpListner != null) onHttpListner.onHttpListener(false, arg2 == null ? "error" : new String(arg2));
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (onHttpListner != null) onHttpListner.onHttpListener(false, "");
		}
	}

	/**
	 * get请求，返回JSONArray
	 */
	public static void get_AbsoluteUrl_JSONArray(final String absoulteUrl, RequestParams params, final OnHttpListener<JSONArray> onHttpListner) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.setTimeout(5000);
		asyncHttpClient.get(absoulteUrl, params, new JsonHttpResponseHandler("UTF-8") {
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
				if (onHttpListner != null) {
					if (statusCode == HTTP_OK) {
						response = (JSONArray) (response == null ? (new JSONObject()) : response);
						onHttpListner.onHttpListener(true, response);
					} else onHttpListner.onHttpListener(false, response);
				}
			}
		});
	}

	/**
	 * https post请求
	 */
	public static void httpsPost(String url, RequestParams params, final OnHttpListener<String> onHttpListener) {
		if (params == null) params = new RequestParams();
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		KeyStore trustStore = null;
		try {
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		}
		try {
			trustStore.load(null, null);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (CertificateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		MySSLSocketFactory socketFactory = null;
		try {
			socketFactory = new MySSLSocketFactory(trustStore);
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (UnrecoverableKeyException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		}
		socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		asyncHttpClient.setSSLSocketFactory(socketFactory);
		asyncHttpClient.setTimeout(5000);
		try {
			asyncHttpClient.post(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					if (onHttpListener != null) onHttpListener.onHttpListener(false, arg2 == null ? "error" : new String(arg2));
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if (arg0 == HTTP_OK) {
						if (onHttpListener != null) onHttpListener.onHttpListener(true, arg2 == null ? "error" : new String(arg2));
					} else {
						if (onHttpListener != null) onHttpListener.onHttpListener(false, arg2 == null ? "error" : new String(arg2));
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (onHttpListener != null) onHttpListener.onHttpListener(false, "");
		}
	}

	//**************************************************************************************************************************
	//	                                                                                                       其他方法
	//**************************************************************************************************************************
	/**
	 * 根据错误信息生成相应的json对象
	 */
	public static JSONObject getErrorJson(String error) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("error", error);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 添加ky-value到Requestparams
	 */
	public static void addRequestParam(RequestParams params, String key, String value) {
		if (params == null) params = new RequestParams();
		params.add(key, value);
	}

	/**
	 * 设置持久化保存cookie
	 */
	public static void saveCookie(Context context) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore cookieStore = new PersistentCookieStore(context);
		asyncHttpClient.setCookieStore(cookieStore);
	}

	public interface OnHttpListener<T> {
		public void onHttpListener(boolean httpSuccessed, T obj);
	}
}