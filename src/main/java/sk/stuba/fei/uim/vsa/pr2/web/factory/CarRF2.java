package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarDto2;

public class CarRF2 implements ResponseFactory<CAR, CarDto2> {
    @Override
    public CarDto2 transformToDto(CAR entity) {
        CarDto2 dto=new CarDto2();
        dto.setId(entity.getCarId());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setColour(entity.getColor());
        dto.setVrp(entity.getVehicleRegistrationPlate());
        dto.setOwner(new UserRF2().transformToDto(entity.getUser()));

        return dto;
    }

    @Override
    public CAR transformToEntity(CarDto2 dto) {
        CAR car = new CAR();
        car.setCarId(dto.getId());
        car.setBrand(dto.getBrand());
        car.setColor(dto.getColour());
        car.setModel(dto.getModel());
        car.setVehicleRegistrationPlate(dto.getVrp());
        User user = new UserRF2().transformToEntity(dto.getOwner());
        car.setUser(user);
        user.addCar(car);
        return car;
    }
}
