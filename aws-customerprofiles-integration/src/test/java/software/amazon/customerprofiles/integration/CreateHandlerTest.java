package software.amazon.customerprofiles.integration;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {
    private static final Instant TIME = Instant.now();

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
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName("testDomainName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .objectTypeName("testObjectTypeName")
                .build();

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.doThrow(new RuntimeException()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
