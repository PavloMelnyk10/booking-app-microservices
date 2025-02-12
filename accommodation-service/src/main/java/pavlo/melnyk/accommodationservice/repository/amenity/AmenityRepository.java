package pavlo.melnyk.accommodationservice.repository.amenity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pavlo.melnyk.accommodationservice.model.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByName(String name);
}
