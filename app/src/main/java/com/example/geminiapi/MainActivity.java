package com.example.geminiapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText ingredientInput;
    private Button generateButton, saveButton, shareButton   ;
    private TextView responseTextView;
    private ProgressBar progressBar;

    private static final String API_KEY = "AIzaSyBPIftpy28wVx-i5hx86N4Ipx6pkW4WJdE"; // Replace with your API key
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    private static final String PREFS_NAME = "SavedRecipes";
    private static final String RECIPE_KEY = "recipe_list"; // Updated key for multiple recipes

    private String generatedRecipe = ""; // Store the generated recipe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ingredientInput = findViewById(R.id.ingredientInput);
        generateButton = findViewById(R.id.generateButton);
        saveButton = findViewById(R.id.saveButton);
        shareButton = findViewById(R.id.shareButton);

        responseTextView = findViewById(R.id.responseTextView);
        progressBar = findViewById(R.id.progressBar);

        // Enable scrolling for the TextView
        responseTextView.setMovementMethod(new ScrollingMovementMethod());

        generateButton.setOnClickListener(view -> {
            String ingredients = ingredientInput.getText().toString().trim();
            if (!ingredients.isEmpty()) {
                fetchRecipe(ingredients);
            } else {
                Toast.makeText(MainActivity.this, "Please enter ingredients!", Toast.LENGTH_SHORT).show();
            }
        });
        Button createRecipeButton = findViewById(R.id.createRecipeButton);
        createRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateOwnRecipe.class);
            startActivity(intent);
        });

        Button yourRecipesButton = findViewById(R.id.yourRecipesButton);
        yourRecipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, YourRecipesActivity.class);
            startActivity(intent);
        });


        saveButton.setOnClickListener(view -> saveRecipe());
        shareButton.setOnClickListener(view -> shareRecipe());
    }

    private void fetchRecipe(String ingredients) {
        progressBar.setVisibility(View.VISIBLE);
        responseTextView.setText("");

        OkHttpClient client = new OkHttpClient();

        String jsonRequest = "{ \"contents\": [{ \"parts\": [{ \"text\": \"Generate a recipe with " + ingredients + "\" }] }] }";

        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    responseTextView.setText("Request failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    generatedRecipe = parseRecipe(responseBody);
                    runOnUiThread(() -> responseTextView.setText(generatedRecipe));
                } else {
                    runOnUiThread(() -> responseTextView.setText("Error: " + response.code()));
                }
            }
        });
    }

    private String parseRecipe(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray candidates = jsonObject.getJSONArray("candidates");

            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");

                if (parts.length() > 0) {
                    return parts.getJSONObject(0).getString("text");
                }
            }
        } catch (Exception e) {
            return "Error parsing response.";
        }
        return "No recipe found.";
    }

    private void saveRecipe() {
        if (generatedRecipe.isEmpty()) {
            Toast.makeText(this, "Generate a recipe first!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedRecipes = sharedPreferences.getStringSet(RECIPE_KEY, new HashSet<>());

        savedRecipes.add(generatedRecipe); // ✅ Add new recipe to the list

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(RECIPE_KEY, savedRecipes);
        editor.apply();

        Toast.makeText(this, "Recipe Saved!", Toast.LENGTH_SHORT).show();

        // Open Saved Recipes Activity
        Intent intent = new Intent(MainActivity.this, SavedRecipesActivity.class);
        startActivity(intent);
    }

    private void shareRecipe() {
        if (generatedRecipe.isEmpty()) {
            Toast.makeText(this, "Generate a recipe first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Delicious Recipe!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generatedRecipe);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Inflate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_saved_recipes) {
            Intent intent = new Intent(this, SavedRecipesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
