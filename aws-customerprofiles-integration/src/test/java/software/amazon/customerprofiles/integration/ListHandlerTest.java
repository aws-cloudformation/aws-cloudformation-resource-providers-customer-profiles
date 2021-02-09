package software.amazon.customerprofiles.integration;

import com.google.common.collect.Lists;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.ListIntegrationItem;
import software.amazon.awssdk.services.customerprofiles.model.ListIntegrationsResponse;
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
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
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
        final ListHandler handler = new ListHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName("testDomainName")
                .build();

        ListIntegrationItem item1 = ListIntegrationItem.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName1")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        ListIntegrationItem item2 = ListIntegrationItem.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName2")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration2")
                .build();

        ListIntegrationsResponse result = ListIntegrationsResponse.builder()
                .items(Lists.newArrayList(item1, item2))
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(result);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getResourceModels().get(0).getObjectTypeName().equals(item1.objectTypeName()));
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
