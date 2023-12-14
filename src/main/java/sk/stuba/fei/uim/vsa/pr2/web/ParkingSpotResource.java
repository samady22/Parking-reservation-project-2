package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.*;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.ParkingSpotResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.ParkingSpotDTO;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Path("/parkingspots")
public class ParkingSpotResource {

    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(ParkingSpotResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final ParkingSpotResponseFactory factory = new ParkingSpotResponseFactory();



    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPsById(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) throws JsonProcessingException {
        if (getEmail(authorization)) {

            PARKING_SPOT ps = (PARKING_SPOT) service.getParkingSpot(id);
            if (ps != null) {
                ParkingSpotDTO psDto = factory.transformToDto(ps);
                return Response.status(Response.Status.OK)
                        .entity(json.writeValueAsString(psDto)).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }



//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateParkingSpot(@PathParam("id") Long id, String parkingSpot) throws JsonProcessingException {
//        if (id > 0) {
//            ParkingSpotDTO cp=json.readValue(parkingSpot,ParkingSpotDTO.class);
//
//            cp.setParkingSpotId(id);
//            PARKING_SPOT psUpdate = (PARKING_SPOT) service.updateParkingSpot(cp);
//            if (psUpdate != null) {
//                ParkingSpotDTO dto = factory.transformToDto(psUpdate);
//                return Response.status(Response.Status.OK)
//                        .entity(json.writeValueAsString(dto))
//                        .build();
//            }
//        }
//        return  Response.status(Response.Status.CONFLICT).build();
//
//    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteParkingSpot(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (getEmail(authorization)) {
            try {
                PARKING_SPOT ps = (PARKING_SPOT) service.deleteParkingSpot(id);
                if (ps != null) {
                    ParkingSpotDTO dto = factory.transformToDto(ps);
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
