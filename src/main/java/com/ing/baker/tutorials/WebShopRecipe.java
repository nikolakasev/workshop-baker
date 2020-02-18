package com.ing.baker.tutorials;

import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.tutorials.interactions.ValidateOrder;

import static com.ing.baker.recipe.javadsl.InteractionDescriptor.of;

class WebShopRecipe {
    static Recipe getRecipe() {
        return new Recipe("WebShopRecipe")
                .withInteraction(
                        of(ValidateOrder.class));
    }
}
