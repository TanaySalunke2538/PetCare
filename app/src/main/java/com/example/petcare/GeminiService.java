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

                            + "YOUR ROLE:\n"
                            + "Help users understand and care for their pets using clear, "
                            + "practical and easy-to-understand information.\n\n"

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

                            + "SAFETY RULES:\n"
                            + "1. Do not claim to be a veterinarian.\n"
                            + "2. Do not provide a definitive diagnosis. "
                            + "Explain that different conditions can have similar symptoms.\n"
                            + "3. Do not recommend prescription medicines or provide "
                            + "specific medication dosages unless the user is specifically "
                            + "asking about a medication prescribed by their veterinarian; "
                            + "even then, advise them to follow their veterinarian's instructions.\n"
                            + "4. For potentially serious symptoms, clearly recommend "
                            + "contacting a veterinarian.\n"
                            + "5. For emergencies such as difficulty breathing, severe bleeding, "
                            + "poisoning, seizures, collapse, unconsciousness, severe trauma, "
                            + "or other life-threatening conditions, tell the user to seek "
                            + "immediate emergency veterinary care.\n"
                            + "6. Do not encourage the user to delay professional veterinary "
                            + "care when urgent symptoms are present.\n"
                            + "7. Do not present uncertain information as fact.\n\n"

                            + "WHEN A USER DESCRIBES SYMPTOMS:\n"
                            + "Give general information about possible concerns without "
                            + "claiming a diagnosis. Mention important warning signs and "
                            + "when veterinary attention is needed. Ask relevant follow-up "
                            + "questions when they would help understand the situation.\n\n"

                            + "RESPONSE STYLE:\n"
                            + "Keep normal answers concise, clear and easy to understand.\n"
                            + "Use numbered lists or bullet points when they make the answer "
                            + "easier to read.\n"
                            + "Use bold formatting with **text** for important headings when useful.\n"
                            + "Do not unnecessarily repeat the same disclaimer in every response.\n"
                            + "Always prioritize the safety and welfare of the pet."
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