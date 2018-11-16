package com.ing.baker.tutorials;

import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.Recipe;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class WebShopRecipeTest {
    private final Recipe recipe = WebShopRecipe.getRecipe();

    @Test
    public void shouldHaveNoValidationErrors() {
        CompiledRecipe compileRecipe = RecipeCompiler.compileRecipe(recipe);
        Assert.assertEquals(Collections.emptyList(), compileRecipe.getValidationErrors());
    }

    @Test
    public void shouldVisualiseWebShopRecipe() throws IOException {
        CompiledRecipe compiledRecipe = RecipeCompiler.compileRecipe(recipe);
        String visualRecipe = compiledRecipe.getRecipeVisualization();
        saveVisualizationAsSvg(visualRecipe);
    }

    private void saveVisualizationAsSvg(final String dotGraph) throws IOException {
        final File file = new File("./target/" + "web-shop-recipe.svg");
        final MutableGraph g = Parser.read(dotGraph);
        Graphviz.fromGraph(g).render(Format.SVG).toFile(file);
        System.out.println("Exported graph here: " + file.getAbsolutePath());
    }
}