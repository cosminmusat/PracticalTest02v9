package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.AsyncTask;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PracticalTest02MainActivityv9 extends AppCompatActivity {

    private EditText wordInput;
    private EditText minLengthInput;
    private TextView resultText;
    private final Executor executor = Executors.newSingleThreadExecutor();
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
            new FetchAnagramsTask().executeOnExecutor(executor, word, String.valueOf(minLength));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private class FetchAnagramsTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "FetchAnagramsTask";

        @Override
        protected String doInBackground(String... params) {
            String word = params[0];
            int minLength = Integer.parseInt(params[1]);
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

                JSONObject jsonObject = new JSONObject(json.toString());
                JSONArray anagrams = jsonObject.getJSONArray("all");

                for (int i = 0; i < anagrams.length(); i++) {
                    String anagram = anagrams.getString(i);
                    if (anagram.length() >= minLength) {
                        result.append(anagram).append("\n");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching anagrams", e);
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            resultText.setText(result);
        }
    }
}