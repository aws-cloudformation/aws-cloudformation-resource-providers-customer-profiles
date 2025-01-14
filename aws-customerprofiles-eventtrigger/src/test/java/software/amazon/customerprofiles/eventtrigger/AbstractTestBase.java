package software.amazon.customerprofiles.eventtrigger;

import java.util.function.Function;

import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Credentials;
import software.amazon.cloudformation.proxy.LoggerProxy;
import software.amazon.cloudformation.proxy.ProxyClient;

public class AbstractTestBase {
    protected static final Credentials MOCK_CREDENTIALS;
    protected static final LoggerProxy loggerProxy;

    static {
        MOCK_CREDENTIALS = new Credentials("accessKey", "secretKey", "token");
        loggerProxy = new LoggerProxy();
    }

    static ProxyClient<CustomerProfilesClient> MOCK_PROXY(
            final AmazonWebServicesClientProxy proxy,
            final CustomerProfilesClient client) {
        return new ProxyClient<CustomerProfilesClient>() {
            @Override
            public <RequestT extends AwsRequest, ResponseT extends AwsResponse> ResponseT
            injectCredentialsAndInvokeV2(RequestT request, Function<RequestT, ResponseT> requestFunction) {
                return proxy.injectCredentialsAndInvokeV2(request, requestFunction);
            }

            @Override
            public CustomerProfilesClient client() {
                return client;
            }
        };
    }
}
