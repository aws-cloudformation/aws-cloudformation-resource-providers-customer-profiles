package software.amazon.customerprofiles.calculatedattributedefinition;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {
    public static CustomerProfilesClient getClient() {
        return CustomerProfilesClient.builder()
                .httpClient(LambdaWrapper.HTTP_CLIENT)
                .build();
    }
}
