package com.ing.baker.tutorials.interactions;

import com.ing.baker.recipe.annotations.FiresEvent;
import com.ing.baker.recipe.annotations.RequiresIngredient;
import com.ing.baker.recipe.javadsl.Interaction;
import com.ing.baker.tutorials.interactions.events.SendInvoiceEvents;
import com.ing.baker.tutorials.interactions.events.SendInvoiceEvents.InvoiceSent;

public interface SendInvoice extends Interaction {

    @FiresEvent(oneOf = {InvoiceSent.class})
    SendInvoiceEvents.InvoicingOutcome apply(@RequiresIngredient("customerInfo") String customerInfo) throws Exception;
}