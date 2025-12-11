package repository.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

@Mapper
public interface SportsFacilityMapper {


    static MapperBuilder<SportsFacilityMapper> builder(CqlSession session) {
        return new SportsFacilityMapperBuilder(session); //generowany prez procesor adnotacji DataStax
    }

    @DaoFactory
    ClientDao clientDao();

    @DaoFactory
    SportsFacilityDao sportsFacilityDao();

    @DaoFactory
    RentalDao rentalDao();
}