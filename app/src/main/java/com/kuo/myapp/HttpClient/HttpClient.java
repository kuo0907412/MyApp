package com.kuo.myapp.HttpClient;

import android.util.Log;

import com.kuo.myapp.Config.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HttpClient {

	// HTTP Method
	public static final String GET = "GET";
	public static final String POST = "POST";


	public static final String TAG_SUCCESS = "HTTP ACCESS SUCCESSFUL";
	public static final String TAG_FAILURE = "HTTP ACCESS FAILURE";

	private static HttpClientInterface HCInterFace;
	/**
	 * GETメソッドを発行する
	 * メソッドパラメータとか無視してそのままURLを踏む仕様
	 * */
	public static byte[] getByteArrayFromURL(String strUrl,HttpClientInterface receiver,HashMap<String, String> parameter) {
		HCInterFace = receiver;
		byte[] byteArray = new byte[1024];
		byte[] result = null;
		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		int size;
        String parameterString;

        if(parameter.size() != 0) {
            parameterString = parseRequestData(parameter, GET);
            strUrl += parameterString;
        }
        try {
            URL url = new URL(strUrl);

            con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Accept-Language", "ja");
			con.setConnectTimeout(Config.CONNECTION_TIMEOUT);
			con.setReadTimeout(Config.READ_TIMEOUT);
			con.connect();

            int responseCode = con.getResponseCode();
            if(responseCode / 100 == 4 || responseCode / 100 == 5){
                in = con.getErrorStream();
            }else{
                in = con.getInputStream();
            }

			out = new ByteArrayOutputStream();
			long total = 0;
			int fileLength = con.getContentLength();
			while ((size = in.read(byteArray)) != -1) {
				total += size;
				out.write(byteArray, 0, size);
				if(HCInterFace != null){
					HCInterFace.HttpClientProgress((float)total/(float)fileLength);
				}

			}
			result = out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
        } finally {
			try {
				if (con != null)
					con.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * HTTPでPOSTパラメータを発行するメソッド
	 * */
	public static byte[] getByteArrayFromUrlPost(String strUrl, HashMap<String, String> parameter,HttpClientInterface receiver,String type)
			throws MalformedURLException {
        if(type !=null) {
            if (type.equals("GET")) {
                return getByteArrayFromURL(strUrl, receiver, parameter);
            }
        }

		HCInterFace = receiver;

		byte[] byteArray = new byte[1024];
		byte[] result = null;
		int size;

		HttpURLConnection con = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			URL url = new URL(strUrl);

			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(Config.CONNECTION_TIMEOUT);
			con.setReadTimeout(Config.READ_TIMEOUT);
			con.setRequestMethod("POST");
			con.setRequestProperty("Accept-Language", "ja");
			con.setDoOutput(true);
			PrintWriter printWriter = new PrintWriter(con.getOutputStream());

            // Post値の設定
            if(parameter.size() != 0) {
                String parameterString = parseRequestData(parameter, POST);
                printWriter.print(parameterString);
            }
			printWriter.close();

			con.connect();

            int responseCode = con.getResponseCode();
            if(responseCode / 100 == 4 || responseCode / 100 == 5){
                in = con.getErrorStream();
            }else{
                in = con.getInputStream();
            }

			out = new ByteArrayOutputStream();
			long total = 0;
			int fileLength = con.getContentLength();
			while ((size = in.read(byteArray)) != -1) {
				total += size;
				out.write(byteArray, 0, size);
				if(HCInterFace != null){
					HCInterFace.HttpClientProgress((float)total/(float)fileLength);
				}
			}
			result = out.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.disconnect();
				if (in != null)
					in.close();
				if (out != null)
					out.close();

			} catch (IOException ignore) {
			}
		}

		return result;
	}

	/**
	 * HashMapからリクエストパラメータの形式にParseする
	 * */
	/**
	 * HashMapからリクエストパラメータの形式にParseする
	 * @author kimura_kouki
	 * @since 2011/11/20
	 * */
	private static String parseRequestData(HashMap<String, String> request,
										   String method) {

		ArrayList<String> ele = new ArrayList<String>();
		Set<String> keys = null;

		// HashMapからごっそりKeyValueを回収する。
		if (!request.isEmpty()) {
			keys = request.keySet();
		}

		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = request.get(key);
			ele.add(key + "=" + value);
		}

		// KeyValueを接続する
		StringBuffer data = new StringBuffer();
		for (Iterator<String> iterator = ele.iterator(); iterator.hasNext();) {
			String kv = (String) iterator.next();
			data.append(kv);

			if (iterator.hasNext()) {
				data.append("&");
			}
		}

		// 形式を合わせる
		if (method.equals(GET)) {
			data.insert(0, "?");
		}
		if (method.equals(POST)) {
		}
		if(Config.DEBUG)
			Log.d("response",data.toString());
		return data.toString();
	}
	public interface HttpClientInterface {
		void HttpClientProgress(float progress);
	}
}