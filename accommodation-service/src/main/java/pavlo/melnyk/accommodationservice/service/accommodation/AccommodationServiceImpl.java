package pavlo.melnyk.accommodationservice.service.accommodation;

import static pavlo.melnyk.accommodationservice.service.notification.MessageBuilder.buildAccommodationCreatedMessage;
import static pavlo.melnyk.accommodationservice.service.notification.MessageBuilder.buildAccommodationDeletedMessage;
import static pavlo.melnyk.accommodationservice.service.notification.MessageBuilder.buildAccommodationUpdatedMessage;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationAvailabilityDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.AccommodationSummaryDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.CreateAccommodationRequestDto;
import pavlo.melnyk.accommodationservice.dto.accommodation.UpdateAccommodationRequestDto;
import pavlo.melnyk.accommodationservice.exception.AccommodationNotFoundException;
import pavlo.melnyk.accommodationservice.exception.DuplicateEntityException;
import pavlo.melnyk.accommodationservice.mapper.AccommodationMapper;
import pavlo.melnyk.accommodationservice.model.Accommodation;
import pavlo.melnyk.accommodationservice.model.Amenity;
import pavlo.melnyk.accommodationservice.repository.accommodation.AccommodationRepository;
import pavlo.melnyk.accommodationservice.service.amenity.AmenityService;
import pavlo.melnyk.accommodationservice.service.notification.KafkaNotificationProducer;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final AmenityService amenityService;
    private final KafkaNotificationProducer notificationProducer;

    @Override
    @Transactional
    public AccommodationDto save(final CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        validateAccommodationUniqueness(accommodation);

        Set<Amenity> amenities = amenityService.findOrCreateByNames(requestDto.getAmenities());
        accommodation.setAmenities(amenities);

        Accommodation savedAccommodation = accommodationRepository.save(accommodation);
        sendNotification(buildAccommodationCreatedMessage(savedAccommodation));

        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    @Transactional
    public AccommodationDto update(final Long id, final UpdateAccommodationRequestDto requestDto) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        validateAccommodationUniqueness(id, requestDto);

        accommodationMapper.updateAccommodationFromDto(requestDto, accommodation);
        updateAmenities(accommodation, requestDto.getAmenities());

        sendNotification(buildAccommodationUpdatedMessage(accommodation));

        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public Page<AccommodationSummaryDto> findAll(final Pageable pageable) {
        return accommodationRepository.findAll(pageable).map(accommodationMapper::toSummaryDto);
    }

    @Override
    public AccommodationDto findById(Long id) {
        Accommodation accommodation = getAccommodationWithAmenitiesOrThrow(id);
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationAvailabilityDto findAvailabilityById(Long id) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        return accommodationMapper.toAvailabilityDto(accommodation);
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        accommodation.setDeleted(true);

        sendNotification(buildAccommodationDeletedMessage(accommodation));

        accommodationRepository.save(accommodation);
    }

    private void validateAccommodationUniqueness(Accommodation accommodation) {
        if (accommodationRepository.existsByNameAndLocation(
                accommodation.getName(), accommodation.getLocation())) {
            throw new DuplicateEntityException(
                    "Accommodation with the same name and location already exists!");
        }
    }

    private void validateAccommodationUniqueness(Long id,
                                                 UpdateAccommodationRequestDto requestDto) {
        if (accommodationRepository.existsByNameAndLocationAndIdNot(
                requestDto.getName(), requestDto.getLocation(), id)) {
            throw new DuplicateEntityException(
                    "Another accommodation with the same name and location already exists");
        }
    }

    private Accommodation getAccommodationOrThrow(Long id) {
        return accommodationRepository.findById(id).orElseThrow(()
                -> new AccommodationNotFoundException("Accommodation with id "
                + id + " not found"));
    }

    private Accommodation getAccommodationWithAmenitiesOrThrow(Long id) {
        return accommodationRepository.findWithAmenitiesById(id).orElseThrow(()
                -> new AccommodationNotFoundException("Accommodation with id "
                + id + " not found"));
    }

    private void updateAmenities(Accommodation accommodation, Set<String> amenities) {
        if (amenities != null) {
            Set<Amenity> updatedAmenities = amenityService.findOrCreateByNames(amenities);
            accommodation.setAmenities(updatedAmenities);
        }
    }

    private void sendNotification(String message) {
        notificationProducer.sendNotification("accommodation_topic", message);
    }
}
