package com.dreamfish.com.autocalc.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;




public class HttpUtils {

  
  public static JSONObject httpGetJson(String url) throws IOException {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(url).build();

    Response response = client.newCall(request).execute();
    String result = response.body().string();
    return JSON.parseObject(result);
  }
}
