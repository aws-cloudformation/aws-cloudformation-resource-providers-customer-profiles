package software.amazon.customerprofiles.segmentdefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.google.common.collect.Lists;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListSegmentDefinitionsRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListSegmentDefinitionsResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.SegmentDefinitionItem;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String SEGMENT_DEFINITION_NAME_1 = "segmentDefinitionName1";
    private static final String SEGMENT_DEFINITION_NAME_2 = "segmentDefinitionName2";
    private static final String DISPLAY_NAME_1 = "displayName1";
    private static final String DISPLAY_NAME_2 = "displayName1";
    private static final String DESCRIPTION_1 = "description1";
    private static final String DESCRIPTION_2 = "description2";

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    private ResourceModel model;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);

        model = ResourceModel.builder()
            .domainName(DOMAIN_NAME)
            .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        SegmentDefinitionItem listDefinitionItem1 = SegmentDefinitionItem.builder()
            .segmentDefinitionName(SEGMENT_DEFINITION_NAME_1)
            .displayName(DISPLAY_NAME_1)
            .description(DESCRIPTION_1)
            .createdAt(TIME)
            .build();
        SegmentDefinitionItem listDefinitionItem2 = SegmentDefinitionItem.builder()
            .segmentDefinitionName(SEGMENT_DEFINITION_NAME_2)
            .displayName(DISPLAY_NAME_2)
            .description(DESCRIPTION_2)
            .createdAt(TIME)
            .build();

        final ListSegmentDefinitionsResponse listDefinitionsResponse =
            ListSegmentDefinitionsResponse.builder()
                .items(Lists.newArrayList(listDefinitionItem1, listDefinitionItem2))
                .build();
        Mockito.doReturn(listDefinitionsResponse).when(proxy).injectCredentialsAndInvokeV2(
            any(ListSegmentDefinitionsRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
            request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getSegmentDefinitionName()).isEqualTo(
            SEGMENT_DEFINITION_NAME_1);
        assertThat(response.getResourceModels().get(1).getSegmentDefinitionName()).isEqualTo(
            SEGMENT_DEFINITION_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListSegmentDefinitionsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(
            CfnInvalidRequestException.class,
            () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListSegmentDefinitionsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(
            CfnServiceInternalErrorException.class,
            () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListSegmentDefinitionsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(
            CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ListHandler handler = new ListHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListSegmentDefinitionsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(
            CfnGeneralServiceException.class,
            () -> handler.handleRequest(proxy, request, null, logger));
    }
}
