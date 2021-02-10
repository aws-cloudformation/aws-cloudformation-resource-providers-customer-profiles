package software.amazon.customerprofiles.objecttype;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    static CustomerProfilesClient getClient() {
        return CustomerProfilesClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
    }
}
