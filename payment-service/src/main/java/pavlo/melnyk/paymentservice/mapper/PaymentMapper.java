package pavlo.melnyk.paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pavlo.melnyk.paymentservice.config.MapperConfig;
import pavlo.melnyk.paymentservice.dto.PaymentDto;
import pavlo.melnyk.paymentservice.model.Payment;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface PaymentMapper {
    PaymentDto toDto(Payment payment);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Payment toModel(PaymentDto paymentDto);
}
