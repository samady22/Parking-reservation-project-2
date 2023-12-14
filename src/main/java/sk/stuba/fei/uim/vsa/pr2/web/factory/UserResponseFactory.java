package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.CAR;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponseFactory implements ResponseFactory<User, UserDTO> {


    @Override
    public UserDTO transformToDto(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getUserId());
        dto.setFirstName(entity.getFirstname());
        dto.setLastName(entity.getLastname());
        dto.setEmail(entity.getEmail());
        CarResponseFactory factory = new CarResponseFactory();
        dto.setCars(entity.getCars().stream().map(factory::transformToDto).collect(Collectors.toList()));
        return dto;
    }

    @Override
    public User transformToEntity(UserDTO dto) {
        User user = new User();
        user.setUserId(user.getUserId());
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        CarResponseFactory factory = new CarResponseFactory();
        List<CAR> cars=dto.getCars().stream().map(factory::transformToEntity).collect(Collectors.toList());
        user.setCars(cars);
        return user;
    }
}
