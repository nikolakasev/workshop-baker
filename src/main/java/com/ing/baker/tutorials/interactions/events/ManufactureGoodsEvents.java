package com.ing.baker.tutorials.interactions.events;

import lombok.Value;

public class ManufactureGoodsEvents {
    public interface ManufactureOutcome {
    }

    @Value
    public static class GoodsManufactured implements ManufactureOutcome {
        String goods;
    }
}
