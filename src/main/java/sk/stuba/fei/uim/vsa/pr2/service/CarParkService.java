package sk.stuba.fei.uim.vsa.pr2.service;


import sk.stuba.fei.uim.vsa.pr2.AbstractCarParkService;
import sk.stuba.fei.uim.vsa.pr2.entities.*;
import sk.stuba.fei.uim.vsa.pr2.entities.CAR_PARK;
import sk.stuba.fei.uim.vsa.pr2.web.response.HolidayDTO;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class CarParkService extends AbstractCarParkService {

    @Override
    public Object createCarPark(String name, String address, Integer pricePerHour) {
        EntityManager em = emf.createEntityManager();
        List<CAR_PARK> carParks = em.createQuery("select cp from CAR_PARK cp", CAR_PARK.class).getResultList();
        for (CAR_PARK ps : carParks) {
            if (ps.getName().equals(name)) {
                return null;
            }
        }
        try {
            CAR_PARK car_park = new CAR_PARK(name, address, pricePerHour);
            em.getTransaction().begin();
            em.persist(car_park);
            em.getTransaction().commit();
            em.close();
            return car_park;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public Object createCarPark(Object carpark) {
        EntityManager em = emf.createEntityManager();
        CAR_PARK carPark=(CAR_PARK) carpark;
        List<CAR_PARK> carParks = em.createQuery("select cp from CAR_PARK cp", CAR_PARK.class).getResultList();
        for (CAR_PARK ps : carParks) {
            if (ps.getName().equals(carPark.getName())) {
                return null;
            }
        }
        try {
            CAR_PARK car_park = new CAR_PARK(carPark.getName(), carPark.getAddress(),carPark.getPricePerHour());
            em.getTransaction().begin();
            em.persist(car_park);
            em.getTransaction().commit();
            em.close();
            return car_park;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object getCarPark(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        return em.find(CAR_PARK.class, carParkId);
    }

    @Override
    public Object getCarPark(String carParkName) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createQuery("select cp from CAR_PARK cp where cp.name=:name", CAR_PARK.class);
            q.setParameter("name", carParkName);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Object> getCarParks() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select cp from CAR_PARK cp", CAR_PARK.class);
        return (List<Object>) query.getResultList();
    }

    @Override
    public Object updateCarPark(Object carPark) {
        EntityManager em = emf.createEntityManager();
        if (carPark instanceof CAR_PARK) {
            CAR_PARK car_park = (CAR_PARK) carPark;
            CAR_PARK carPark1 = em.find(CAR_PARK.class, car_park.getId());
            List<CAR_PARK> carParks = em.createQuery("select cp from CAR_PARK cp", CAR_PARK.class).getResultList();
            em.getTransaction().begin();
            if (carPark1 == null) {
                for (CAR_PARK ps : carParks) {
                    if (ps.getName().equals(car_park.getName())) {
                        return null;
                    }
                }
                em.persist(carPark);
            } else {
                for (CAR_PARK ps : carParks) {
                    if (ps.getName().equals(car_park.getName())) {
                        return null;
                    }
                }
                if (car_park.getName() != null) {
                    carPark1.setName(car_park.getName());
                }
                if (car_park.getAddress() != null) {
                    carPark1.setAddress(car_park.getAddress());
                }
                if (car_park.getPricePerHour() != 0) {
                    carPark1.setPricePerHour(car_park.getPricePerHour());
                }
            }
            em.getTransaction().commit();
            return carPark;
        }
        return null;
    }

    @Override
    public Object deleteCarPark(Long carParkId) {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CAR_PARK carPark;
            Query query;
            Query query1;
            Query query2;
            List<PARKING_SPOT> parking_spots;
            try {
                query = em.createQuery("update Reservation rs set rs.parking_spot = null" +
                        " where rs.parking_spot.car_park_floor.car_park.carParkId=:cpId", Reservation.class);
                query.setParameter("cpId", carParkId);

                query1 = em.createQuery("select ps from PARKING_SPOT ps where ps.reservations in (select rs from Reservation rs where rs.parking_spot.car_park_floor.car_park.carParkId=:cpId)", PARKING_SPOT.class);
                query1.setParameter("cpId", carParkId);
                parking_spots = query1.getResultList();

                query2 = em.createQuery("select r from Reservation r where r.parking_spot.car_park_floor.car_park.carParkId=:cpId", Reservation.class);
                query2.setParameter("cpId", carParkId);

                carPark = em.getReference(CAR_PARK.class,carParkId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            List<Reservation> rever = query2.getResultList();
            for (Reservation r : rever) {
                endReservation(r.getReservationId());
                parking_spots.get(0).removeReservation(r);
            }

            query.executeUpdate();
            em.remove(carPark);
            em.getTransaction().commit();
            return carPark;
        } finally {
            if (em != null) {
                em.close();
            }
        }



//        EntityManager em = emf.createEntityManager();
//        try {
//            em.getTransaction().begin();
//            CAR_PARK car_park;
//            try {
//                car_park = em.getReference(CAR_PARK.class, carParkId);
//                car_park.getId();
//            } catch (EntityNotFoundException e) {
//                return null;
//            }
//            em.remove(car_park);
//            em.getTransaction().commit();
//            return car_park;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
    }

    @Override
    public Object createCarParkFloor(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        try {
            List<CAR_PARK_FLOOR> carParkFloors = em.createQuery("select cpf from CAR_PARK_FLOOR cpf", CAR_PARK_FLOOR.class).getResultList();
            for (CAR_PARK_FLOOR psf : carParkFloors) {
                if (psf.getId().getFloorIdentifier().equals(floorIdentifier) && psf.getId().getCarParkId().equals(carParkId)) {
                    return null;
                }
            }

            CAR_PARK car_park;
//            try {
//                car_park = em.find(CAR_PARK.class, carParkId);
//            } catch (EntityNotFoundException e) {
//                return null;
//            }

            car_park = em.find(CAR_PARK.class, carParkId);
            if (car_park == null) {
                return null;
            }

            CAR_PARK_FLOOR car_park_floor = new CAR_PARK_FLOOR(new CPF_ID(carParkId, floorIdentifier));
            car_park_floor.setCar_park(car_park);
            car_park.addCarParkFloor(car_park_floor);
            em.getTransaction().begin();
            em.persist(car_park_floor);
            em.getTransaction().commit();
            return car_park_floor;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object getCarParkFloor(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select cpf from CAR_PARK_FLOOR cpf where cpf.id.carParkId=:parkId " +
                    "and cpf.id.floorIdentifier=:floorId", CAR_PARK_FLOOR.class);
            query.setParameter("parkId", carParkId).setParameter("floorId", floorIdentifier);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Object getCarParkFloor(Long carParkFloorId) {
        return null;
    }

    @Override
    public List<Object> getCarParkFloors(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select cpf from CAR_PARK_FLOOR cpf where cpf.car_park.carParkId=:cpId", CAR_PARK_FLOOR.class);
        query.setParameter("cpId", carParkId);

        return query.getResultList();
    }

    @Override
    public Object updateCarParkFloor(Object carParkFloor) {
        return null;
    }

    @Override
    public Object deleteCarParkFloor(Long carParkId, String floorIdentifier) {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            CAR_PARK_FLOOR carParkFloor;
            Query query;
            Query query1;
            Query query2;
            List<PARKING_SPOT> parking_spots;
            try {
                query = em.createQuery("update Reservation rs set rs.parking_spot = null" +
                        " where rs.parking_spot.car_park_floor.car_park.carParkId=:cpId", Reservation.class);
                query.setParameter("cpId", carParkId);

                query1 = em.createQuery("select ps from PARKING_SPOT ps where ps.reservations in (select rs from Reservation rs where rs.parking_spot.car_park_floor.car_park.carParkId=:cpId)", PARKING_SPOT.class);
                query1.setParameter("cpId", carParkId);
                parking_spots = query1.getResultList();

                query2 = em.createQuery("select r from Reservation r where r.parking_spot.car_park_floor.car_park.carParkId=:cpId", Reservation.class);
                query2.setParameter("cpId", carParkId);


                Query query3 = em.createQuery("select cpf from CAR_PARK_FLOOR cpf where" +
                    " cpf.id.carParkId=:cpId and cpf.id.floorIdentifier=:floorId", CAR_PARK_FLOOR.class);
            query3.setParameter("cpId", carParkId).setParameter("floorId", floorIdentifier);
            carParkFloor = (CAR_PARK_FLOOR) query3.getSingleResult();
            } catch (EntityNotFoundException e) {
                return null;
            }
            List<Reservation> rever = query2.getResultList();
            for (Reservation r : rever) {
                endReservation(r.getReservationId());
                parking_spots.get(0).removeReservation(r);
            }

            query.executeUpdate();
            em.remove(carParkFloor);
            em.getTransaction().commit();
            return carParkFloor;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public Object deleteCarParkFloor(Long carParkFloorId) {
        return null;

    }

    @Override
    public Object createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {
        EntityManager em = emf.createEntityManager();
        try {
            CAR_PARK_FLOOR car_park_floor;
            try {
                car_park_floor = em.createQuery("select cpf from CAR_PARK_FLOOR cpf where cpf.id.carParkId=:carpId and cpf.id.floorIdentifier=:floorId", CAR_PARK_FLOOR.class)
                        .setParameter("carpId", carParkId).setParameter("floorId", floorIdentifier).getSingleResult();
            } catch (Exception e) {
                return null;
            }
            List<PARKING_SPOT> lis = em.createQuery("select ps from PARKING_SPOT ps where ps.car_park_floor.car_park.carParkId=:cpId", PARKING_SPOT.class).setParameter("cpId", carParkId).getResultList();
            if (lis != null) {
                for (PARKING_SPOT p : lis) {
                    if (p.getSpotIdentifier().equals(spotIdentifier)) {
                        return null;
                    }

                }
            }
            PARKING_SPOT parking_spot = new PARKING_SPOT(spotIdentifier);
            parking_spot.setCar_park_floor(car_park_floor);
            car_park_floor.addParkingSpot(parking_spot);
            em.getTransaction().begin();
            em.persist(parking_spot);
            em.getTransaction().commit();
            return parking_spot;
        } catch (EntityNotFoundException e) {
            return null;
        }

    }


    @Override
    public Object getParkingSpot(Long parkingSpotId) {
        EntityManager em = emf.createEntityManager();
        return em.find(PARKING_SPOT.class, parkingSpotId);
    }

    @Override
    public List<Object> getParkingSpots(Long carParkId, String floorIdentifier) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select ps from PARKING_SPOT ps where ps.car_park_floor.car_park.carParkId=:parkId " +
                    "and ps.car_park_floor.id.floorIdentifier=:floorId", PARKING_SPOT.class);
            query.setParameter("parkId", carParkId).setParameter("floorId", floorIdentifier);
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Object> getParkingSpots(Long carParkId) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select ps from PARKING_SPOT ps where ps.car_park_floor.id.carParkId=:cpId", PARKING_SPOT.class);
            query.setParameter("cpId", carParkId);
            List<Object> list = query.getResultList();
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    @Override
    public List<Object> getAvailableParkingSpots(String carParkName) {
        EntityManager em = emf.createEntityManager();
        Map<String, List<Object>> map = new HashMap<>();
        try {
            Query query = em.createQuery("select ps from PARKING_SPOT ps where ps.car_park_floor.car_park.name=:cpName", PARKING_SPOT.class);
            query.setParameter("cpName", carParkName);
            List<PARKING_SPOT> list = query.getResultList();

            list.removeIf(ps -> ps.getReservation().size() != 0);

            return Arrays.asList(list.toArray());
        } catch (NoResultException e) {
            return null;
        }

    }

    @Override
    public List<Object> getOccupiedParkingSpots(String carParkName) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select ps from PARKING_SPOT ps where  ps.car_park_floor.car_park.name=:cpName", PARKING_SPOT.class);
            query.setParameter("cpName", carParkName);
            List<PARKING_SPOT> list = query.getResultList();
            list.removeIf(ps -> ps.getReservation().size() == 0);
            return Arrays.asList(list.toArray());
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Object updateParkingSpot(Object parkingSpot) {
        EntityManager em = emf.createEntityManager();

//        if (parkingSpot instanceof PARKING_SPOT) {
            PARKING_SPOT parking_spot = (PARKING_SPOT) parkingSpot;
//            if (parking_spot.getCar_park_floor() == null) {
//                return null;
//            }
            PARKING_SPOT parkingSpot1 = em.find(PARKING_SPOT.class, parking_spot.getParkingSpotId());
        if (parkingSpot1 == null) {
            return null;
        }
            em.getTransaction().begin();
            List<PARKING_SPOT> parkingSpots = em.createQuery("select ps from PARKING_SPOT ps", PARKING_SPOT.class).getResultList();
            if (!parkingSpots.isEmpty()) {
                for (PARKING_SPOT ps : parkingSpots) {
                    if (ps.getSpotIdentifier().equals(parking_spot.getSpotIdentifier())) {
                        return null;
                    }
                }
            } else {
                if (parking_spot.getSpotIdentifier() != null) {
                    parkingSpot1.setSpotIdentifier(parking_spot.getSpotIdentifier());
                }
                em.getTransaction().commit();
                return parkingSpot;
            }
//        }


        return null;
    }

    @Override
    public Object deleteParkingSpot(Long parkingSpotId) {


        EntityManager em = emf.createEntityManager();
        try {
            PARKING_SPOT parking_spot1;
            Query query;
            Query query1;
            Query query2;
            List<PARKING_SPOT> parking_spots;
            try {
                query = em.createQuery("update Reservation rs set rs.parking_spot = null" +
                        " where rs.parking_spot.parkingSpotId=:psId", Reservation.class);
                query.setParameter("psId", parkingSpotId);

                query1 = em.createQuery("select ps from PARKING_SPOT ps where ps.reservations in (select rs from Reservation rs where rs.parking_spot.parkingSpotId=:psId)", PARKING_SPOT.class);
                query1.setParameter("psId", parkingSpotId);
                parking_spots = query1.getResultList();

                query2 = em.createQuery("select r from Reservation r where r.parking_spot.parkingSpotId=:psId", Reservation.class);
                query2.setParameter("psId", parkingSpotId);

                parking_spot1 = em.getReference(PARKING_SPOT.class,parkingSpotId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            List<Reservation> rever = query2.getResultList();
            for (Reservation r : rever) {
                endReservation(r.getReservationId());
                parking_spots.get(0).removeReservation(r);
            }
            em.getTransaction().begin();

            query.executeUpdate();
            em.remove(parking_spot1);
            em.getTransaction().commit();
            return parking_spot1;
        } finally {
            if (em != null) {
                em.close();
            }
        }




//        EntityManager em = emf.createEntityManager();
//        try {
//            em.getTransaction().begin();
//            PARKING_SPOT parking_spot;
//            try {
//                parking_spot = em.createQuery("select ps from PARKING_SPOT ps where ps.parkingSpotId=:psId", PARKING_SPOT.class)
//                        .setParameter("psId", parkingSpotId).getSingleResult();
//            } catch (EntityNotFoundException e) {
//                return null;
//            }
//            em.remove(parking_spot);
//            em.getTransaction().commit();
//            return parking_spot;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }

    }

    @Override
    public Object createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {
//        EntityManager em = emf.createEntityManager();
//        try {
//            User user;
//            try {
//                user = em.find(User.class, userId);
//                List<CAR> lis = em.createQuery("select c from CAR c", CAR.class).getResultList();
//                if (lis != null) {
//                    for (CAR c : lis) {
//                        if (c.getVehicleRegistrationPlate().equals(vehicleRegistrationPlate)) {
//                            return null;
//                        }
//
//                    }
//                }
//            } catch (EntityNotFoundException e) {
//                return null;
//            }
//            CAR car = new CAR(brand, model, colour, vehicleRegistrationPlate);
//            car.setUser(user);
//            user.addCar(car);
//            em.getTransaction().begin();
//            em.persist(car);
//            em.getTransaction().commit();
//            return car;
//        } catch (EntityNotFoundException e) {
//            return null;
//        }


        EntityManager em = emf.createEntityManager();
        try {
            List<CAR> cars = em.createQuery("select c from CAR c", CAR.class).getResultList();
            if (!cars.isEmpty()) {
                for (CAR cr : cars) {
                    if (cr.getVehicleRegistrationPlate().equals(vehicleRegistrationPlate)) {
                        return null;
                    }
                }
            }

            User user= em.find(User.class, userId);
            if (user == null) {
                return null;
            }
            CAR car = new CAR(brand, model, colour, vehicleRegistrationPlate);
            car.setUser(user);
            user.addCar(car);
            em.getTransaction().begin();
            em.persist(car);
            em.getTransaction().commit();
            return car;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @Override
    public Object getCar(Long carId) {
        EntityManager em = emf.createEntityManager();
        return em.find(CAR.class, carId);
    }

    @Override
    public Object getCar(String vehicleRegistrationPlate) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createQuery("select c from CAR c where c.vehicleRegistrationPlate=:vRP", CAR.class);
            q.setParameter("vRP", vehicleRegistrationPlate);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Object> getCars(Long userId) {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select c from CAR c where c.user.userId=:uId", CAR.class);
        query.setParameter("uId", userId);
        return query.getResultList();
    }

    @Override
    public List<Object> getCars() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select c from CAR c", CAR.class);
        return query.getResultList();
    }

    @Override
    public Object updateCar(Object car) {
        EntityManager em = emf.createEntityManager();

        if (car instanceof CAR) {
            CAR car1 = (CAR) car;
//            if (car1.getUser() == null) {
//                return null;
//            }
            CAR car2 = em.find(CAR.class, car1.getCarId());
            em.getTransaction().begin();
            if (car2 == null) {
                List<CAR> cars = em.createQuery("select c from CAR c", CAR.class).getResultList();
                for (CAR c : cars) {
                    if (c.getVehicleRegistrationPlate().equals(car1.getVehicleRegistrationPlate())) {
                        return null;
                    }
                }
                em.persist(car);
            } else {
                List<CAR> cars = em.createQuery("select c from CAR c", CAR.class).getResultList();
                for (CAR c : cars) {
                    if (c.getVehicleRegistrationPlate().equals(car1.getVehicleRegistrationPlate())) {
                        return null;
                    }
                }
                if (car1.getBrand() != null) {
                    car2.setBrand(car1.getBrand());
                }
                if (car1.getModel() != null) {
                    car2.setModel(car1.getModel());
                }
                if (car1.getColor() != null) {
                    car2.setColor(car1.getColor());
                }
                if (car1.getVehicleRegistrationPlate() != null) {
                    car2.setVehicleRegistrationPlate(car1.getVehicleRegistrationPlate());
                }

            }
            em.getTransaction().commit();
            return car;
        }

        return null;
    }

    @Override
    public Object deleteCar(Long carId) {
        EntityManager em = emf.createEntityManager();
        try {
            CAR car1;
            Query query;
            Query query1;
            Query query2;
            List<CAR> cars;
            try {
                query = em.createQuery("update Reservation rs set rs.car = null" +
                        " where rs.car.carId=:carId", Reservation.class);
                query.setParameter("carId", carId);

                query1 = em.createQuery("select c from CAR c where c.reservation in (select rs from Reservation rs where rs.car.carId=:carId)", CAR.class);
                query1.setParameter("carId", carId);
                cars = query1.getResultList();

                query2 = em.createQuery("select r from Reservation r where r.car.carId=:carId", Reservation.class);
                query2.setParameter("carId", carId);

                car1 = em.getReference(CAR.class,carId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            List<Reservation> rever = query2.getResultList();
            for (Reservation r : rever) {
                endReservation(r.getReservationId());
                cars.get(0).removeReservation(r);
            }
            em.getTransaction().begin();

            query.executeUpdate();
            em.remove(car1);
            em.getTransaction().commit();
            return car1;
        } finally {
            if (em != null) {
                em.close();
            }
        }


    }

    @Override
    public Object createUser(String firstname, String lastname, String email) {
        EntityManager em = emf.createEntityManager();
        List<User> users = em.createQuery("select u from User u", User.class).getResultList();
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                return null;
            }
        }
        try {
            User user = new User(firstname, lastname, email);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();
            return user;
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    @Override
    public Object getUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        return em.find(User.class, userId);
    }

    @Override
    public Object getUser(String email) {
        EntityManager em = emf.createEntityManager();

        try {
            Query q = em.createQuery("select u from User u where u.email=:email", User.class);
            q.setParameter("email", email);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Object> getUsers() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select u from User u", User.class);
        return query.getResultList();
    }

    @Override
    public Object updateUser(Object user) {
        EntityManager em = emf.createEntityManager();

        if (user instanceof User) {
            User user1 = (User) user;
            User user2 = em.find(User.class, user1.getUserId());
            em.getTransaction().begin();
            List<User> users = em.createQuery("select u from User u", User.class).getResultList();
            if (user2 == null) {
                for (User u : users) {
                    if (u.getEmail().equals(user1.getEmail())) {
                        return null;
                    }
                }
                em.persist(user);
            } else {
                for (User u : users) {
                    if (u.getEmail().equals(user1.getEmail())) {
                        return null;
                    }
                }
                if (user1.getFirstname() != null) {
                    user2.setFirstname(user1.getFirstname());
                }
                if (user1.getLastname() != null) {
                    user2.setLastname(user1.getLastname());
                }
                if (user1.getEmail() != null) {
                    user2.setEmail(user1.getEmail());
                }
            }
            em.getTransaction().commit();
            return user;
        }

        return null;
    }

    @Override
    public Object deleteUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user;
            Query query;
            Query query1;
            Query query2;
            List<CAR> cars;
            try {
                query = em.createQuery("update Reservation rs set rs.car = null" +
                        " where rs.car.user.userId=:uI", Reservation.class);
                query.setParameter("uI", userId);

                query1 = em.createQuery("select c from CAR c where c.reservation in (select rs from Reservation rs where rs.car.user.userId=:userId)", CAR.class);
                query1.setParameter("userId", userId);
                cars = query1.getResultList();

                query2 = em.createQuery("select r from Reservation r where r.car.user.userId=:usId", Reservation.class);
                query2.setParameter("usId", userId);

                user = em.getReference(User.class, userId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            List<Reservation> rever = query2.getResultList();
            for (Reservation r : rever) {
                endReservation(r.getReservationId());
                cars.get(0).removeReservation(r);
            }

            query.executeUpdate();
            em.remove(user);
            em.getTransaction().commit();
            return user;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Object createReservation(Long parkingSpotId, Long cardId) {
        EntityManager em = emf.createEntityManager();
        try {
            PARKING_SPOT parking_spot;
            CAR car;
            try {
                parking_spot = em.find(PARKING_SPOT.class, parkingSpotId);
                car = em.find(CAR.class, cardId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            Reservation reservation = new Reservation(new Date());
            try {
                if (parking_spot.getReservation().isEmpty()) {
                    if (car.getReservation().isEmpty()) {
                        reservation.setParking_spot(parking_spot);
                        reservation.setCar(car);
                        parking_spot.addReservation(reservation);
                        car.addReservation(reservation);
                        em.getTransaction().begin();
                        em.persist(reservation);
                        em.getTransaction().commit();
                        return reservation;
                    }
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    @Override
    public Object endReservation(Long reservationId) {
        EntityManager em = emf.createEntityManager();
        int chargedTime = 0;

        try {
            Reservation reservation = em.find(Reservation.class, reservationId);
            if (reservation.getEndDate() != null) {
                return null;
            }

            Query queryH = em.createQuery("select h from Holiday h", Holiday.class);
            System.out.println("star date "+reservation.getDate());
            List<Holiday> holidays = queryH.getResultList();

            chargedTime = hoursDifference1(reservation.getDate(), new Date());
            Query query = em.createQuery("select ps.car_park_floor.car_park.pricePerHour from PARKING_SPOT ps where ps.reservations in (select r from Reservation r where r.reservationId=:rId)", PARKING_SPOT.class);
            query.setParameter("rId", reservationId);
            Integer perHour = (Integer) query.getSingleResult();
            int totalPrice = chargedTime * perHour;

            int discount = 0;
            double newPrice = 0.0;

            if (holidays != null) {
                Calendar startCalender = Calendar.getInstance();
                startCalender.setTime(reservation.getDate());
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(new Date());

                for (; startCalender.compareTo(endCalendar) <= 0;
                     startCalender.add(Calendar.DATE, 1)) {
                    for (Holiday hs : holidays) {
                        if (isSameDay(startCalender.getTime(), hs.getDate())) {
                            discount++;
                        }
                    }
                }
                int discountDay = discount * 24 * perHour;
                totalPrice = totalPrice - discountDay;
                double discountPrice = discount * 24 * perHour * 0.25;
                double totalDisPrice = discountDay - discountPrice;
                newPrice = totalPrice + totalDisPrice;
            }
            em.getTransaction().begin();
            reservation.getCar().setReservation(null);
            reservation.getParking_spot().setReservation(null);
            reservation.setEndDate(new Date());
            reservation.setPrice(newPrice);
            em.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<Object> getReservations(Long parkingSpotId, Date date) {
        EntityManager em = emf.createEntityManager();
        List<Reservation> re = new ArrayList<>();
        try {
            Query query = em.createQuery("select re from Reservation re where re.parking_spot.parkingSpotId=:psId", Reservation.class);
            query.setParameter("psId", parkingSpotId);
            re = query.getResultList();
            if (!re.isEmpty()) {
                    re.removeIf(p -> !isSameDay(p.getDate(), date)) ;
                    return Arrays.asList(re.toArray());
            }
        } catch (NoResultException e) {
            return Arrays.asList(re.toArray());
        }
        return Arrays.asList(re.toArray());

    }

    @Override
    public List<Object> getReservations() {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select rs from Reservation rs",Reservation.class);
            List<Reservation> reservations = query.getResultList();
            return Arrays.asList(reservations.toArray());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Object> getMyReservations(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createQuery("select rs from Reservation rs where rs.car.user.userId=:uId", Reservation.class);
            query.setParameter("uId", userId);
            return (List<Object>) query.getResultList();
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public Object getReservation(Long reserveId) {
        EntityManager em = emf.createEntityManager();
        return em.find(Reservation.class, reserveId);

    }

    @Override
    public Object updateReservation(Object reservation) {
        EntityManager em = emf.createEntityManager();

        if (reservation instanceof Reservation) {
            Reservation reservation1 = (Reservation) reservation;
            Reservation reservation2 = em.find(Reservation.class, reservation1.getReservationId());
            em.getTransaction().begin();
            List<Reservation> reservations = em.createQuery("select r from Reservation r", Reservation.class).getResultList();
            if (reservation2 == null) {
                for (Reservation r : reservations) {
                    if (reservation1.getParking_spot() != null) {
                        if (r.getParking_spot().getParkingSpotId().equals(reservation1.getParking_spot().getParkingSpotId()) &&
                                r.getEndDate() == null) {
                            return null;
                        }
                    }
                }
                em.persist(reservation);
            } else {
                for (Reservation r : reservations) {
                    if (reservation1.getParking_spot() != null) {
                        if (r.getParking_spot().getParkingSpotId().equals(reservation1.getParking_spot().getParkingSpotId())
                                && r.getEndDate() == null) {
                            return null;
                        }
                    }
                }
                if (reservation1.getParking_spot() != null) {
                    reservation2.setParking_spot(reservation1.getParking_spot());
                }
                if (reservation1.getCar() != null) {
                    reservation2.setCar(reservation1.getCar());
                }
                if (reservation1.getDate() != null) {
                    reservation2.setDate(reservation1.getDate());
                }
                endReservation1(reservation1.getReservationId(), reservation1.getDate(), reservation2.getEndDate());


            }
            em.getTransaction().commit();
            return reservation;
        }

        return null;

    }

    @Override
    public Object createHoliday(String name, Date date) {
        EntityManager em = emf.createEntityManager();
        List<Holiday> holidays =getHolidays().stream().filter(Holiday.class::isInstance)
                .map(Holiday.class::cast).collect(Collectors.toList());
        if (!holidays.isEmpty()) {
            for (Holiday h : holidays) {
                if (h.getName().equals(name) && h.getDate().equals(date)) {
                    return null;
                }
            }
        }
        try {
            Holiday holiday = new Holiday(name, date);
            em.getTransaction().begin();
            em.persist(holiday);
            em.getTransaction().commit();
            em.close();
            return holiday;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object getHoliday(Date date) {
        EntityManager em = emf.createEntityManager();
        try {
            Query q = em.createQuery("select h from Holiday h where h.date=:d", Holiday.class);
            q.setParameter("d", date);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Object> getHolidays() {
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("select h from Holiday h", Holiday.class);
        return (List<Object>) query.getResultList();
    }

    @Override
    public Object deleteHoliday(Long holidayId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Holiday holiday;
            try {
                holiday = em.getReference(Holiday.class, holidayId);
            } catch (EntityNotFoundException e) {
                return null;
            }
            em.remove(holiday);
            em.getTransaction().commit();
            return holiday;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static int hoursDifference1(Date startDate, Date endDate) {

        ZonedDateTime start = ZonedDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        ZonedDateTime end = ZonedDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        Duration total = Duration.ofMinutes(ChronoUnit.MINUTES.between(start, end));
        long hours = total.toHours();
        long diff = endDate.getTime() - startDate.getTime();//as given
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        if (seconds > 0) {
            hours++;
        }
        return (int) hours;
    }

    public static Date toDate(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m - 1, d, 0, 0, 0);

        return calendar.getTime();
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        boolean sameYear = calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
        boolean sameMonth = calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH);
        boolean sameDay = calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
        return (sameDay && sameMonth && sameYear);
    }

    public void endReservation1(Long reservationId, Date strDate, Date endDate) {
        EntityManager em = emf.createEntityManager();
        int chargedTime = 0;

        try {
            Reservation reservation = em.find(Reservation.class, reservationId);


            Query queryH = em.createQuery("select h from Holiday h", Holiday.class);
            List<Holiday> holidays = queryH.getResultList();

            chargedTime = hoursDifference1(strDate, endDate);
            Query query = em.createQuery("select ps.car_park_floor.car_park.pricePerHour from PARKING_SPOT ps where ps.reservations in (select r from Reservation r where r.reservationId=:rId)", PARKING_SPOT.class);
            query.setParameter("rId", reservationId);
            Integer perHour = (Integer) query.getSingleResult();
            int totalPrice = chargedTime * perHour;


            int discount = 0;
            double newPrice = 0.0;

            if (holidays != null) {
                Calendar startCalender = Calendar.getInstance();
                startCalender.setTime(strDate);
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);

                for (; startCalender.compareTo(endCalendar) <= 0;
                     startCalender.add(Calendar.DATE, 1)) {
                    for (Holiday hs : holidays) {
                        if (isSameDay(startCalender.getTime(), hs.getDate())) {
                            discount++;
                        }
                    }
                }
                int discountDay = discount * 24 * perHour;
                totalPrice = totalPrice - discountDay;
                double discountPrice = discount * 24 * perHour * 0.25;
                double totalDisPrice = discountDay - discountPrice;
                newPrice = totalPrice + totalDisPrice;
            }
            em.getTransaction().begin();
            reservation.getCar().setReservation(null);
            reservation.getParking_spot().setReservation(null);
            reservation.setEndDate(endDate);
            reservation.setPrice(newPrice);
            em.getTransaction().commit();
        } catch (Exception ignored) {
        }

    }

}
