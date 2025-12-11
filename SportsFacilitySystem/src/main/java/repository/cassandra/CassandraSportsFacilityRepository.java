package repository.cassandra;

import model.*;
import model.cassandra.SportsFacilityEntity;
import repository.SportsFacilityRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CassandraSportsFacilityRepository implements SportsFacilityRepository {

    private final SportsFacilityDao dao;

    public CassandraSportsFacilityRepository(SportsFacilityDao dao) {
        this.dao = dao;
    }

    @Override
    public SportsFacility save(SportsFacility facility) {
        SportsFacilityEntity entity = new SportsFacilityEntity(
                facility.getId(),
                facility.getName(),
                facility.getPricePerHour(),
                facility.getCapacity()
        );

        if (facility instanceof TennisCourt) {
            TennisCourt court = (TennisCourt) facility;
            entity.setFacilityType("TENNIS_COURT");
            entity.setSurfaceType(court.getSurfaceType().name());
            entity.setIsIndoor(court.isIndoor());
        } else if (facility instanceof Gym) {
            Gym gym = (Gym) facility;
            entity.setFacilityType("GYM");
            entity.setAreaInSqm(gym.getAreaInSqm());
            entity.setHasSauna(gym.isHasSauna());
        } else if (facility instanceof SwimmingPool) {
            SwimmingPool swimmingPool = (SwimmingPool) facility;
            entity.setFacilityType("SWIMMING_POOL");
            entity.setPoolLength(swimmingPool.getPoolLength());
            entity.setNumberOfLanes(swimmingPool.getNumberOfLanes());
        }

        dao.save(entity);
        return facility;
    }

    @Override
    public Optional<SportsFacility> findById(UUID id) {
        SportsFacilityEntity entity = dao.findById(id);
        if (entity == null) {
            return Optional.empty();
        }

        SportsFacility facility = null;
        String type = entity.getFacilityType();

        if ("TENNIS_COURT".equals(type)) {
            facility = new TennisCourt(
                    entity.getName(),
                    entity.getPricePerHour(),
                    entity.getCapacity(),
                    entity.getSurfaceType() != null ? SurfaceType.valueOf(entity.getSurfaceType()) : SurfaceType.CLAY,
                    entity.getIsIndoor() != null ? entity.getIsIndoor() : false
            );
        } else if ("GYM".equals(type)) {
            facility = new Gym(
                    entity.getName(),
                    entity.getPricePerHour(),
                    entity.getCapacity(),
                    entity.getAreaInSqm() != null ? entity.getAreaInSqm() : 0,
                    entity.getHasSauna() != null ? entity.getHasSauna() : false);

        } else if ("SWIMMING_POOL".equals(type)) {
            facility = new SwimmingPool(
                    entity.getName(),
                    entity.getPricePerHour(),
                    entity.getCapacity(),
                    entity.getPoolLength() != null ? entity.getPoolLength() : 0,
                    entity.getNumberOfLanes() != null ? entity.getNumberOfLanes() : 0
            );
        } else {
            throw new IllegalStateException("Nieznany typ obiektu w bazie: " + type);
        }

        facility.setId(entity.getId());
        return Optional.of(facility);
    }

    @Override
    public List<SportsFacility> findAll() {
        throw new UnsupportedOperationException("findAll not supported");
    }

    @Override
    public void deleteById(UUID id) {
        SportsFacilityEntity entity = new SportsFacilityEntity();
        entity.setId(id);
        dao.delete(entity);
    }
}