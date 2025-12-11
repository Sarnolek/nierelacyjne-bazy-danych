package model.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import java.util.UUID;

@Entity
@CqlName("clients")
public class ClientEntity {

    @PartitionKey
    private UUID id;

    @CqlName("first_name")
    private String firstName;

    @CqlName("last_name")
    private String lastName;

    public ClientEntity() {}

    public ClientEntity(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}