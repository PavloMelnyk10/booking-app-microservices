package pavlo.melnyk.bookingservice.service.discount;

import java.math.BigDecimal;

public class GoldDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, int completedBookings) {
        return totalAmount.multiply(BigDecimal.valueOf(0.10));
    }
}
