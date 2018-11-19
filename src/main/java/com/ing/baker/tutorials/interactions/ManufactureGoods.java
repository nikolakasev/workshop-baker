package com.ing.baker.tutorials.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;
import com.ing.baker.tutorials.interactions.events.ManufactureGoodsEvents;
import com.ing.baker.tutorials.interactions.events.ManufactureGoodsEvents.GoodsManufactured;

public interface ManufactureGoods extends Interaction {

    @FiresEvent(oneOf = {GoodsManufactured.class})
    ManufactureGoodsEvents.ManufactureOutcome apply(@RequiresIngredient("order") String order) throws Exception;
}