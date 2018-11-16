package com.ing.baker.tutorials.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents.OrderInvalid;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents.OrderValid;

public interface ValidateOrder extends Interaction {

    @FiresEvent(oneOf = {OrderValid.class, OrderInvalid.class})
    ValidateOrderEvents.ValidateOrderOutcome apply(@RequiresIngredient("order") String order) throws Exception;
}