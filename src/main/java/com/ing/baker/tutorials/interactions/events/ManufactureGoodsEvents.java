package com.ing.baker.tutorials.interactions.events;

import lombok.Value;

public class ManufactureGoodsEvents {
    public interface ManufactureOutcome {
    }

    @Value
    public static final class GoodsManufactured implements ManufactureOutcome {
        private final String goods;
    }
}
