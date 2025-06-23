package pavlo.melnyk.accommodationservice.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import pavlo.melnyk.accommodationservice.config.MapperConfig;
import pavlo.melnyk.accommodationservice.model.Amenity;

@Mapper(config = MapperConfig.class, componentModel = "spring")
public interface AmenityMapper {

    default Amenity mapFromName(String name) {
        Amenity amenity = new Amenity();
        amenity.setName(name);
        return amenity;
    }

    default Set<Amenity> mapAmenities(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(this::mapFromName)
                .collect(Collectors.toSet());
    }
}
