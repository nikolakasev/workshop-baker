package com.ing.baker.tutorials;

import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.Recipe;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class WebShopRecipeTest {
    private final Recipe recipe = WebShopRecipe.getRecipe();

    @Test
    public void shouldHaveNoValidationErrors() {
        CompiledRecipe compileRecipe = RecipeCompiler.compileRecipe(recipe);
        Assert.assertEquals(Collections.emptyList(), compileRecipe.getValidationErrors());
    }
}