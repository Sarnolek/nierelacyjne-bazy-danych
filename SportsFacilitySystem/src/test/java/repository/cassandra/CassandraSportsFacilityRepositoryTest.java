package repository.cassandra;

import model.Gym;
import model.SportsFacility;
import model.SwimmingPool;
import model.TennisCourt;
import model.SurfaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CassandraSportsFacilityRepositoryTest extends BaseCassandraTest {

    private CassandraSportsFacilityRepository facilityRepo;

    @BeforeEach
    void setUp() {
        session.execute("TRUNCATE facilities");
        facilityRepo = new CassandraSportsFacilityRepository(mapper.sportsFacilityDao());
    }

    @Test
    void shouldSaveAndFindTennisCourt() {
        TennisCourt court = new TennisCourt("Kort Centralny", 100.0, 4, SurfaceType.CLAY, true);

        facilityRepo.save(court);
        SportsFacility found = facilityRepo.findById(court.getId()).orElse(null);

        assertNotNull(found);
        assertTrue(found instanceof TennisCourt);
        assertEquals(court.getId(), found.getId());
        assertEquals("Kort Centralny", found.getName());
        assertEquals(SurfaceType.CLAY, ((TennisCourt) found).getSurfaceType());
        assertTrue(((TennisCourt) found).isIndoor());
    }

    @Test
    void shouldSaveAndFindGym() {
        Gym gym = new Gym("Siłownia Główna", 50.0, 20, 150, true);

        facilityRepo.save(gym);
        SportsFacility found = facilityRepo.findById(gym.getId()).orElse(null);

        assertNotNull(found);
        assertTrue(found instanceof Gym);
        assertEquals(gym.getId(), found.getId());
        assertEquals(150, ((Gym) found).getAreaInSqm());
        assertTrue(((Gym) found).isHasSauna());
    }

    @Test
    void shouldSaveAndFindSwimmingPool() {
        SwimmingPool pool = new SwimmingPool("Basen Olimpijski", 200.0, 50, 50, 10);

        facilityRepo.save(pool);
        SportsFacility found = facilityRepo.findById(pool.getId()).orElse(null);

        assertNotNull(found);
        assertTrue(found instanceof SwimmingPool);
        assertEquals(pool.getId(), found.getId());
        assertEquals(50, ((SwimmingPool) found).getPoolLength());
        assertEquals(10, ((SwimmingPool) found).getNumberOfLanes());
    }

    @Test
    void shouldDeleteFacility() {
        SportsFacility facility = new TennisCourt("Kort Boczny", 80.0, 2, SurfaceType.HARD, false);
        facilityRepo.save(facility);
        assertTrue(facilityRepo.findById(facility.getId()).isPresent());

        facilityRepo.deleteById(facility.getId());

        assertTrue(facilityRepo.findById(facility.getId()).isEmpty());
    }
}