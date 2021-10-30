package refactoring.app.chapter11.removeFlagArgument;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class Example {

    public LocalDateTime rushDeliveryDate(Order order) {
        return deliveryDate(order, true);
    }

    public LocalDateTime regularDeliveryDate(Order order) {
        return deliveryDate(order, false);
    }

    private LocalDateTime deliveryDate(Order order, boolean isRush) {
        int deliveryTime;

        if (Stream.of("MA", "CT")
                .anyMatch(state -> order.deliveryState.equals(state))) {
            deliveryTime = isRush ? 1 : 2;
        }
        else if (Stream.of("NY", "NH")
                .anyMatch(state -> order.deliveryState.equals(state))) {
            deliveryTime = 2;
            if (order.deliveryState.equals("NH") && !isRush) {
                deliveryTime = 3;
            }
        }
        else if (isRush) {
            deliveryTime = 3;
        }
        else if (order.deliveryState.equals("ME")) {
            deliveryTime = 3;
        }
        else {
            deliveryTime = 4;
        }
        LocalDateTime result = order.placeOn.plusDays(2 + deliveryTime);
        if (isRush) result = result.minusDays(1);
        return result;
    }
}
