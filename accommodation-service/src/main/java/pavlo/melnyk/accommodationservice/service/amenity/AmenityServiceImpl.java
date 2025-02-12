package pavlo.melnyk.accommodationservice.service.amenity;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pavlo.melnyk.accommodationservice.mapper.AmenityMapper;
import pavlo.melnyk.accommodationservice.model.Amenity;
import pavlo.melnyk.accommodationservice.repository.amenity.AmenityRepository;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    public Amenity findOrCreateByName(final String name) {
        return amenityRepository.findByName(name)
                .orElseGet(() -> amenityRepository.save(amenityMapper.mapFromName(name)));
    }

    @Override
    public Set<Amenity> findOrCreateByNames(final Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(this::findOrCreateByName)
                .collect(Collectors.toSet());
    }
}
