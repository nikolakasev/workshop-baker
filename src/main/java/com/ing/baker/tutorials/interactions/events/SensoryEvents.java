package com.ing.baker.tutorials.interactions.events;

import lombok.Data;

public final class SensoryEvents {
    @Data
    public static final class OrderPlaced {
        private final String order;
    }

    @Data
    public static final class CustomerInfoReceived {
        private final String customerInfo;
    }

    @Data
    public static final class PaymentMade {

    }
}
