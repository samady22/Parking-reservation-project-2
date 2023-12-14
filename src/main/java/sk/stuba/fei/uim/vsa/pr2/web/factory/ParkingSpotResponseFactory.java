package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.web.response.ParkingSpotDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpotResponseFactory implements ResponseFactory<PARKING_SPOT, ParkingSpotDTO> {
    @Override
    public ParkingSpotDTO transformToDto(PARKING_SPOT entity) {
        ParkingSpotDTO dto = new ParkingSpotDTO();
        dto.setId(entity.getParkingSpotId());
        dto.setIdentifier(entity.getSpotIdentifier());
        dto.setCarPark(entity.getCar_park_floor().getCar_park().getId());
        dto.setCarParkFloor(entity.getCar_park_floor().getId().getFloorIdentifier());
        if (entity.getReservation().isEmpty()) {
            dto.setFree(true);
        } else {
            dto.setFree(entity.getReservation().get(0).getEndDate() != null);
        }
        ReservationResponseFactory factory = new ReservationResponseFactory();
        dto.setReservations(entity.getReservation().stream().map(factory::transformToDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public PARKING_SPOT transformToEntity(ParkingSpotDTO dto) {
        PARKING_SPOT ps = new PARKING_SPOT();
        ps.setParkingSpotId(dto.getId());
        ps.setSpotIdentifier(dto.getIdentifier());
        ReservationResponseFactory factory = new ReservationResponseFactory();
        List<Reservation> reservations = dto.getReservations().stream().map(factory::transformToEntity).collect(Collectors.toList());
        ps.setReservation(reservations);
        return ps;
    }
}
