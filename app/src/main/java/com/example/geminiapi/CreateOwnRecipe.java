package com.example.geminiapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.HashSet;
import java.util.Set;

public class CreateOwnRecipe extends AppCompatActivity {

    private EditText recipeNameInput, recipeIngredientsInput, recipeStepsInput;
    private Button saveRecipeButton;

    private static final String PREFS_NAME = "UserRecipes";
    private static final String RECIPE_LIST_KEY = "recipe_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_own_recipe);

        // Initialize Views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Your Recipe");
        }

        recipeNameInput = findViewById(R.id.recipeNameInput);
        recipeIngredientsInput = findViewById(R.id.recipeIngredientsInput);
        recipeStepsInput = findViewById(R.id.recipeStepsInput);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);

        // Save Button Click Listener
        saveRecipeButton.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        // Retrieve user inputs
        String name = recipeNameInput.getText().toString().trim();
        String ingredients = recipeIngredientsInput.getText().toString().trim();
        String steps = recipeStepsInput.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || ingredients.isEmpty() || steps.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save recipe to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> recipeSet = new HashSet<>(sharedPreferences.getStringSet(RECIPE_LIST_KEY, new HashSet<>()));

        // Store in format: "Name##Ingredients##Steps"
        recipeSet.add(name + "##" + ingredients + "##" + steps);
        sharedPreferences.edit().putStringSet(RECIPE_LIST_KEY, recipeSet).apply();

        Toast.makeText(this, "Recipe Saved Successfully!", Toast.LENGTH_SHORT).show();

        // Redirect to Recipe List Page
        startActivity(new Intent(this, YourRecipesActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Navigate back on toolbar arrow click
        return true;
    }
}
