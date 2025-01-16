package ro.pub.cs.systems.eim.practicaltest02v9;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PracticalTest02MainActivityv9 extends AppCompatActivity {

    private EditText wordInput;
    private EditText minLengthInput;
    private TextView resultText;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v9_main);

        wordInput = findViewById(R.id.word_input);
        minLengthInput = findViewById(R.id.min_length_input);
        resultText = findViewById(R.id.result_text);
        Button searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String word = wordInput.getText().toString();
            int minLength = Integer.parseInt(minLengthInput.getText().toString());
            executorService.execute(new FetchAnagramsTask(word, minLength));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register the broadcast receiver with the RECEIVER_NOT_EXPORTED flag
        AnagramReceiver anagramReceiver = new AnagramReceiver();
        IntentFilter filter = new IntentFilter("ro.pub.cs.systems.eim.practicaltest02v9.ANAGRAMS");
        registerReceiver(anagramReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }

    private class FetchAnagramsTask implements Runnable {
        private static final String TAG = "FetchAnagramsTask";
        private final String word;
        private final int minLength;

        FetchAnagramsTask(String word, int minLength) {
            this.word = word;
            this.minLength = minLength;
        }

        @Override
        public void run() {
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("http://www.anagramica.com/all/" + word);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                Log.d(TAG, "Response from web service: " + json);

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray anagrams = jsonObject.getJSONArray("all");

                for (int i = 0; i < anagrams.length(); i++) {
                    String anagram = anagrams.getString(i);
                    if (anagram.length() >= minLength) {
                        result.append(anagram).append("\n");
                        Log.d(TAG, "Parsed anagram: " + anagram);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching anagrams", e);
            }

            // Send broadcast with the result
            Intent intent = new Intent("ro.pub.cs.systems.eim.practicaltest02v9.ANAGRAMS");
            intent.putExtra("anagrams", result.toString());
            sendBroadcast(intent);
        }
    }

    public static class AnagramReceiver extends BroadcastReceiver {
        private static final String TAG = "AnagramReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("anagrams")) {
                String anagrams = intent.getStringExtra("anagrams");
                Log.d(TAG, "Received anagrams: " + anagrams);
                // Update the UI
                TextView resultText = ((Activity) context).findViewById(R.id.result_text);
                resultText.setText(anagrams);
            }
        }
    }
}