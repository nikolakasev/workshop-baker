package com.ing.baker.tutorials;

import com.ing.baker.recipe.javadsl.Recipe;
import com.ing.baker.tutorials.interactions.ValidateOrder;

import static com.ing.baker.recipe.javadsl.InteractionDescriptor.of;

class WebShopRecipe {
    static Recipe getRecipe() {
        //TODO implement the recipe according to the sequence diagram in docs/sequence.png
        return new Recipe("WebShop")
                .withInteraction(of(ValidateOrder.class));
    }
}
