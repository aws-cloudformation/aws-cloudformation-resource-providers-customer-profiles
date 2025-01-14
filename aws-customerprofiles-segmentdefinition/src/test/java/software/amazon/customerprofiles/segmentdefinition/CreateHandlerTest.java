package software.amazon.customerprofiles.segmentdefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.customerprofiles.segmentdefinition.CreateHandler.DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AccessDeniedException;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateSegmentDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends AbstractTestBase {

    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String SEGMENT_DEFINITION_NAME = "segmentDefinitionName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DESCRIPTION = "description";
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key1", "value1", "key2",
            "value2");

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
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateSegmentDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(
                DESIRED_TAGS);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        assertThat(response.getResourceModel().getSegmentDefinitionName()).isEqualTo(
                SEGMENT_DEFINITION_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateSegmentDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(
                null);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getSegmentDefinitionName()).isEqualTo(
                SEGMENT_DEFINITION_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getTags()).isNull();

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsEmpty() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateSegmentDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(
                null);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(ImmutableMap.of())
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getTags()).isNull();

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andCalculatedAttributeDefinitionAlreadyExists() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder()
                .message(String.format(DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE, SEGMENT_DEFINITION_NAME))
                .build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder()
                .message("BadRequestException")
                .build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnServiceInternalErrorException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final CreateHandler handler = new CreateHandler();
        ThrottlingException exception = Mockito.mock(ThrottlingException.class);
        Mockito.when(exception.getMessage()).thenReturn("throttling");
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnGeneralServiceException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andTaggingExceptionMessage_thenThrowCfnUnauthorizedTaggingOperationException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        AccessDeniedException exception = Mockito.mock(AccessDeniedException.class);
        Mockito.when(exception.getMessage()).thenReturn("is not authorized to perform profile:TagResource");
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        assertThrows(
                CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    private CreateSegmentDefinitionResponse buildCreateDefinitionResponse(
            Map<String, String> tags) {
        return CreateSegmentDefinitionResponse.builder()
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .createdAt(TIME)
                .tags(tags)
                .build();
    }
}