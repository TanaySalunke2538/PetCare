package com.example.petcare;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

    public void askGemini(
            ArrayList<Message> conversation,
            GeminiCallback callback) {

        try {

            JSONObject body = new JSONObject();

            JSONArray contents = new JSONArray();

            // System instruction
            JSONObject systemInstruction = new JSONObject();

            JSONArray systemParts = new JSONArray();

            JSONObject systemText = new JSONObject();

            systemText.put(
                    "text",
                    "You are PetCare AI, an AI assistant inside a pet care application.\n\n"
                            + "Your purpose is to help users with pet-related questions.\n\n"

                            + "You can discuss:\n"
                            + "- Dogs\n"
                            + "- Cats\n"
                            + "- Birds\n"
                            + "- Fish\n"
                            + "- Rabbits\n"
                            + "- Pet nutrition\n"
                            + "- Vaccination\n"
                            + "- Grooming\n"
                            + "- Pet behaviour\n"
                            + "- General pet care\n"
                            + "- Basic first aid information\n\n"

                            + "Do not pretend to be a veterinarian and do not provide a definitive diagnosis.\n\n"

                            + "For emergencies such as difficulty breathing, severe bleeding, poisoning, "
                            + "seizures, collapse, or serious injury, advise the user to seek immediate "
                            + "veterinary care.\n\n"

                            + "Keep normal answers concise, clear and easy to understand."
            );

            systemParts.put(systemText);

            systemInstruction.put("parts", systemParts);

            body.put("system_instruction", systemInstruction);


            // Conversation history
            for (Message message : conversation) {

                JSONObject content = new JSONObject();

                JSONArray parts = new JSONArray();

                JSONObject part = new JSONObject();

                part.put("text", message.getMessage());

                parts.put(part);

                content.put("parts", parts);

                if (message.isUser()) {
                    content.put("role", "user");
                } else {
                    content.put("role", "model");
                }

                contents.put(content);
            }

            body.put("contents", contents);


            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + Constants.MODEL
                            + ":generateContent?key="
                            + Constants.GEMINI_API_KEY;


            RequestBody requestBody =
                    RequestBody.create(
                            body.toString(),
                            MediaType.parse("application/json")
                    );


            Request request =
                    new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();


            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(
                        Call call,
                        IOException e) {

                    callback.onError(e.getMessage());
                }


                @Override
                public void onResponse(
                        Call call,
                        Response response)
                        throws IOException {

                    String result =
                            response.body().string();

                    android.util.Log.d(
                            "GEMINI_RESPONSE",
                            result
                    );


                    if (!response.isSuccessful()) {

                        callback.onError(result);

                        return;
                    }


                    try {

                        JSONObject object =
                                new JSONObject(result);

                        String reply =
                                object
                                        .getJSONArray("candidates")
                                        .getJSONObject(0)
                                        .getJSONObject("content")
                                        .getJSONArray("parts")
                                        .getJSONObject(0)
                                        .getString("text");


                        callback.onSuccess(reply);

                    } catch (Exception e) {

                        callback.onError(
                                e.getMessage()
                        );
                    }
                }
            });


        } catch (Exception e) {

            callback.onError(
                    e.getMessage()
            );
        }
    }

}