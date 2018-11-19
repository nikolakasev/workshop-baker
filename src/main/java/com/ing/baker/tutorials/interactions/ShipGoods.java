package com.ing.baker.tutorials.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;
import com.ing.baker.tutorials.interactions.events.ShipGoodsEvents;
import com.ing.baker.tutorials.interactions.events.ShipGoodsEvents.GoodsShipped;

public interface ShipGoods extends Interaction {

    @FiresEvent(oneOf = {GoodsShipped.class})
    ShipGoodsEvents.ShippingOutcome apply(@RequiresIngredient("goods") String goods,
                                          @RequiresIngredient("customerInfo") String customerInfo) throws Exception;
}