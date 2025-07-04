package pavlo.melnyk.accommodationservice.service.notification;

import java.util.stream.Collectors;
import pavlo.melnyk.accommodationservice.model.Accommodation;
import pavlo.melnyk.accommodationservice.model.Address;
import pavlo.melnyk.accommodationservice.model.Amenity;

public final class MessageBuilder {
    private MessageBuilder() {
    }

    /* Accommodation */
    public static String buildAccommodationCreatedMessage(Accommodation accommodation) {
        String amenities = accommodation.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.joining(", "));

        return String.format(
                """
                        🏨 New Accommodation Created:
                        ID: %d
                        Name: %s
                        Location: %s
                        Amenities: %s
                        Daily rate: $%.2f
                        """,
                accommodation.getId(),
                accommodation.getName(),
                formatAddress(accommodation.getAddress()),
                amenities.isEmpty() ? "No amenities listed" : amenities,
                accommodation.getDailyRate()
        );
    }

    public static String buildAccommodationDeletedMessage(Accommodation accommodation) {
        return String.format(
                """
                        ❌ Accommodation Deleted:
                        ID: %d
                        Name: %s
                        """,
                accommodation.getId(),
                accommodation.getName()
        );
    }

    public static String buildAccommodationUpdatedMessage(Accommodation accommodation) {
        return String.format(
                """
                        ✏️ Accommodation Updated:
                        ID: %d
                        Name: %s
                        Location: %s
                        Price per Night: $%.2f
                        """,
                accommodation.getId(),
                accommodation.getName(),
                formatAddress(accommodation.getAddress()),
                accommodation.getDailyRate()
        );
    }

    private static String formatAddress(Address address) {
        if (address == null) {
            return "No address provided";
        }
        return String.format("%s, %s, %s, %s",
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getZipCode());
    }
}
