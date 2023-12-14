package sk.stuba.fei.uim.vsa.pr2.web;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.UserResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDTO;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Path("/users")
public class UserResource {

    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final UserResponseFactory factory = new UserResponseFactory();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String user) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            UserDTO dto = json.readValue(user, UserDTO.class);
            User user1 = (User) service.createUser(dto.getFirstName(), dto.getLastName(), dto.getEmail());
            if (user1 != null) {
                List<CarDTO> carDTOS = dto.getCars();
                CAR car;
                for (CarDTO c : carDTOS) {
                    car = (CAR) service.createCar(user1.getUserId(), c.getBrand(), c.getModel(), c.getColour(), c.getVrp());
                    car.setUser(user1);
                    user1.addCar(car);
                }
                dto = factory.transformToDto(user1);
                return Response.status(Response.Status.CREATED)
                        .entity(json.writeValueAsString(dto))
                        .build();
            }
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("email") String email) {
        if (getEmail(authorization)) {
            try {
                List<User> users = new ArrayList<>();
                if (email != null) {
                    User user = (User) service.getUser(email);
                    if (user != null) {
                        users.add(user);
                    }
                } else {
                    users = service.getUsers().stream()
                            .filter(User.class::isInstance)
                            .map(User.class::cast)
                            .collect(toList());
                }
                List<UserDTO> userDTOS = users.stream().map(factory::transformToDto).collect(Collectors.toList());
                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(userDTOS)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            User user = (User) service.getUser(id);
            if (user != null) {
                UserDTO userDTO = factory.transformToDto(user);
                return Response.status(Response.Status.OK)
                        .entity(userDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String user) throws JsonProcessingException {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (id > 0) {
            User user1 = json.readValue(user, User.class);
            user1.setUserId(id);
            User usedrUpdate = (User) service.updateUser(user1);
            if (usedrUpdate != null) {
                UserDTO dto = factory.transformToDto(usedrUpdate);
                return Response.status(Response.Status.OK)
                        .entity(dto)
                        .build();
            }
        }
        return Response.status(Response.Status.CONFLICT).build();
    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            try {
                User user = (User) service.deleteUser(id);
                if (user != null) {
                    UserDTO dto = factory.transformToDto(user);
                    return Response.status(Response.Status.NO_CONTENT)
                            .entity(dto)
                            .build();
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    private Boolean getEmail(String authHeader) {
        try {
            CarParkService service = new CarParkService();
            String base64Encoded = authHeader.substring("Basic ".length());
            String decoded = new String(Base64.getDecoder().decode(base64Encoded));
            String email = decoded.split(":")[0];
            Long id = Long.parseLong(decoded.split(":")[1]);
            User userCast = (User) service.getUser(email);
            User user2Cast = (User) service.getUser(id);
            if (userCast == null || user2Cast == null) {
                return false;
            }
            if (!userCast.getUserId().equals(user2Cast.getUserId())) {
                return false;
            }
            return true;


        } catch (Exception e) {
            return false;
        }
    }

}


