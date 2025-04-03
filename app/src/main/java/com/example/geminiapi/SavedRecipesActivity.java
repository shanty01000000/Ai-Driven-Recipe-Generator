package com.example.geminiapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavedRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private List<String> recipeList;
    private Button backButton;
    private TextView noRecipesText;

    private static final String PREFS_NAME = "SavedRecipes";
    private static final String RECIPE_KEY = "recipe_list";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        noRecipesText = findViewById(R.id.noRecipesText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSavedRecipes();

        adapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(adapter);

        backButton.setOnClickListener(view -> finish());
    }

    private void loadSavedRecipes() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedRecipes = sharedPreferences.getStringSet(RECIPE_KEY, new HashSet<>());

        recipeList = new ArrayList<>(savedRecipes);

        if (recipeList.isEmpty()) {
            noRecipesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noRecipesText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Inflate menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle menu item clicks for navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_saved_recipes) {
            startActivity(new Intent(this, SavedRecipesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
