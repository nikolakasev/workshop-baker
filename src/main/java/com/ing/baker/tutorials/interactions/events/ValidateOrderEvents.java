package com.ing.baker.tutorials.interactions.events;

public class ValidateOrderEvents {
    public interface ValidateOrderOutcome {
    }

    public static final class OrderValid implements ValidateOrderOutcome {
    }

    public static final class OrderInvalid implements ValidateOrderOutcome {
    }
}
