package software.amazon.customerprofiles.eventtrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerSummaryItem;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListEventTriggersRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListEventTriggersResponse;
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

public class ListHandlerTest extends AbstractTestBase {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_TRIGGER_NAME_1 = "eventTriggerName1";
    private static final String EVENT_TRIGGER_NAME_2 = "eventTriggerName2";
    private static final String OBJECT_TYPE_NAME = "objectTypeName";
    private static final String DESCRIPTION_1 = "description1";
    private static final String DESCRIPTION_2 = "description2";

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
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final ListHandler handler = new ListHandler();

        EventTriggerSummaryItem item1 = EventTriggerSummaryItem.builder()
                .eventTriggerName(EVENT_TRIGGER_NAME_1)
                .objectTypeName(OBJECT_TYPE_NAME)
                .description(DESCRIPTION_1)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .build();
        EventTriggerSummaryItem item2 = EventTriggerSummaryItem.builder()
                .eventTriggerName(EVENT_TRIGGER_NAME_2)
                .objectTypeName(OBJECT_TYPE_NAME)
                .description(DESCRIPTION_2)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .build();

        final ListEventTriggersResponse listEventTriggersResponse = ListEventTriggersResponse.builder()
                .items(Lists.newArrayList(item1, item2))
                .build();
        when(proxyClient.client().listEventTriggers(any(ListEventTriggersRequest.class))).thenReturn(listEventTriggersResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getEventTriggerName()).isEqualTo(EVENT_TRIGGER_NAME_1);
        assertThat(response.getResourceModels().get(1).getEventTriggerName()).isEqualTo(EVENT_TRIGGER_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ListHandler handler = new ListHandler();
        BadRequestException exception = BadRequestException.builder().build();
        when(proxyClient.client().listEventTriggers(any(ListEventTriggersRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final ListHandler handler = new ListHandler();
        InternalServerException exception = InternalServerException.builder().build();
        when(proxyClient.client().listEventTriggers(any(ListEventTriggersRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final ListHandler handler = new ListHandler();
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().listEventTriggers(any(ListEventTriggersRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ListHandler handler = new ListHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        when(proxyClient.client().listEventTriggers(any(ListEventTriggersRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }
}
