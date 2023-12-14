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
import sk.stuba.fei.uim.vsa.pr2.web.factory.CarResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.UserDto2;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Path("/cars")
public class CarResource {

    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(CarResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final CarResponseFactory factory = new CarResponseFactory();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String car) {
        if (!getEmail(authorization)) {
            Response.status(Response.Status.UNAUTHORIZED).build();
        }

        try {
            CarDTO dto = json.readValue(car, CarDTO.class);
            UserDto2 userDTO = dto.getOwner();

            User user = new User();
            if (userDTO.getId() == null) {
                user = (User) service.getUser(userDTO.getEmail());
            } else {
                user = (User) service.getUser(userDTO.getId());
            }
            if (user != null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            user = (User) service.createUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
            CAR car1 = new CAR();
            if (user != null) {
                car1 = (CAR) service.createCar(user.getUserId(), dto.getBrand(), dto.getModel(), dto.getColour(), dto.getVrp());
                if (car1 != null) {
                    dto = factory.transformToDto(car1);
                    return Response.status(Response.Status.CREATED)
                            .entity(dto)
                            .build();
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();

        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCars(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("user") Long user, @QueryParam("vrp") String vrp) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            List<CAR> cars = new ArrayList<>();
            if (user == null && vrp != null) {
                CAR car = (CAR) service.getCar(vrp);
                if (car != null) {
                    cars.add(car);
                }
            } else if (user != null && vrp == null) {
                cars = service.getCars(user).stream()
                        .filter(CAR.class::isInstance).map(CAR.class::cast)
                        .collect(Collectors.toList());
            } else {
                cars = service.getCars().stream()
                        .filter(CAR.class::isInstance).map(CAR.class::cast)
                        .collect(Collectors.toList());
            }
            List<CarDTO> carDTOS = cars.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK)
                    .entity(json.writeValueAsString(carDTOS)).build();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CAR car = (CAR) service.getCar(id);
        if (car != null) {
            CarDTO carDTO = factory.transformToDto(car);
            return Response.status(Response.Status.OK)
                    .entity(carDTO)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();

        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String car) throws JsonProcessingException {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CAR car1 = json.readValue(car, CAR.class);
        car1.setCarId(id);
        CAR updCar = (CAR) service.updateCar(car1);
        if (updCar != null) {
            return
                    Response.status(Response.Status.OK)
                            .entity(updCar)
                            .build();
        } else {
            return
                    Response.status(Response.Status.CONFLICT).build();
        }
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteCar(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CAR car = (CAR) service.deleteCar(id);
        if (car != null) {
            CarDTO dto = factory.transformToDto(car);
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(dto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
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
