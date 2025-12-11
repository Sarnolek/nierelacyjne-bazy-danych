package config;

import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;

public class CassandraSessionManager {
    private static CqlSession session;

    public static void initSession() {
        if (session == null) {
            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress("localhost", 9042))
                    .withLocalDatacenter("dc1")
                    .withAuthCredentials("cassandra", "cassandra")
                    .build();

            System.out.println("Połączono z klastrem Cassandry!");
        }
    }

    public static CqlSession getSession() {
        if (session == null) {
            initSession();
        }
        return session;
    }

    public static void closeSession() {
        if (session != null) {
            session.close();
        }
    }
}