package repository.mongo;

import db.MongoDbManager;
import model.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SportsFacilityMongoRepositoryTest {

    private static SportsFacilityMongoRepository facilityRepo;

    @BeforeAll
    static void setup() {
        MongoDbManager.init();
    }

    @AfterAll
    static void tearDown() {
        MongoDbManager.close();
    }

    @BeforeEach
    void setupEach() {
        MongoDbManager.getDatabase().getCollection("facilities").drop();
        facilityRepo = new SportsFacilityMongoRepository();
    }

    @Test
    @Order(1)
    void testSaveAndFindPolymorphic_Gym() {
        Gym gym = new Gym("Siłownia Gold", 50.0, 20, 300, true);
        facilityRepo.save(gym);

        Optional<SportsFacility> found = facilityRepo.findById(gym.getId());

        assertTrue(found.isPresent());
        // Weryfikacja polimorfizmu - czy odzyskany obiekt jest instancją Gym
        assertInstanceOf(Gym.class, found.get(), "Odzyskany obiekt nie jest siłownią (Gym)");

        Gym foundGym = (Gym) found.get();
        assertEquals("Siłownia Gold", foundGym.getName());
        assertEquals(300, foundGym.getAreaInSqm());
        assertTrue(foundGym.isHasSauna());
    }

    @Test
    @Order(2)
    void testSaveAndFindPolymorphic_SwimmingPool() {
        SwimmingPool pool = new SwimmingPool("Pływalnia Fala", 80.0, 50, 25, 6);
        facilityRepo.save(pool);

        Optional<SportsFacility> found = facilityRepo.findById(pool.getId());

        assertTrue(found.isPresent());
        assertInstanceOf(SwimmingPool.class, found.get(), "Odzyskany obiekt nie jest basenem (SwimmingPool)");

        SwimmingPool foundPool = (SwimmingPool) found.get();
        assertEquals("Pływalnia Fala", foundPool.getName());
        assertEquals(25, foundPool.getPoolLength());
        assertEquals(6, foundPool.getNumberOfLanes());
    }

    @Test
    @Order(3)
    void testSaveAndFindPolymorphic_TennisCourt() {
        TennisCourt court = new TennisCourt("Korty Rakieta", 65.0, 4, SurfaceType.CLAY, false);
        facilityRepo.save(court);

        Optional<SportsFacility> found = facilityRepo.findById(court.getId());

        assertTrue(found.isPresent());
        assertInstanceOf(TennisCourt.class, found.get(), "Odzyskany obiekt nie jest kortem (TennisCourt)");

        TennisCourt foundCourt = (TennisCourt) found.get();
        assertEquals("Korty Rakieta", foundCourt.getName());
        assertEquals(SurfaceType.CLAY, foundCourt.getSurfaceType());
        assertFalse(foundCourt.isIndoor());
    }

    @Test
    @Order(4)
    void testFindAllPolymorphic() {
        Gym gym = new Gym("Siłownia", 50.0, 20, 300, true);
        SwimmingPool pool = new SwimmingPool("Basen", 80.0, 50, 25, 6);
        TennisCourt court = new TennisCourt("Korty", 65.0, 4, SurfaceType.HARD, true);

        facilityRepo.save(gym);
        facilityRepo.save(pool);
        facilityRepo.save(court);

        List<SportsFacility> facilities = facilityRepo.findAll();
        assertEquals(3, facilities.size());

        // Weryfikujemy, czy lista zawiera obiekty różnych, poprawnych typów
        assertTrue(facilities.stream().anyMatch(f -> f instanceof Gym));
        assertTrue(facilities.stream().anyMatch(f -> f instanceof SwimmingPool));
        assertTrue(facilities.stream().anyMatch(f -> f instanceof TennisCourt));
    }
}