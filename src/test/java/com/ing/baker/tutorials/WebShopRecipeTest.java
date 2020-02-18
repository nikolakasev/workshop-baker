package com.ing.baker.tutorials;

import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableList;
import com.ing.baker.compiler.RecipeCompiler;
import com.ing.baker.il.CompiledRecipe;
import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.runtime.javadsl.Baker;
import com.ing.baker.runtime.javadsl.EventInstance;
import com.ing.baker.runtime.javadsl.InteractionInstance;
import com.ing.baker.tutorials.interactions.ManufactureGoods;
import com.ing.baker.tutorials.interactions.SendInvoice;
import com.ing.baker.tutorials.interactions.ShipGoods;
import com.ing.baker.tutorials.interactions.ValidateOrder;
import com.ing.baker.tutorials.interactions.events.ManufactureGoodsEvents.GoodsManufactured;
import com.ing.baker.tutorials.interactions.events.SendInvoiceEvents.InvoiceSent;
import com.ing.baker.tutorials.interactions.events.SensoryEvents;
import com.ing.baker.tutorials.interactions.events.ShipGoodsEvents.GoodsShipped;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents.OrderValid;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class WebShopRecipeTest {
    private final Recipe recipe = WebShopRecipe.getRecipe();

    private final ValidateOrder mockValidateOrder = mock(ValidateOrder.class);
    private final ManufactureGoods mockManufactureGoods = mock(ManufactureGoods.class);
    private final ShipGoods mockShipGoods = mock(ShipGoods.class);
    private final SendInvoice mockSendInvoice = mock(SendInvoice.class);

    //Baker spins an actor system based on AKKA under the hood
    private ActorSystem testActorSystem = ActorSystem.create("WebShop");
    private Baker baker = Baker.akkaLocalDefault(testActorSystem);

    //Baker can run multiple recipes at the same time, each recipe gets a unique recipeId
    private String recipeId;

    //happy flow mock data
    private final String orderId = "ORD-123456";
    private final String customerInfo = "John Doe, Amsterdam";
    private final String goods = "this is the product";

    //the good thing about this is that no actual implementations are needed
    //the complete flow can be verified that it will work, speeds up the validation of orchestration logic
    private List<InteractionInstance> mockImplementations = ImmutableList.of(
            InteractionInstance.from(mockValidateOrder),
            InteractionInstance.from(mockManufactureGoods),
            InteractionInstance.from(mockShipGoods),
            InteractionInstance.from(mockSendInvoice));

    public WebShopRecipeTest() throws ExecutionException, InterruptedException {
        CompiledRecipe compiledRecipe = RecipeCompiler.compileRecipe(recipe);
        baker.addInteractionInstances(mockImplementations);
        recipeId = baker.addRecipe(compiledRecipe).get();
    }

    @Before
    public void setupTest() {
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
    //ingredients not provided will cause validation errors for example
    public void shouldHaveNoValidationErrors() {
        CompiledRecipe compileRecipe = RecipeCompiler.compileRecipe(recipe);
        //this is a sound recipe and it's guaranteed that it will work
        Assert.assertEquals(Collections.emptyList(), compileRecipe.getValidationErrors());
    }

    @Test
    //Baker will check that it has implementations of all interactions in the recipe
    public void shouldBakeTheRecipe() throws Exception {
        String processId = UUID.randomUUID().toString();

        //create an instance of the recipe (flow) for each customer and give it a unique id
        baker.bake(recipeId, processId);
        Map<String, Value> accumulatedState = baker.getIngredients(processId).get();
        assert (accumulatedState != null);
    }

    @Test
    //Baker will check that it has implementations of all interactions in the recipe
    public void shouldExecuteHappyFlowCorrectly() throws Exception {
        setupMocks();

        String processId = UUID.randomUUID().toString();
        baker.bake(recipeId, processId);

        //blocks the current thread until all interactions that can be called have been executed by Baker
        //this is useful when an underlying system gives information back that must be returned in the response from the API or in unit-tests
        baker.fireEventAndResolveWhenCompleted(processId, EventInstance.from(new SensoryEvents.CustomerInfoReceived(customerInfo))).get();
        verify(mockValidateOrder, never()).apply(anyString());
        verify(mockManufactureGoods, never()).apply(anyString());
        verify(mockShipGoods, never()).apply(anyString(), anyString());
        verify(mockManufactureGoods, never()).apply(anyString());
        verify(mockSendInvoice, never()).apply(anyString());

        baker.fireEventAndResolveWhenCompleted(processId, EventInstance.from(new SensoryEvents.OrderPlaced(orderId))).get();
        verify(mockValidateOrder).apply(orderId);
        verify(mockManufactureGoods, never()).apply(anyString());
        verify(mockShipGoods, never()).apply(anyString(), anyString());
        verify(mockSendInvoice, never()).apply(anyString());

        baker.fireEventAndResolveWhenCompleted(processId, EventInstance.from(new SensoryEvents.PaymentMade())).get();
        verify(mockManufactureGoods).apply(orderId);
        verify(mockShipGoods).apply(goods, customerInfo);
        verify(mockSendInvoice).apply(customerInfo);

        verify(mockValidateOrder, times(1)).apply(orderId);
        verify(mockManufactureGoods, times(1)).apply(orderId);
        verify(mockShipGoods, times(1)).apply(goods, customerInfo);
        verify(mockSendInvoice, times(1)).apply(customerInfo);
    }

    private void setupMocks() throws Exception {
        when(mockValidateOrder.apply(orderId)).thenReturn(new OrderValid());
        when(mockManufactureGoods.apply(orderId)).thenReturn(new GoodsManufactured(goods));
        when(mockShipGoods.apply(goods, customerInfo)).thenReturn(new GoodsShipped());
        when(mockSendInvoice.apply(customerInfo)).thenReturn(new InvoiceSent());
    }

    private void saveVisualizationAsSvg(final String dotGraph) throws IOException {
        final File file = new File("./target/" + "web-shop-recipe.svg");
        final MutableGraph g = Parser.read(dotGraph);
        //try Format.SVG for a vector format, better for printed recipes
        Graphviz.fromGraph(g).render(Format.SVG).toFile(file);
        System.out.println("Exported graph here: " + file.getAbsolutePath());
    }
}