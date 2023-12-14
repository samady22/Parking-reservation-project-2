package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK_FLOOR;
import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.CarParkFloorResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarParkFloorDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.ParkingSpotDTO;

import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//@Path("/")
public class CarParkFloorResource {

    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(CarParkFloorResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final CarParkFloorResponseFactory factory = new CarParkFloorResponseFactory();

    /*@GET
    @Path("/carparks/{id}/floors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCpfs(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            try {
                List<CAR_PARK_FLOOR> cpfs = service.getCarParkFloors(id).stream()
                        .filter(CAR_PARK_FLOOR.class::isInstance)
                        .map(CAR_PARK_FLOOR.class::cast)
                        .collect(toList());
                List<CarParkFloorDTO> cpfDtos = cpfs.stream().map(factory::transformToDto).collect(Collectors.toList());

                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(cpfDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/carparks/{id}/floors/{identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if (getEmail(authorization)) {
            CAR_PARK_FLOOR cpf = (CAR_PARK_FLOOR) service.getCarParkFloor(id, identifier);
            if (cpf != null) {
                CarParkFloorDTO dto = factory.transformToDto(cpf);
                return Response.status(Response.Status.OK)
                        .entity(dto)
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Path("/carparks/{id}/floors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCpfs(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String carParkFloor) {
        if (getEmail(authorization)) {
            try {
                CarParkFloorDTO dto = json.readValue(carParkFloor, CarParkFloorDTO.class);
                CAR_PARK_FLOOR cpf = (CAR_PARK_FLOOR) service.createCarParkFloor(id, dto.getIdentifier());
                if (cpf != null) {
                    List<ParkingSpotDTO> psDtos = dto.getSpots();
                    PARKING_SPOT ps;
                    for (ParkingSpotDTO p : psDtos) {
                        ps = (PARKING_SPOT) service.createParkingSpot(id, dto.getIdentifier(), p.getIdentifier());
                        ps.setCar_park_floor(cpf);
                        cpf.addParkingSpot(ps);
                    }
                    dto = factory.transformToDto(cpf);
                    return Response.status(Response.Status.CREATED)
                            .entity(dto)
                            .build();
                }
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @DELETE
    @Path("/carparks/{id}/floors/{identifier}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if (getEmail(authorization)) {
            try {
                CAR_PARK_FLOOR cpf = (CAR_PARK_FLOOR) service.deleteCarParkFloor(id, identifier);
                if (cpf != null) {
                    CarParkFloorDTO dto = factory.transformToDto(cpf);
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
    }*/


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
