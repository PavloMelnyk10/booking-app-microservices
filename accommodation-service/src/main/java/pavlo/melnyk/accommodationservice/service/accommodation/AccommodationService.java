package pavlo.melnyk.accommodationservice.service.accommodation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationAvailabilityDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationSummaryDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.CreateAccommodationRequestDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.UpdateAccommodationRequestDto;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    Page<AccommodationSummaryDto> findAll(Pageable pageable);

    AccommodationDto findById(Long id);

    AccommodationAvailabilityDto findAvailabilityById(Long id);

    void deleteById(Long id);

    AccommodationDto update(Long id, UpdateAccommodationRequestDto requestDto);
}
