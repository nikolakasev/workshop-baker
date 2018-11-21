package com.ing.baker.tutorials;

import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableList;
import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.runtime.java_api.JBaker;
import com.ing.baker.tutorials.interactions.ManufactureGoods;
import com.ing.baker.tutorials.interactions.SendInvoice;
import com.ing.baker.tutorials.interactions.ShipGoods;
import com.ing.baker.tutorials.interactions.ValidateOrder;
import com.ing.baker.types.Value;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

public class WebShopRecipeTest {
    private final Recipe recipe = WebShopRecipe.getRecipe();

    private final ValidateOrder mockValidateOrder = mock(ValidateOrder.class);
    private final ManufactureGoods mockManufactureGoods = mock(ManufactureGoods.class);
    private final ShipGoods mockShipGoods = mock(ShipGoods.class);
    private final SendInvoice mockSendInvoice = mock(SendInvoice.class);

    //Baker spins an actor system based on AKKA under the hood
    private ActorSystem testActorSystem = ActorSystem.create("WebShop");
    private JBaker baker = new JBaker(testActorSystem);

    //Baker can run multiple recipes at the same time, each recipe gets a unique recipeId
    private String recipeId;

    //the good thing about this is that no actual implementations are needed
    //the complete flow can be verified that it will work, speeds up the validation of orchestration logic
    private List<Object> mockImplementations = ImmutableList.<Object>of(
            mockValidateOrder,
            mockManufactureGoods,
            mockShipGoods,
            mockSendInvoice);

    public WebShopRecipeTest() {
        CompiledRecipe compiledRecipe = RecipeCompiler.compileRecipe(recipe);
        baker.addImplementations(mockImplementations);
        recipeId = baker.addRecipe(compiledRecipe);
    }

    @Before
    public void setupTest() throws Exception {
        for (Object mockImplementation : mockImplementations) {
            if (mockImplementation instanceof Mock)
                reset(mockImplementation);
        }
    }

    @After
    public void stopActorSystem() throws TimeoutException, InterruptedException {
        Await.ready(testActorSystem.terminate(), Duration.apply(20, TimeUnit.SECONDS));
    }

    @Test
    public void shouldVisualiseTheRecipe() throws IOException {
        CompiledRecipe compiledRecipe = RecipeCompiler.compileRecipe(recipe);
        String visualRecipe = compiledRecipe.getRecipeVisualization();
        saveVisualizationAsSvg(visualRecipe);
    }

    @Test
    //Baker will check that it has implementations of all interactions in the recipe
    public void shouldBakeTheRecipe() throws Exception {
        UUID processId = UUID.randomUUID();

        //create an instance of the recipe (flow) for each customer and give it a unique id
        baker.bake(recipeId, processId);
        Map<String, Value> accumulatedState = baker.getIngredients(processId);
        assert (accumulatedState != null);
    }

    @Test
    //ingredients not provided will cause validation errors for example
    public void shouldHaveNoValidationErrors() {
        CompiledRecipe compileRecipe = RecipeCompiler.compileRecipe(recipe);
        //this is a sound recipe and it's guaranteed that it will work
        Assert.assertEquals(Collections.emptyList(), compileRecipe.getValidationErrors());
    }

    private void saveVisualizationAsSvg(final String dotGraph) throws IOException {
        final File file = new File("./target/" + "web-shop-recipe.svg");
        final MutableGraph g = Parser.read(dotGraph);
        //try Format.SVG for a vector format, better for printed recipes
        Graphviz.fromGraph(g).render(Format.SVG).toFile(file);
        System.out.println("Exported graph here: " + file.getAbsolutePath());
    }
}