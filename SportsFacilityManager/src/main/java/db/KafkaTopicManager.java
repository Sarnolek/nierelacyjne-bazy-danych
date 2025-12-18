package db;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class KafkaTopicManager {


    private static final String BOOTSTRAP_SERVERS = "localhost:9092,localhost:9094,localhost:9095";
    public static final String TOPIC_NAME = "rentals";

    public static void createTopic() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        try (AdminClient admin = AdminClient.create(props)) {
            Set<String> topics = admin.listTopics().names().get();

            if (!topics.contains(TOPIC_NAME)) {
                System.out.println("Tworzenie tematu Kafka: " + TOPIC_NAME);
                NewTopic newTopic = new NewTopic(TOPIC_NAME, 3, (short) 3);
                admin.createTopics(Collections.singleton(newTopic)).all().get();
                System.out.println("Temat utworzony!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBootstrapServers() {
        return BOOTSTRAP_SERVERS;
    }
}