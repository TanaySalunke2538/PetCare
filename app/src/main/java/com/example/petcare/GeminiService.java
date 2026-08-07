package com.example.petcare;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    private final OkHttpClient client = new OkHttpClient();

    public interface GeminiCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    public void askGemini(String userMessage, GeminiCallback callback) {

        try {

            String prompt =
                    "You are PetCare AI.\n\n" +

                            "Only answer questions related to pets.\n" +

                            "Topics include:\n" +
                            "- Dogs\n" +
                            "- Cats\n" +
                            "- Birds\n" +
                            "- Fish\n" +
                            "- Rabbits\n" +
                            "- Vaccination\n" +
                            "- Nutrition\n" +
                            "- Grooming\n" +
                            "- Pet behaviour\n" +
                            "- First aid\n\n" +

                            "If the question is unrelated, politely explain that you only assist with pet care.\n\n" +

                            "Never pretend to be a veterinarian.\n\n" +

                            "If symptoms indicate an emergency such as poisoning, seizures, severe bleeding, collapse, or difficulty breathing, advise immediate veterinary care.\n\n" +

                            "Keep answers short (3-6 sentences).\n\n" +

                            "User Question:\n" + userMessage;

            JSONObject body = new JSONObject();

            JSONArray contents = new JSONArray();

            JSONObject content = new JSONObject();

            JSONArray parts = new JSONArray();

            JSONObject part = new JSONObject();

            part.put("text", prompt);

            parts.put(part);

            content.put("parts", parts);

            contents.put(content);

            body.put("contents", contents);

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + Constants.MODEL
                            + ":generateContent?key="
                            + Constants.GEMINI_API_KEY;

            RequestBody requestBody =
                    RequestBody.create(
                            body.toString(),
                            MediaType.parse("application/json"));

            Request request =
                    new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    callback.onError(e.getMessage());

                }

                @Override
                public void onResponse(Call call, Response response)
                        throws IOException {

                    try {

                        String result = response.body().string();

                        android.util.Log.d("GEMINI_RESPONSE", result);

                        if (!response.isSuccessful()) {
                            callback.onError(result);
                            return;
                        }

                        JSONObject object = new JSONObject(result);

                        String reply =

                                object.getJSONArray("candidates")
                                        .getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text");

                        callback.onSuccess(reply);

                    } catch (Exception e) {

                        callback.onError(e.getMessage());

                    }

                }

            });

        } catch (Exception e) {

            callback.onError(e.getMessage());

        }

    }

}