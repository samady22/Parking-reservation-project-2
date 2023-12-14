package sk.stuba.fei.uim.vsa.pr2.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.entities.Holiday;
import sk.stuba.fei.uim.vsa.pr2.entities.User;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.web.factory.HolidayResponseFactory;
import sk.stuba.fei.uim.vsa.pr2.web.response.HolidayDTO;

import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Path("/holidays")
public class HolidayResource {
    public static final String EMPTY_RESPONSE = "{}";
    private static final Logger LOGGER = Logger.getLogger(HolidayResource.class.getName());

    private final ObjectMapper json = new ObjectMapper();
    private final CarParkService service = new CarParkService();
    private final HolidayResponseFactory factory = new HolidayResponseFactory();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createHoliday(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String holiday) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            HolidayDTO dto = json.readValue(holiday, HolidayDTO.class);
            Holiday holiday1 = (Holiday) service.createHoliday(dto.getName(), dto.getDate());
            if (holiday1 != null) {
                dto = factory.transformToDto(holiday1);
                return Response.status(Response.Status.CREATED)
                        .entity(dto)
                        .build();
            }
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CONFLICT).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllHolidays(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            List<Holiday> holidays = service.getHolidays().stream()
                    .filter(Holiday.class::isInstance)
                    .map(Holiday.class::cast)
                    .collect(toList());
            List<HolidayDTO> holidayDto = holidays.stream().map(factory::transformToDto).collect(Collectors.toList());
            return Response.status(Response.Status.OK)
                    .entity(json.writeValueAsString(holidayDto)).build();
        } catch (JsonProcessingException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteHoliday(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if (!getEmail(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try {
            Holiday holiday = (Holiday) service.deleteHoliday(id);
            if (holiday != null) {
                HolidayDTO dto = factory.transformToDto(holiday);
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
