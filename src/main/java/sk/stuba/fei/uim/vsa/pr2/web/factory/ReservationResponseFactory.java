package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR;
import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.web.response.ReservationDTO;

public class ReservationResponseFactory implements ResponseFactory<Reservation, ReservationDTO> {

    @Override
    public ReservationDTO transformToDto(Reservation entity) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(entity.getReservationId());
        dto.setStart(entity.getDate());
        dto.setEnd(entity.getEndDate());
        dto.setPrices(entity.getPrice());
        if (entity.getParking_spot()!=null) {
            dto.setSpot(new SpotRF2().transformToDto(entity.getParking_spot()));
        }
        if (entity.getCar()!=null) {
            dto.setCar(new CarRF2().transformToDto(entity.getCar()));
        }
        return dto;
    }

    @Override
    public Reservation transformToEntity(ReservationDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(dto.getId());
        reservation.setPrice(dto.getPrices());
        reservation.setDate(dto.getStart());
        reservation.setEndDate(dto.getEnd());
        PARKING_SPOT ps = new SpotRF2().transformToEntity(dto.getSpot());
        reservation.setParking_spot(ps);
        CAR car = new CarRF2().transformToEntity(dto.getCar());
        reservation.setCar(car);
        return reservation;
    }
}
