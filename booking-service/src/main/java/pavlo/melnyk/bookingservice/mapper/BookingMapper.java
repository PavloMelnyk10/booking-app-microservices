package pavlo.melnyk.bookingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pavlo.melnyk.bookingservice.config.MapperConfig;
import pavlo.melnyk.bookingservice.dto.booking.BookingDto;
import pavlo.melnyk.bookingservice.dto.booking.CreateBookingRequestDto;
import pavlo.melnyk.bookingservice.model.Booking;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "dailyRate", ignore = true)
    @Mapping(target = "hasPayment", ignore = true)
    Booking toModel(CreateBookingRequestDto requestDto);

    BookingDto toDto(Booking booking);
}
