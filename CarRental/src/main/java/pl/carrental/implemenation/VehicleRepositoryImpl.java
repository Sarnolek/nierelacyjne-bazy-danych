package pl.carrental.implemenation;

import jakarta.persistence.EntityManager;
import pl.carrental.repository.VehicleRepository;
import pl.carrental.vehicle.Vehicle;

import java.util.List;
import java.util.Optional;

public class VehicleRepositoryImpl implements VehicleRepository {
    private final EntityManager em;

    public VehicleRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Vehicle vehicle){
        if (vehicle.getId() == null) {
            em.persist(vehicle);
        } else
            em.merge(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(Long id){
        return Optional.ofNullable(em.find(Vehicle.class, id));
    }

    @Override
    public Optional<Vehicle> findByVehicleId(Long vehicleId) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.vehicleId = :vehicleId", Vehicle.class)
                .setParameter("vehicleId", vehicleId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Vehicle> findAll(){
        return em.createQuery("SELECT v FROM Vehicle v", Vehicle.class)
                .getResultList();
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return em.createQuery("SELECT v FROM Vehicle v WHERE v.plateNumber = :plateNumber", Vehicle.class)
                .setParameter("plateNumber", plateNumber)
                .getResultStream()
                .findFirst();
    }


    @Override
    public void delete(Vehicle vehicle){
        if (em.contains(vehicle))
            em.remove(vehicle);
        else {
            Vehicle managedVehicle = em.find(Vehicle.class, vehicle.getId());
            if (managedVehicle != null){
                em.remove(managedVehicle);
            }
        }
    }
}
