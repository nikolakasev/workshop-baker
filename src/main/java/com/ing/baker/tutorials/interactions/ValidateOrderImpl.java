package com.ing.baker.tutorials.interactions;

import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents;
import com.ing.baker.tutorials.interactions.events.ValidateOrderEvents.ValidateOrderOutcome;

public class ValidateOrderImpl implements ValidateOrder {
    @Override
    public ValidateOrderOutcome apply(String order) throws Exception {
        System.out.println("Validating order...");
        return new ValidateOrderEvents.OrderValid();
    }
}
