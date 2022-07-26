package software.amazon.customerprofiles.domain;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientBuilderTest {
    private static final String CUSTOMER_PROFILES_CLIENT = "CustomerProfilesClient";

    @Test
    public void testCreateConnectClient() {
        CustomerProfilesClient client = ClientBuilder.getClient();
        assertThat(client).isNotNull();
        assertThat(client.toString().contains(CUSTOMER_PROFILES_CLIENT)).isTrue();
    }
}
