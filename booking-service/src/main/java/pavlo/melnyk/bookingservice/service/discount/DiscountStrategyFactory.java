package pavlo.melnyk.bookingservice.service.discount;

import org.springframework.stereotype.Component;

@Component
public class DiscountStrategyFactory {
    public DiscountStrategy getStrategy(int completedBookings) {
        if (completedBookings >= 10) {
            return new PlatinumDiscountStrategy();
        } else if (completedBookings >= 5) {
            return new GoldDiscountStrategy();
        } else if (completedBookings >= 2) {
            return new SilverDiscountStrategy();
        } else {
            return new BronzeDiscountStrategy();
        }
    }
}
