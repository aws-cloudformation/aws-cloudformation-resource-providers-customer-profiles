package software.amazon.customerprofiles.domain;

import com.google.common.collect.ImmutableMap;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.UpdateDomainRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateDomainResponse;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String ACCOUNT_ID = "123456789012";
    private static final String KEY_ARN = "arn:aws:kms:us-east-1:" + ACCOUNT_ID
            + ":key/1234abcd-12ab-34cd-56ef-1234567890ab";
    private static final String QUEUE_URL = "https://queue/url";
    private static final int EXPIRATION_DAYS = 100;
    private static final String DOMAIN_NAME = "testDomainName";
    private static final Map<String, String> PREVIOUS_TAGS = ImmutableMap.of("Key1", "Value1", "Key2", "Value2");
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("Key2", "Value4", "Key3", "Value3");

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
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .tags(DESIRED_TAGS)
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(updateDomainResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(
                DESIRED_TAGS.get(response.getResourceModel().getTags().get(0).getKey()));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
