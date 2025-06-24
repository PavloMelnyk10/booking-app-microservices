package pavlo.melnyk.accommodationservice.service.accommodation;


import static pavlo.melnyk.accommodationservice.service.notification.MessageBuilder.buildAccommodationDeletedMessage;
import static pavlo.melnyk.accommodationservice.service.notification.MessageBuilder.buildAccommodationUpdatedMessage;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import pavlo.melnyk.accommodationservice.service.notification.MessageBuilder;

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
        sendNotification(MessageBuilder.buildAccommodationCreatedMessage(savedAccommodation));

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
    @Transactional
    public void deleteById(final Long id) {
        Accommodation accommodation = getAccommodationOrThrow(id);
        accommodation.setDeleted(true);

        sendNotification(buildAccommodationDeletedMessage(accommodation));

        accommodationRepository.save(accommodation);
    }

    private void validateAccommodationUniqueness(Accommodation accommodation) {
        if (accommodationRepository.existsByNameAndAddress_CountryAndAddress_CityAndAddress_Street(
                accommodation.getName(),
                accommodation.getAddress().getCountry(),
                accommodation.getAddress().getCity(),
                accommodation.getAddress().getStreet())) {
            throw new DuplicateEntityException(
                    "Accommodation with the same name and address already exists!");
        }
    }

    private void validateAccommodationUniqueness(Long id,
                                                 UpdateAccommodationRequestDto requestDto) {
        if (requestDto.getName() != null && requestDto.getAddress() != null) {
            if (accommodationRepository.existsByNameAndAddress_CountryAndAddress_CityAndAddress_StreetAndIdNot(
                    requestDto.getName(),
                    requestDto.getAddress().getCountry(),
                    requestDto.getAddress().getCity(),
                    requestDto.getAddress().getStreet(),
                    id)) {
                throw new DuplicateEntityException(
                        "Another accommodation with the same name and address already exists");
            }
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
