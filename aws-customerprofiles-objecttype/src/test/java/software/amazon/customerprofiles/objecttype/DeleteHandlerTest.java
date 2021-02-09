package software.amazon.customerprofiles.objecttype;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.DeleteProfileObjectTypeResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest {

    private static final String OBJECT_TYPE_NAME = "testObjectTypeName";
    private static final String DELETE_MESSAGE = "Deleted";
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
        final DeleteHandler handler = new DeleteHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final DeleteProfileObjectTypeResponse deleteProfileObjectTypeResponse = DeleteProfileObjectTypeResponse.builder()
                .message(DELETE_MESSAGE)
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(deleteProfileObjectTypeResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getMessage()).isEqualTo(DELETE_MESSAGE);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
