package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK_FLOOR;
import sk.stuba.fei.uim.vsa.pr2.entities.PARKING_SPOT;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.CarParkFloorResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.factory.CarParkResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.factory.ParkingSpotResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarParkDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.CarParkFloorDTO;
import sk.stuba.fei.uim.vsa.pr2.web.response.ParkingSpotDTO;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Path("/carparks")
public class CarParkResource {
    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(CarParkResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final CarParkResponseFactory factory = new CarParkResponseFactory();

    private final CarParkFloorResponseFactory floorFactory = new CarParkFloorResponseFactory();
    private final ParkingSpotResponseFactory spotsFactory = new ParkingSpotResponseFactory();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {

            CAR_PARK cp = (CAR_PARK) service.getCarPark(id);
            if (cp != null) {
                CarParkDTO dto = factory.transformToDto(cp);
                return Response.status(Response.Status.OK)
                        .entity(dto)
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCarParks(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("name") String name) {
        if (getEmail(authorization)) {
            try {
                List<CAR_PARK> cps = new ArrayList<>();
                List<CarParkDTO> cpDtos = new ArrayList<>();
                if (name == null) {
                    cps = service.getCarParks().stream()
                            .filter(CAR_PARK.class::isInstance)
                            .map(CAR_PARK.class::cast)
                            .collect(toList());
                    cpDtos = cps.stream().map(factory::transformToDto).collect(Collectors.toList());
                    return Response.status(Response.Status.OK)
                            .entity(json.writeValueAsString(cpDtos)).build();
                }
                CAR_PARK car_park = (CAR_PARK) service.getCarPark(name);

                if (car_park != null) {
                    cps.add(car_park);
                    cpDtos = cps.stream().map(factory::transformToDto).collect(Collectors.toList());

                }
                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(cpDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String carPark) {
        if (getEmail(authorization)) {
            try {
                CarParkDTO dto = json.readValue(carPark, CarParkDTO.class);
                CAR_PARK cp = (CAR_PARK) service.createCarPark(dto.getName(), dto.getAddress(), dto.getPrices());

                if (cp != null) {
                    List<CarParkFloorDTO> cpfDto = dto.getFloors();
                    CAR_PARK_FLOOR cpf;
                    for (CarParkFloorDTO d : cpfDto) {
                        cpf = (CAR_PARK_FLOOR) service.createCarParkFloor(cp.getId(), d.getIdentifier());
                        cpf.setCar_park(cp);
                        cp.addCarParkFloor(cpf);
                        List<ParkingSpotDTO> psDto = d.getSpots();
                        PARKING_SPOT ps;
                        for (ParkingSpotDTO p : psDto) {
                            ps = (PARKING_SPOT) service.createParkingSpot(cp.getId(), d.getIdentifier(), p.getIdentifier());
                            ps.setCar_park_floor(cpf);
                            cpf.addParkingSpot(ps);
                        }
                    }
                    dto = factory.transformToDto(cp);
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


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String carPark) throws JsonProcessingException {
        if (getEmail(authorization)) {
            if (id > 0) {
                CAR_PARK cp = json.readValue(carPark, CAR_PARK.class);
                cp.setId(id);
                CAR_PARK cpUpdate = (CAR_PARK) service.updateCarPark(cp);
                if (cpUpdate != null) {
                    CarParkDTO dto = factory.transformToDto(cpUpdate);
                    return Response.status(Response.Status.OK)
                            .entity(dto)
                            .build();
                }
            }
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            try {
                CAR_PARK cp = (CAR_PARK) service.deleteCarPark(id);
                if (cp != null) {
                    CarParkDTO dto = factory.transformToDto(cp);
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

    // FLOORS
    @GET
    @Path("/{id}/floors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCpfs(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            try {
                List<CAR_PARK_FLOOR> cpfs = service.getCarParkFloors(id).stream()
                        .filter(CAR_PARK_FLOOR.class::isInstance)
                        .map(CAR_PARK_FLOOR.class::cast)
                        .collect(toList());
                List<CarParkFloorDTO> cpfDtos = cpfs.stream().map(floorFactory::transformToDto).collect(Collectors.toList());

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
    @Path("/{id}/floors/{identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if (getEmail(authorization)) {
            CAR_PARK_FLOOR cpf = (CAR_PARK_FLOOR) service.getCarParkFloor(id, identifier);
            if (cpf != null) {
                CarParkFloorDTO dto = floorFactory.transformToDto(cpf);
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
    @Path("/{id}/floors")
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
                    dto = floorFactory.transformToDto(cpf);
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
    @Path("/{id}/floors/{identifier}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCarPark(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if (getEmail(authorization)) {
            try {
                CAR_PARK_FLOOR cpf = (CAR_PARK_FLOOR) service.deleteCarParkFloor(id, identifier);
                if (cpf != null) {
                    CarParkFloorDTO dto = floorFactory.transformToDto(cpf);
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


    // SPOTS
    @GET
    @Path("/carparks/{id}/spots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPss(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("free") Boolean free, @PathParam("id") Long id) {
        if (getEmail(authorization)) {

            try {
                List<PARKING_SPOT> ps;
                ps = service.getParkingSpots(id).stream()
                        .filter(PARKING_SPOT.class::isInstance)
                        .map(PARKING_SPOT.class::cast)
                        .collect(toList());

                List<PARKING_SPOT> ocuPs;
                List<PARKING_SPOT> avPs;

                String carParkName = "";
                for (PARKING_SPOT p : ps) {
                    carParkName = p.getCar_park_floor().getCar_park().getName();
                }
                ocuPs = service.getOccupiedParkingSpots(carParkName).stream()
                        .filter(PARKING_SPOT.class::isInstance)
                        .map(PARKING_SPOT.class::cast)
                        .collect(toList());

                avPs = service.getAvailableParkingSpots(carParkName).stream()
                        .filter(PARKING_SPOT.class::isInstance)
                        .map(PARKING_SPOT.class::cast)
                        .collect(toList());

                List<ParkingSpotDTO> psDtos = new ArrayList<>();

                if (free == null) {
                    psDtos = ps.stream().map(spotsFactory::transformToDto).collect(Collectors.toList());
                }
                if (Boolean.FALSE.equals(free)) {
                    psDtos = ocuPs.stream().map(spotsFactory::transformToDto).collect(Collectors.toList());
                }
                if (Boolean.TRUE.equals(free)) {
                    psDtos = avPs.stream().map(spotsFactory::transformToDto).collect(Collectors.toList());
                }
                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(psDtos)).build();

            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/carparks/{id}/floors/{identifier}/spots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPs(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if (getEmail(authorization)) {

            try {
                List<PARKING_SPOT> pss = service.getParkingSpots(id, identifier).stream()
                        .filter(PARKING_SPOT.class::isInstance)
                        .map(PARKING_SPOT.class::cast)
                        .collect(toList());

                List<ParkingSpotDTO> psDtos = pss.stream().map(spotsFactory::transformToDto).collect(Collectors.toList());

                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(psDtos)).build();
            } catch (JsonProcessingException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/carparks/{id}/floors/{identifier}/spots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPs(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier, String parkingSpot) {
        if (getEmail(authorization)) {
            try {
                ParkingSpotDTO dto = json.readValue(parkingSpot, ParkingSpotDTO.class);
                PARKING_SPOT ps = (PARKING_SPOT) service.createParkingSpot(id, identifier, dto.getIdentifier());

                if (ps != null) {
                    dto = spotsFactory.transformToDto(ps);
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
