package sk.stuba.fei.uim.vsa.pr2.web.factory;

import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDto2;

public class UserRF2 implements ResponseFactory<User, UserDto2> {
    @Override
    public UserDto2 transformToDto(User entity) {
        UserDto2 dto = new UserDto2();
        dto.setId(entity.getUserId());
        dto.setFirstName(entity.getFirstname());
        dto.setLastName(entity.getLastname());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    /*public UserDTO transformToOtherDto(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getUserId());
        dto.setFirstName(entity.getFirstname());
        dto.setLastName(entity.getLastname());
        dto.setEmail(entity.getEmail());
        return dto;
    }*/

    @Override
    public User transformToEntity(UserDto2 dto) {
        User user = new User();
        user.setUserId(user.getUserId());
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        return user;
    }

    /*public User otherTransformToEntity(UserDTO dto) {
        User user = new User();
        user.setUserId(user.getUserId());
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        return user;
    }*/
}
