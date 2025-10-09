//package pl.carrental.model;
//
//import jakarta.persistence.*;
//
//import java.util.UUID;
//
//@Converter
//public class UUIDConverter implements AttributeConverter<UUID, String> {
//    @Override
//    public String convertToDatabaseColumn(UUID attribute) {
//        return attribute.toString();
//    }
//    @Override
//    public UUID convertToEntityAttribute(String dbData) {
//        return UUID.fromString(dbData);
//    }
//}
