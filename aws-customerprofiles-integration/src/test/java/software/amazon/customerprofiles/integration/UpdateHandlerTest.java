package software.amazon.customerprofiles.integration;

import com.google.common.collect.ImmutableMap;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "testDomainName";
    private static final String OBJECT_TYPE_NAME = "testObjectType";
    private static final String URI = "arn:aws:flow:us-east-1:123456789012:URIOfIntegration1";
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
                .objectTypeName(OBJECT_TYPE_NAME)
                .uri(URI)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        PutIntegrationResponse putIntegrationResponse = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName(DOMAIN_NAME)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .tags(DESIRED_TAGS)
                .uri(URI)
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(putIntegrationResponse);

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
