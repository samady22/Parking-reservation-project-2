package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.Reservation;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.ReservationResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.ReservationDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/reservations")
public class ReservationResource {

    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(ReservationResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final ReservationResponseFactory factory = new ReservationResponseFactory();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReserv(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String res) {
        if (getEmail(authorization) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            ReservationDTO resDto = json.readValue(res, ReservationDTO.class);
            if (resDto.getSpot() != null && resDto.getCar() != null) {
                Reservation reservation1 = (Reservation) service.createReservation(resDto.getSpot().getId(), resDto.getCar().getId());
                if (reservation1 != null) {
                    resDto = factory.transformToDto(reservation1);
                    return Response.status(Response.Status.CREATED)
                            .entity(json.writeValueAsString(resDto))
                            .build();
                }
            }
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReserves(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("user") Long user, @QueryParam("spot") Long spot,
                                   @QueryParam("date") String date) {
        if (getEmail(authorization) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        List<Reservation> reservations = new ArrayList<>();
        if (user != null && spot == null && date == null) {
            reservations = service.getMyReservations(user).stream()
                    .filter(Reservation.class::isInstance).map(Reservation.class::cast)
                    .collect(Collectors.toList());
        } else if (user == null && spot != null && date != null) {
            Date date1;
            try {
                date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            } catch (ParseException e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            reservations = service.getReservations(spot, date1)
                    .stream()
                    .filter(Reservation.class::isInstance).map(Reservation.class::cast)
                    .collect(Collectors.toList());
        } else {
            reservations = service.getReservations()
                    .stream()
                    .filter(Reservation.class::isInstance).map(Reservation.class::cast)
                    .collect(Collectors.toList());
            if (reservations.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity(reservations).build();
            }
        }

        if (!reservations.isEmpty()) {
            List<ReservationDTO> reservationDTOS = reservations.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK)
                    .entity(reservationDTOS).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReserve(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) throws JsonProcessingException {
        if (getEmail(authorization) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Reservation res = (Reservation) service.getReservation(id);
        if (res != null) {
            ReservationDTO resDto = factory.transformToDto(res);
            return Response.status(Response.Status.OK)
                    .entity(json.writeValueAsString(resDto)).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/{id}/end")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response endReserve(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        System.out.println("You entere the end reservation ");
        Reservation reservation = (Reservation) service.getReservation(id);
        if (reservation != null) {
            User user = getEmail(authorization);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            } else if (!reservation.getCar().getUser().getUserId().equals(user.getUserId())) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        Reservation endRes = (Reservation) service.endReservation(id);
        if (endRes != null) {
            ReservationDTO resDto = factory.transformToDto(endRes);
            return Response.status(Response.Status.OK)
                    .entity(resDto)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

//    @PUT
//    @Path("/{id}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateUser(@PathParam("id") Long id, String reserve) throws JsonProcessingException {
//        if (id > 0) {
//            Reservation reservation=json.readValue(reserve,Reservation.class);
//            reservation.setReservationId(id);
//            Reservation reserveUpdate = (Reservation) service.updateReservation(reservation);
//            if (reserveUpdate != null) {
//                ReservationDTO dto = factory.transformToDto(reserveUpdate);
//                return Response.status(Response.Status.OK)
//                        .entity(dto)
//                        .build();
//            }
//        }
//        return  Response.status(Response.Status.CONFLICT).build();
//    }


    private User getEmail(String authHeader) {
        try {
            CarParkService service = new CarParkService();
            String base64Encoded = authHeader.substring("Basic ".length());
            String decoded = new String(Base64.getDecoder().decode(base64Encoded));
            String email = decoded.split(":")[0];
            Long id = Long.parseLong(decoded.split(":")[1]);
            Object user = service.getUser(email);
            Object user2 = service.getUser(id);
            if (user == null || user2 == null) {
                return null;
            }
            User userCast = (User) user;
            User user2Cast = (User) user2;
            if (!userCast.getUserId().equals(user2Cast.getUserId())) {
                return null;
            }
            return (User) user;
        } catch (Exception e) {
            return null;
        }
    }


}
