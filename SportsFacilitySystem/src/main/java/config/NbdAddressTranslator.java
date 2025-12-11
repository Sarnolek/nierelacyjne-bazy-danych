package config;

import com.datastax.oss.driver.api.core.addresstranslation.AddressTranslator;
import com.datastax.oss.driver.api.core.context.DriverContext;
import java.net.InetSocketAddress;

public class NbdAddressTranslator implements AddressTranslator {

    public NbdAddressTranslator(DriverContext dctx) {
    }

    @Override
    public InetSocketAddress translate(InetSocketAddress address) {
        String hostAddress = address.getAddress().getHostAddress();
        String hostName = address.getHostName();

        return switch (hostAddress) {
            case "172.25.0.2" -> new InetSocketAddress("localhost", 9042);
            case "172.25.0.3" -> new InetSocketAddress("localhost", 9043);
            default -> address;
        };
    }

    @Override
    public void close() {

    }
}