package com.example.geminiapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class YourRecipesActivity extends AppCompatActivity {

    private ListView recipeListView;
    private ArrayAdapter<String> adapter;
    private List<String> recipeList;

    private static final String PREFS_NAME = "UserRecipes";
    private static final String RECIPE_LIST_KEY = "recipe_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_recipes);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Recipes");
        }

        // Initialize ListView
        recipeListView = findViewById(R.id.recipeListView);
        recipeList = new ArrayList<>();

        // Load saved recipes
        loadRecipes();

        // Set up adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeList);
        recipeListView.setAdapter(adapter);

        // Click to view detailed recipe
        recipeListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRecipe = recipeList.get(position);
            openRecipeDetail(selectedRecipe);
        });

        // Long click to delete a recipe
        recipeListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String recipeToDelete = recipeList.get(position);
            deleteRecipe(recipeToDelete);
            return true;
        });
    }

    // Load recipes from SharedPreferences
    private void loadRecipes() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedRecipes = sharedPreferences.getStringSet(RECIPE_LIST_KEY, new HashSet<>());

        if (savedRecipes.isEmpty()) {
            Toast.makeText(this, "No saved recipes found!", Toast.LENGTH_SHORT).show();
        } else {
            recipeList.addAll(savedRecipes);
        }
    }

    // Open Recipe Detail Activity
    private void openRecipeDetail(String recipeData) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("recipeData", recipeData);
        startActivity(intent);
    }

    // Delete a recipe
    private void deleteRecipe(String recipeToDelete) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedRecipes = new HashSet<>(sharedPreferences.getStringSet(RECIPE_LIST_KEY, new HashSet<>()));

        if (savedRecipes.remove(recipeToDelete)) {
            sharedPreferences.edit().putStringSet(RECIPE_LIST_KEY, savedRecipes).apply();
            recipeList.remove(recipeToDelete);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Recipe deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Navigate back on toolbar arrow click
        return true;
    }
}
