package software.amazon.customerprofiles.segmentdefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

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
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceResponse;
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
public class UpdateHandlerTest extends AbstractTestBase {

    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String SEGMENT_DEFINITION_NAME = "segmentDefinitionName";
    private static final String SEGMENT_DEFINITION_ARN = "arn:aws:profiles:us-east-1:123456789012:domains/domainName/segment-definitions/segmentDefinitionName";
    private static final Map<String, String> PREVIOUS_TAGS = ImmutableMap.of("key1", "value1", "key2",
            "value2");
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key2", "newValue2",
            "key3", "value3");
    private ResourceModel model;
    GetSegmentDefinitionResponse getDefinitionResponse;
    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);

        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .build();

        getDefinitionResponse = GetSegmentDefinitionResponse.builder()
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .segmentDefinitionArn(SEGMENT_DEFINITION_ARN)
                .createdAt(TIME)
                .tags(DESIRED_TAGS)
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(
                        UntagResourceRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(
                        TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isEqualTo(
                Translator.mapTagsToSet(DESIRED_TAGS));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andPreviousResourceTagsIsNull() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);
        Mockito.verify(proxy, Mockito.times(0))
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isEqualTo(
                Translator.mapTagsToSet(DESIRED_TAGS));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andPreviousResourceTagsIsEmpty() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .previousResourceTags(ImmutableMap.of())
                .build();

        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);
        Mockito.verify(proxy, Mockito.times(0))
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isEqualTo(
                Translator.mapTagsToSet(DESIRED_TAGS));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsNull() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        GetSegmentDefinitionResponse getDefinitionResponse = GetSegmentDefinitionResponse.builder()
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .segmentDefinitionArn(SEGMENT_DEFINITION_ARN)
                .createdAt(TIME)
                .tags(null)
                .build();

        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, Mockito.times(0))
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsEmpty() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(ImmutableMap.of())
                .build();

        GetSegmentDefinitionResponse getDefinitionResponse = GetSegmentDefinitionResponse.builder()
                .segmentDefinitionName(SEGMENT_DEFINITION_NAME)
                .segmentDefinitionArn(SEGMENT_DEFINITION_ARN)
                .createdAt(TIME)
                .tags(null)
                .build();

        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy)
                .injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy,
                request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, Mockito.times(0))
                .injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andGetSegmentDefinition_hasBadRequestException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnInvalidRequestException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andGetSegmentDefinition_hasInternalServerException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnServiceInternalErrorException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andGetSegmentDefinition_hasResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(
                CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_hasOtherException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        ThrottlingException exception = Mockito.mock(ThrottlingException.class);
        Mockito.when(exception.getMessage()).thenReturn("throttling");
        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, times(0)).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andTagSupportDeniedForUnTag_thenThrowCfnUnauthorizedTaggingOperationException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        AccessDeniedException exception = Mockito.mock(AccessDeniedException.class);
        Mockito.when(exception.getMessage()).thenReturn("is not authorized to perform profile:UntagResource");
        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, times(0)).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        assertThrows(
                CfnUnauthorizedTaggingOperationException.class,
                () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andTagSupportDeniedForTag_thenThrowCfnUnauthorizedTaggingOperationException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        AccessDeniedException exception = Mockito.mock(AccessDeniedException.class);
        Mockito.when(exception.getMessage()).thenReturn("is not authorized to perform profile:TagResource");
        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetSegmentDefinitionRequest.class), any());
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());
        Mockito.verify(proxy, times(0)).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();
        assertThrows(CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
