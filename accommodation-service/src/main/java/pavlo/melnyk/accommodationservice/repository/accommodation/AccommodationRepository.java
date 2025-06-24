package pavlo.melnyk.accommodationservice.repository.accommodation;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pavlo.melnyk.accommodationservice.model.Accommodation;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    @EntityGraph(attributePaths = "amenities")
    Optional<Accommodation> findWithAmenitiesById(Long id);

    boolean existsByNameAndAddress_CountryAndAddress_CityAndAddress_Street(String name, String country, String city, String street);

    boolean existsByNameAndAddress_CountryAndAddress_CityAndAddress_StreetAndIdNot(String name, String country, String city, String street, Long id);
}
