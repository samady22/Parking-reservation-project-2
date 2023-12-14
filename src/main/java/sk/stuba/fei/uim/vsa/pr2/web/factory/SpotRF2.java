package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.web.response.SpotDto2;

public class SpotRF2 implements ResponseFactory<PARKING_SPOT, SpotDto2> {
    @Override
    public SpotDto2 transformToDto(PARKING_SPOT entity) {
        SpotDto2 dto =new SpotDto2();
        dto.setId(entity.getParkingSpotId());
        dto.setIdentifier(entity.getSpotIdentifier());
        dto.setCarPark(entity.getCar_park_floor().getCar_park().getId());
        dto.setCarParkFloor(entity.getCar_park_floor().getId().getFloorIdentifier());
        if (entity.getReservation().isEmpty()) {
            dto.setFree(true);
        }else{
            dto.setFree(entity.getReservation().get(0).getEndDate() != null);
        }
        return dto;
    }

    @Override
    public PARKING_SPOT transformToEntity(SpotDto2 dto) {
        PARKING_SPOT ps = new PARKING_SPOT();

        ps.setParkingSpotId(dto.getId());
        ps.setSpotIdentifier(dto.getIdentifier());

        return ps;
    }
}
