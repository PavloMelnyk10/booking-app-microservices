package pavlo.melnyk.accommodationservice.service.amenity;

import java.util.Set;
import pavlo.melnyk.accommodationservice.model.Amenity;

public interface AmenityService {
    Amenity findOrCreateByName(String name);

    Set<Amenity> findOrCreateByNames(Set<String> names);
}

