package com.ing.baker.tutorials;

import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.tutorials.interactions.ManufactureGoods;
import com.ing.baker.tutorials.interactions.SendInvoice;
import com.ing.baker.tutorials.interactions.ShipGoods;
import com.ing.baker.tutorials.interactions.ValidateOrder;
import com.ing.baker.tutorials.interactions.events.SensoryEvents.CustomerInfoReceived;
import com.ing.baker.tutorials.interactions.events.SensoryEvents.OrderPlaced;
import com.ing.baker.tutorials.interactions.events.SensoryEvents.PaymentMade;
import com.ing.baker.tutorials.interactions.events.ShipGoodsEvents.GoodsShipped;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents.OrderValid;

import static com.ing.baker.recipe.javadsl.InteractionDescriptor.of;

class WebShopRecipe {
    static Recipe getRecipe() {
        return new Recipe("WebShopRecipe")
                .withInteractions(
                        of(ValidateOrder.class),
                        of(ManufactureGoods.class)
                                .withRequiredEvents(OrderValid.class, PaymentMade.class),
                        of(ShipGoods.class),
                        of(SendInvoice.class)
                                .withRequiredEvent(GoodsShipped.class))
                .withSensoryEvents(
                        OrderPlaced.class,
                        CustomerInfoReceived.class,
                        PaymentMade.class);
    }
}
