package com.idemia.pocidemiacarabineros.services;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OCRPlateService {


    public void processPlate(final Activity activity, final String filename, final AsynchronousTask callback) {

        // TODO: Enviar a seccion parametros -- VERSION TRIAL
        String APIKEY = "780bca411cfa9e5d2a23e2b81f1c3389c3905f7c";
        String serverUrl = "https://api.platerecognizer.com/v1/plate-reader/";

        AsyncTask.execute(() -> {
            try {

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("upload", filename,
                                RequestBody.create(MediaType.parse("application/octet-stream"),
                                        new File(filename)))
                        .addFormDataPart("regions", "cl")
                        .build();

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS).build();

                Request request = new Request.Builder()
                        .url(serverUrl)
                        .post(body)
                        .addHeader("Authorization", "Token 780bca411cfa9e5d2a23e2b81f1c3389c3905f7c")
                        .addHeader("Content-Type", "multipart/form-data;")
                        .build();

                Call call = client.newCall(request);

                call.enqueue(new Callback() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String mesage = "";
                        try {
                            String jsonData = response.body().string();
                            JSONObject Jobject = null;
                            String messageOcr = "";
                            System.out.println("JJPP Message" + jsonData);
                            Jobject = new JSONObject(jsonData);

                            JSONArray jsonArry = Jobject.getJSONArray("results");
                            if (jsonArry.length() > 0) {
                                JSONObject jsonRes = (JSONObject) jsonArry.get(0);
                                System.out.println("i::" + jsonRes.toString());
                                if (jsonRes.has("plate")) {
                                    String plate = jsonRes.get("plate").toString();
                                    String score = jsonRes.get("score").toString();
                                    System.out.println("plate" + plate);
                                    System.out.println("score" + score);
                                    messageOcr = plate;

                                } else {
                                    if (jsonRes.has("error")) {
                                        messageOcr = jsonRes.get("error").toString();
                                    } else {
                                        messageOcr = "No hay placa";
                                    }
                                }
                            } else {
                                messageOcr = "No hay placa";
                            }
                            messageOcr = messageOcr.toUpperCase();
                            callback.onReceiveResults(messageOcr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onFailure(mesage);
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        });
    }

}
