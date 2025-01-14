package software.amazon.customerprofiles.eventtrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AccessDeniedException;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.ComparisonOperator;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerLogicalOperator;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PeriodUnit;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.UpdateEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateEventTriggerResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandlerTest extends AbstractTestBase {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_TRIGGER_NAME = "eventTriggerName";
    private static final String OBJECT_TYPE_NAME = "objectTypeName";
    private static final String DESCRIPTION = "description";
    private static final String FIELD_NAME = "fieldName";
    private static final String VALUE = "value";
    private static final String SEGMENT_FILTER = "segmentFilter";
    private static final Map<String, String> PREVIOUS_TAGS = ImmutableMap.of("key1", "value1", "key2", "value2");
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key2", "newValue2", "key3", "value3");

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    private ProxyClient<CustomerProfilesClient> proxyClient;
    @Mock
    private Logger logger;

    private ResourceModel model;
    private EventTriggerCondition eventTriggerCondition;
    private EventTriggerLimits eventTriggerLimits;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        customerProfilesClient = mock(CustomerProfilesClient.class);
        proxyClient = MOCK_PROXY(proxy, customerProfilesClient);
        logger = mock(Logger.class);

        ObjectAttribute objectAttribute = ObjectAttribute.builder()
                .fieldName(FIELD_NAME)
                .comparisonOperator(ComparisonOperator.EQUAL.name())
                .values(Collections.singletonList(VALUE))
                .build();
        EventTriggerDimension eventTriggerDimension = EventTriggerDimension.builder()
                .objectAttributes(Collections.singletonList(objectAttribute))
                .build();
        eventTriggerCondition = EventTriggerCondition.builder()
                .eventTriggerDimensions(Collections.singletonList(eventTriggerDimension))
                .logicalOperator(EventTriggerLogicalOperator.ANY.name())
                .build();

        Period period = Period.builder()
                .unlimited(false)
                .maxInvocationsPerProfile(10)
                .unit(PeriodUnit.DAYS.name())
                .value(10)
                .build();
        eventTriggerLimits = EventTriggerLimits.builder()
                .eventExpiration(TIME.getEpochSecond())
                .periods(Collections.singletonList(period))
                .build();

        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .eventTriggerName(EVENT_TRIGGER_NAME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .description(DESCRIPTION)
                .eventTriggerConditions(Collections.singletonList(eventTriggerCondition))
                .segmentFilter(SEGMENT_FILTER)
                .eventTriggerLimits(eventTriggerLimits)
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final UpdateEventTriggerResponse updateEventTriggerResponse = buildUpdateEventTriggerResponse(DESIRED_TAGS);
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenReturn(updateEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);
        verify(proxyClient.client()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client()).tagResource(any(TagResourceRequest.class));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // event-trigger
        assertThat(response.getResourceModel().getEventTriggerName()).isEqualTo(EVENT_TRIGGER_NAME);
        assertThat(response.getResourceModel().getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getEventTriggerConditions().get(0)).isEqualTo(eventTriggerCondition);
        assertThat(response.getResourceModel().getSegmentFilter()).isEqualTo(SEGMENT_FILTER);
        assertThat(response.getResourceModel().getEventTriggerLimits()).isEqualTo(eventTriggerLimits);
        assertThat(response.getResourceModel().getTags()).isEqualTo(Translator.mapTagsToSet(DESIRED_TAGS));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andPreviousResourceTagsIsNull() {
        final UpdateHandler handler = new UpdateHandler();

        final UpdateEventTriggerResponse updateEventTriggerResponse = buildUpdateEventTriggerResponse(DESIRED_TAGS);
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenReturn(updateEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);
        verify(proxyClient.client(), never()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client()).tagResource(any(TagResourceRequest.class));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isEqualTo(Translator.mapTagsToSet(DESIRED_TAGS));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andPreviousResourceTagsIsEmpty() {
        final UpdateHandler handler = new UpdateHandler();

        final UpdateEventTriggerResponse updateEventTriggerResponse = buildUpdateEventTriggerResponse(DESIRED_TAGS);
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenReturn(updateEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(ImmutableMap.of())
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenReturn(TagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);
        verify(proxyClient.client(), never()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client()).tagResource(any(TagResourceRequest.class));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags()).isEqualTo(Translator.mapTagsToSet(DESIRED_TAGS));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsNull() {
        final UpdateHandler handler = new UpdateHandler();

        final UpdateEventTriggerResponse updateEventTriggerResponse = buildUpdateEventTriggerResponse(null);
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenReturn(updateEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);
        verify(proxyClient.client()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), never()).tagResource(any(TagResourceRequest.class));
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
        final UpdateHandler handler = new UpdateHandler();

        final UpdateEventTriggerResponse updateEventTriggerResponse = buildUpdateEventTriggerResponse(null);
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenReturn(updateEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(ImmutableMap.of())
                .build();

        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenReturn(UntagResourceResponse.builder().build());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);
        verify(proxyClient.client()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), never()).tagResource(any(TagResourceRequest.class));
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
    public void handleRequest_andGetEventTrigger_hasBadRequestException() {
        final UpdateHandler handler = new UpdateHandler();
        BadRequestException exception = BadRequestException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andGetEventTrigger_hasInternalServerException() {
        final UpdateHandler handler = new UpdateHandler();
        InternalServerException exception = InternalServerException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andGetEventTrigger_hasResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler();
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andGetEventTrigger_hasOtherException() {
        final UpdateHandler handler = new UpdateHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andUpdateEventTrigger_hasBadRequestException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        BadRequestException exception = BadRequestException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andUpdateEventTrigger_hasInternalServerException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        InternalServerException exception = InternalServerException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andUpdateEventTrigger_hasResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andUpdateEventTrigger_hasOtherException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        ThrottlingException exception = mock(ThrottlingException.class);
        when(exception.getMessage()).thenReturn("ThrottlingException");
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        verify(proxyClient.client(), never()).tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), never()).updateEventTrigger(any(UpdateEventTriggerRequest.class));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andPermissionDeniedForUntag_thenThrowCfnUnauthorizedTaggingOperationException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        AccessDeniedException exception = mock(AccessDeniedException.class);
        when(exception.getMessage()).thenReturn("is not authorized to perform profile:UntagResource");
        when(proxyClient.client().untagResource(any(UntagResourceRequest.class))).thenThrow(exception);
        verify(proxyClient.client(), never()).tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), never()).updateEventTrigger(any(UpdateEventTriggerRequest.class));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        assertThrows(CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andPermissionDeniedForTag_thenThrowCfnUnauthorizedTaggingOperationException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        AccessDeniedException exception = mock(AccessDeniedException.class);
        when(exception.getMessage()).thenReturn("is not authorized to perform profile:TagResource");
        when(proxyClient.client().tagResource(any(TagResourceRequest.class))).thenThrow(exception);
        verify(proxyClient.client(), never()).untagResource(any(UntagResourceRequest.class));
        verify(proxyClient.client(), never()).updateEventTrigger(any(UpdateEventTriggerRequest.class));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        assertThrows(CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andPermissionDeniedForListTags_thenThrowCfnUnauthorizedTaggingOperationException() {
        final UpdateHandler handler = new UpdateHandler();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(GetEventTriggerResponse.builder().build());
        AccessDeniedException exception = mock(AccessDeniedException.class);
        when(exception.getMessage()).thenReturn("is not authorized to perform profile:ListTagsForResource");
        when(proxyClient.client().updateEventTrigger(any(UpdateEventTriggerRequest.class))).thenThrow(exception);
        verify(proxyClient.client(), never()).tagResource(any(TagResourceRequest.class));
        verify(proxyClient.client(), never()).untagResource(any(UntagResourceRequest.class));
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    private UpdateEventTriggerResponse buildUpdateEventTriggerResponse(Map<String, String> tags) {
        return UpdateEventTriggerResponse.builder()
                .eventTriggerName(EVENT_TRIGGER_NAME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .description(DESCRIPTION)
                .eventTriggerConditions(Translator.translateFromInternalEventTriggerConditions(Collections.singletonList(eventTriggerCondition)))
                .segmentFilter(SEGMENT_FILTER)
                .eventTriggerLimits(Translator.translateFromInternalEventTriggerLimits(eventTriggerLimits))
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .tags(tags)
                .build();
    }
}
