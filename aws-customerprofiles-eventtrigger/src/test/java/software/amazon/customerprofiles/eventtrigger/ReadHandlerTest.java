package software.amazon.customerprofiles.eventtrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.ComparisonOperator;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerCondition;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerDimension;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerLimits;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerLogicalOperator;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ObjectAttribute;
import software.amazon.awssdk.services.customerprofiles.model.Period;
import software.amazon.awssdk.services.customerprofiles.model.PeriodUnit;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandlerTest extends AbstractTestBase {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_TRIGGER_NAME = "eventTriggerName";
    private static final String OBJECT_TYPE_NAME = "objectTypeName";
    private static final String DESCRIPTION = "description";
    private static final String FIELD_NAME = "fieldName";
    private static final String VALUE = "value";
    private static final String SEGMENT_FILTER = "segmentFilter";

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    private ProxyClient<CustomerProfilesClient> proxyClient;
    @Mock
    private Logger logger;

    private ResourceModel model;

    @BeforeEach
    public void setup() {
        proxy = new AmazonWebServicesClientProxy(loggerProxy, MOCK_CREDENTIALS, () -> Duration.ofSeconds(600).toMillis());
        customerProfilesClient = mock(CustomerProfilesClient.class);
        proxyClient = MOCK_PROXY(proxy, customerProfilesClient);
        logger = mock(Logger.class);

        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .eventTriggerName(EVENT_TRIGGER_NAME)
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler();

        final ObjectAttribute objectAttribute = ObjectAttribute.builder()
                .fieldName(FIELD_NAME)
                .comparisonOperator(ComparisonOperator.EQUAL.name())
                .values(Collections.singletonList(VALUE))
                .build();
        final EventTriggerDimension eventTriggerDimension = EventTriggerDimension.builder()
                .objectAttributes(Collections.singletonList(objectAttribute))
                .build();
        final EventTriggerCondition eventTriggerCondition = EventTriggerCondition.builder()
                .eventTriggerDimensions(Collections.singletonList(eventTriggerDimension))
                .logicalOperator(EventTriggerLogicalOperator.ANY).build();

        final Period period = Period.builder()
                .unlimited(false)
                .maxInvocationsPerProfile(10)
                .unit(PeriodUnit.DAYS.name())
                .value(10)
                .build();
        final EventTriggerLimits eventTriggerLimits = EventTriggerLimits.builder()
                .eventExpiration(TIME.getEpochSecond())
                .periods(period)
                .build();

        final GetEventTriggerResponse getEventTriggerResponse = GetEventTriggerResponse.builder()
                .eventTriggerName(EVENT_TRIGGER_NAME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .description(DESCRIPTION)
                .eventTriggerConditions(eventTriggerCondition)
                .segmentFilter(SEGMENT_FILTER)
                .eventTriggerLimits(eventTriggerLimits)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenReturn(getEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // event trigger
        assertThat(response.getResourceModel().getEventTriggerName()).isEqualTo(EVENT_TRIGGER_NAME);
        assertThat(response.getResourceModel().getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getEventTriggerConditions().get(0))
                .isEqualTo(Translator.translateToInternalEventTriggerCondition(eventTriggerCondition));
        assertThat(response.getResourceModel().getSegmentFilter()).isEqualTo(SEGMENT_FILTER);
        assertThat(response.getResourceModel().getEventTriggerLimits())
                .isEqualTo(Translator.translateToInternalEventTriggerLimits(eventTriggerLimits));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ReadHandler handler = new ReadHandler();
        BadRequestException exception = BadRequestException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final ReadHandler handler = new ReadHandler();
        InternalServerException exception = InternalServerException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final ReadHandler handler = new ReadHandler();
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ReadHandler handler = new ReadHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        when(proxyClient.client().getEventTrigger(any(GetEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }
}
