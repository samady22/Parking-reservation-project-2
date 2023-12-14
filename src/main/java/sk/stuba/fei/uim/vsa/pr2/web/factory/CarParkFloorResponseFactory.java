package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK_FLOOR;
import sk.stuba.fei.uim.vsa.pr2.entities.CPF_ID;
import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarParkFloorDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarParkFloorResponseFactory implements ResponseFactory<CAR_PARK_FLOOR, CarParkFloorDTO> {

    @Override
    public CarParkFloorDTO transformToDto(CAR_PARK_FLOOR entity) {
        CarParkFloorDTO dto = new CarParkFloorDTO();
        dto.setId(entity.getId().getCarParkId());
        dto.setIdentifier(entity.getId().getFloorIdentifier());
        dto.setCarPark(entity.getCar_park().getId());
        ParkingSpotResponseFactory factory = new ParkingSpotResponseFactory();
        dto.setSpots(entity.getParking_spots().stream().map(factory::transformToDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public CAR_PARK_FLOOR transformToEntity(CarParkFloorDTO dto) {
        CAR_PARK_FLOOR cpf=new CAR_PARK_FLOOR();
        cpf.setId(new CPF_ID(dto.getCarPark(),dto.getIdentifier()));
        ParkingSpotResponseFactory factory = new ParkingSpotResponseFactory();
        List<PARKING_SPOT> pss = dto.getSpots().stream().map(factory::transformToEntity).collect(Collectors.toList());
        cpf.setParking_spots(pss);;
        return cpf;
    }
}
