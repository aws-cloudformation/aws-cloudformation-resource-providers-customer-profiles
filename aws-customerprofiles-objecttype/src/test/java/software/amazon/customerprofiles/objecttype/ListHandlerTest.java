package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.Lists;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.ListProfileObjectTypeItem;
import software.amazon.awssdk.services.customerprofiles.model.ListProfileObjectTypesResponse;
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
    private static final String DOMAIN_NAME = "testDomainName";
    private static final String DESCRIPTION_1 = "description1";
    private static final String DESCRIPTION_2 = "description2";
    private static final String OBJECT_TYPE_NAME_1 = "testObjectTypeName1";
    private static final String OBJECT_TYPE_NAME_2 = "testObjectTypeName2";


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
        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem1 = ListProfileObjectTypeItem.builder()
                .createdAt(TIME)
                .description(DESCRIPTION_1)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_1)
                .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem2 = ListProfileObjectTypeItem.builder()
                .createdAt(TIME)
                .description(DESCRIPTION_2)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_2)
                .build();

        final ListProfileObjectTypesResponse listProfileObjectTypesResponse = ListProfileObjectTypesResponse.builder()
                .items(Lists.newArrayList(listProfileObjectTypeItem1, listProfileObjectTypeItem2))
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(listProfileObjectTypesResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_1);
        assertThat(response.getResourceModels().get(1).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
