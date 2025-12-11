package repository.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import model.cassandra.ClientEntity;
import java.util.UUID;

@Dao
public interface ClientDao {

    @Insert
    void save(ClientEntity client);

    @Select
    ClientEntity findById(UUID id);

    @Delete
    void delete(ClientEntity client);
}