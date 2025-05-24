package com.huytran.goodlife.pages.chat_bot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huytran.goodlife.pages.home.HomeActivity;
import com.huytran.goodlife.local_helper.LocaleHelper;
import com.huytran.goodlife.R;
import com.huytran.goodlife.adapter.ChatAdapter;
import com.huytran.goodlife.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatBotActivity extends AppCompatActivity {

    private static final String API_KEY = "1f33a014eb4f23712e3050d61291119edde7dec0a7d317405e72cddbbb0f25c2";  // Replace with your Together.ai key
    private final String ENDPOINT = "https://api.together.xyz/v1/chat/completions";
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient();
    private ImageButton sendButton, backButton;
    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        inputMessage = findViewById(R.id.inputMessage);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.back_button);

        adapter = new ChatAdapter(chatMessages, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnMessageEditListener((position, message) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatBotActivity.this);

            TextView title = new TextView(ChatBotActivity.this);
            title.setText("Sửa tin nhắn");
            title.setPadding(32, 32, 32, 32);
            title.setTextColor(Color.parseColor("#757575"));  // màu dịu
            title.setTextSize(20);
            builder.setCustomTitle(title);

            final EditText input = new EditText(ChatBotActivity.this);
            input.setText(message.message);
            input.setTextColor(Color.parseColor("#666666"));  // màu chữ dịu cho input
            builder.setView(input);

            builder.setPositiveButton("Gửi lại", (dialog, which) -> {
                String newText = input.getText().toString().trim();
                if (!newText.isEmpty()) {
                    message.message = newText;
                    chatMessages.set(position, message);
                    adapter.notifyItemChanged(position);

                    // Xoá câu trả lời cũ của bot nếu có
                    if (position + 1 < chatMessages.size() && !chatMessages.get(position + 1).isUser) {
                        chatMessages.remove(position + 1);
                        adapter.notifyItemRemoved(position + 1);
                    }

                    // Gửi lại
                    getAIReply(newText);
                }
            });

            builder.setNegativeButton("Hủy", null);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_bg);
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#757575"));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#757575"));
        });


        // Make status bar fully transparent
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));

        // Set the layout to extend into the status bar
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // thời gian chờ kết nối
                .readTimeout(30, TimeUnit.SECONDS)      // thời gian chờ phản hồi
                .writeTimeout(30, TimeUnit.SECONDS)     // thời gian gửi dữ liệu
                .build();


        sendButton.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(message, true);
                inputMessage.setText("");
                getAIReply(message);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatBotActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addMessage(String message, boolean isUser) {
        runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(message, isUser));
            adapter.notifyItemInserted(chatMessages.size() - 1);
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        });
    }

    private void getAIReply(String userInput) {
        try {
            JSONObject json = new JSONObject();
            json.put("model", "mistralai/Mixtral-8x7B-Instruct-v0.1");

            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "Bạn là chuyên gia dinh dưỡng thân thiện, luôn trả lời ngắn gọn, chính xác, bằng tiếng Việt. Không đưa ra thông tin kỳ lạ hay giả tưởng. Trả lời đúng trọng tâm.");
            messages.put(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userInput);
            messages.put(userMsg);

            json.put("messages", messages);
            json.put("max_tokens", 4000);
            json.put("temperature", 0.4);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(ENDPOINT)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    addMessage("Failed to connect: " + e.getMessage(), false);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    Log.d("TogetherAI", res);
                    try {
                        JSONObject obj = new JSONObject(res);
                        JSONArray choices = obj.getJSONArray("choices");
                        String reply = choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addMessage(reply.trim(), false);
                    } catch (Exception e) {
                        addMessage("Failed to parse reply", false);
                    }
                }
            });
        } catch (Exception e) {
            addMessage("Error: " + e.getMessage(), false);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.updateContextLocale(newBase, "vi")); // "en" or "vi"
    }

}