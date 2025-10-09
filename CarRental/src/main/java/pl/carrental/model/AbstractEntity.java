//package pl.carrental.model;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.*;
//
//import java.io.Serializable;
//
//@MappedSuperclass
//public abstract class AbstractEntity implements Serializable {
//    @Embedded
//    @AttributeOverride(name = "uuid", column = @Column(name = "ENTITYID"))
//    @NotNull
//    private UniqueId entityId;
//
//    @Version
//    @Column(name = "VERSION")
//    private long version;
//}
//