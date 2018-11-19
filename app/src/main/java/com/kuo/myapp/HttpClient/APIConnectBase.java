package com.kuo.myapp.HttpClient;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class APIConnectBase implements LoaderManager.LoaderCallbacks<String> {
    private APIConnectBaseListener listener;
    private APIConnectBaseListenerString strListener;
    private String URL;
    private ArrayList<PostParamSet> paramList = new ArrayList<>();
    private boolean showDialog;
    private Context mContext;
    private String type;

    public APIConnectBase(Context context) {
        // 初期化
        mContext = context;
    }

    // 生成時
    public APIConnectBase(Context context,APIConnectBaseListener listener,String URL) {
        // 初期化
        mContext = context;
        this.listener = listener;
        this.URL = URL;
    }

    public APIConnectBase(Context context,APIConnectBaseListenerString listener,String URL ) {
        // 初期化
        mContext = context;
        this.strListener = listener;
        this.URL = URL;
    }
    public void setType(String Type){
        this.type = Type;
    }
    public void setURL(String URL){
        this.URL = URL;
    }
    public void setListener(APIConnectBaseListener listener){
        this.listener = listener;
    }
    public void setStringListener(APIConnectBaseListenerString listener){
        this.strListener = listener;
    }
    /*
     * POSTパラメーター追加
     */
    public void putParam(String key ,String value){
        if(this.paramList == null){
            paramList = new ArrayList<>();
        }
        this.paramList.add(new PostParamSet(key,value));
    }

    public void clearParam(){
        if(this.paramList == null){
            paramList = new ArrayList<>();
        }else {
            this.paramList.clear();
        }
    }

    public void exec_post(final LoaderManager lm, final int id) {
        lm.initLoader(id,null,this);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        APIConnectBasePost loader = new APIConnectBasePost(mContext,URL,paramList,type);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(listener != null) {
            try {
                if (data != null) {

                    JSONObject rootObject = new JSONObject(data);
                    listener.onPostCompleted(rootObject);
                } else {
                    listener.onPostFailed(null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onPostFailed(data);
            }
        }else if(strListener != null) {
            if(data != null) {
                strListener.onPostCompleted(data);
            }else{
                strListener.onPostFailed(null);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    /**
     * ポストパラメータークラス
     */
    private class PostParamSet {
        private String key;
        private String param;

        PostParamSet(String key, String param) {
            this.key = key;
            this.param = param;
        }
    }

    public interface APIConnectBaseListener{
        void onPostCompleted(JSONObject response);
        void onPostFailed(String response);
    }

    public interface APIConnectBaseListenerString{
        void onPostCompleted(String response);
        void onPostFailed(String response);
    }

    static class APIConnectBasePost extends AsyncTaskLoader<String> implements HttpClient.HttpClientInterface {
        /**
         * 通信部分
         */
        String URL;
        String type;
        ArrayList<PostParamSet> paramList;
        APIConnectBasePost(Context context,String URL,ArrayList<PostParamSet> paramList,String Type){
            super(context);
            this.URL = URL;
            this.paramList = paramList;
            this.type = Type;
        }

        @Override
        public void HttpClientProgress(float progress) {
        }

        @Override
        public String loadInBackground() {
            HashMap<String, String> parameter = new HashMap<>();
            for (PostParamSet param : paramList) {
                parameter.put(param.key, param.param);
            }
            String result = null;
            try {
                byte[] resData = HttpClient.getByteArrayFromUrlPost(URL, parameter, this,type);
                //httpCode = H ttpClient.getHttpStatus(URL);
                if (resData == null) {
                    return null;
                }
                result = new String(resData, "UTF-8");
            } catch (UnsupportedEncodingException | MalformedURLException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
