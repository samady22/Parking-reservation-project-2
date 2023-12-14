package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK_FLOOR;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarParkDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarParkResponseFactory implements ResponseFactory<CAR_PARK, CarParkDTO>{
    public CarParkDTO transformToDto(CAR_PARK entity) {
        CarParkDTO dto = new CarParkDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setPrices(entity.getPricePerHour());
        CarParkFloorResponseFactory factory = new CarParkFloorResponseFactory();
        dto.setFloors(entity.getCar_park_floors().stream().map(factory::transformToDto).collect(Collectors.toList()));

        return dto;
    }
    @Override
    public CAR_PARK transformToEntity(CarParkDTO dto) {
        CAR_PARK cp = new CAR_PARK();
        cp.setId(dto.getId());
        cp.setName(dto.getName());
        cp.setAddress(dto.getAddress());
        cp.setPricePerHour(dto.getPrices());
        CarParkFloorResponseFactory factory = new CarParkFloorResponseFactory();
        List<CAR_PARK_FLOOR> floors = dto.getFloors().stream().map(factory::transformToEntity).collect(Collectors.toList());
        cp.setCar_park_floors(floors);
        return cp;
    }


}
