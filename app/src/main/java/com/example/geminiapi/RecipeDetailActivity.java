package com.example.geminiapi;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView recipeDetailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipe Details");
        }

        recipeDetailText = findViewById(R.id.recipeDetailText);

        // Get recipe data from intent
        String recipeData = getIntent().getStringExtra("recipeData");
        if (recipeData != null) {
            displayRecipeDetails(recipeData);
        }
    }

    private void displayRecipeDetails(String recipeData) {
        // Recipe format: "Name##Ingredients##Steps"
        String[] parts = recipeData.split("##");
        if (parts.length == 3) {
            String displayText = "🍽️ Name: " + parts[0] + "\n\n" +
                    "🥦 Ingredients: \n" + parts[1] + "\n\n" +
                    "👨‍🍳 Steps: \n" + parts[2];
            recipeDetailText.setText(displayText);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
