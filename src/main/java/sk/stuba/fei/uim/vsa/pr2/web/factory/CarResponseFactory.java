package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarResponseFactory implements ResponseFactory<CAR, CarDTO> {
    @Override
    public CarDTO transformToDto(CAR entity) {
        CarDTO dto=new CarDTO();
        dto.setId(entity.getCarId());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setColour(entity.getColor());
        dto.setVrp(entity.getVehicleRegistrationPlate());
        dto.setOwner(new UserRF2().transformToDto(entity.getUser()));
        ReservationResponseFactory factory = new ReservationResponseFactory();
        dto.setReservations(entity.getReservation().stream().map(factory::transformToDto).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public CAR transformToEntity(CarDTO dto) {
        CAR car = new CAR();
        car.setCarId(dto.getId());
        car.setBrand(dto.getBrand());
        car.setColor(dto.getColour());
        car.setModel(dto.getModel());
        car.setVehicleRegistrationPlate(dto.getVrp());
        User user = new UserRF2().transformToEntity(dto.getOwner());
        car.setUser(user);
        user.addCar(car);
        ReservationResponseFactory factory = new ReservationResponseFactory();
        List<Reservation> reservations = dto.getReservations().stream().map(factory::transformToEntity).collect(Collectors.toList());
        car.setReservation(reservations);
        return car;
    }
}
