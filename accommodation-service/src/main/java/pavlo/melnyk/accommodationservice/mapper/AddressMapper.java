package pavlo.melnyk.accommodationservice.mapper;

import org.mapstruct.Mapper;
import pavlo.melnyk.accommodationservice.config.MapperConfig;
import pavlo.melnyk.accommodationservice.dto.AddressDto;
import pavlo.melnyk.accommodationservice.model.Address;

@Mapper(config = MapperConfig.class)
public interface AddressMapper {
    Address toModel(AddressDto addressDto);

    AddressDto toDto(Address address);
}
