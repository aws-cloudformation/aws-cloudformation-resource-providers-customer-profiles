package software.amazon.customerprofiles.domain;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String ACCOUNT_ID = "123456789012";
    private static final String KEY_ARN = "arn:aws:kms:us-east-1:" + ACCOUNT_ID
            + ":key/1234abcd-12ab-34cd-56ef-1234567890ab";
    private static final String QUEUE_URL = "https://queue/url";
    private static final int EXPIRATION_DAYS = 100;
    private static final String DOMAIN_NAME = "testDomainName";

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .lastUpdatedAt(TIME)
                .domainName(DOMAIN_NAME)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(getDomainResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
