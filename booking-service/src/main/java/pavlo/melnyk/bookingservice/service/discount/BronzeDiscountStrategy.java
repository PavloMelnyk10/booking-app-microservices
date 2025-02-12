package pavlo.melnyk.bookingservice.service.discount;

import java.math.BigDecimal;

public class BronzeDiscountStrategy implements DiscountStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, int completedBookings) {
        return BigDecimal.ZERO;
    }
}
