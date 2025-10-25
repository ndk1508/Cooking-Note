package com.recipe.cookingnote.model;

import java.util.List;

public class Food {
    private int id;
    private String name;
    private String imagePath;
    private String description;
    private List<String> ingredients;
    private List<String> steps;

    public Food(int id, String name, String imagePath, String description,
                List<String> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // Getter và Setter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
}
