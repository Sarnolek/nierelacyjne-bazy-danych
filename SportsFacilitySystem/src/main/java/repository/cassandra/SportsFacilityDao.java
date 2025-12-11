package repository.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import model.cassandra.SportsFacilityEntity;
import java.util.UUID;

@Dao
public interface SportsFacilityDao {

    @Insert
    void save(SportsFacilityEntity facility);

    @Select
    SportsFacilityEntity findById(UUID id);

    @Delete
    void delete(SportsFacilityEntity facility);
}