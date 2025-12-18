package service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import db.MongoDbManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class RentalAnalyticsConsumer implements Runnable {
    private final String consumerId;
    private final MongoCollection<Document> analyticsCollection;

    public RentalAnalyticsConsumer(String id) {
        this.consumerId = id;
        this.analyticsCollection = MongoDbManager.getDatabase().getCollection("rental_analytics");
    }

    @Override
    public void run() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9094,localhost:9095");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList("rentals"));
            System.out.println(">>> " + consumerId + " wystartował i czeka na dane...");

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(">>> " + consumerId + " odebrał z partycji " + record.partition() + ": " + record.key());

                    Document doc = new Document()
                            .append("_id", record.key())
                            .append("raw_data", Document.parse(record.value()))
                            .append("processed_by", consumerId)
                            .append("partition", record.partition());

                    analyticsCollection.replaceOne(
                            Filters.eq("_id", record.key()),
                            doc,
                            new ReplaceOptions().upsert(true)
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd konsumenta " + consumerId + ": " + e.getMessage());
        }
    }
}