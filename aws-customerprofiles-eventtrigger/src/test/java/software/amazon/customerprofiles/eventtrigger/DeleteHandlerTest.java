package software.amazon.customerprofiles.eventtrigger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AccessDeniedException;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DeleteEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.DeleteEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
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

public class DeleteHandlerTest extends AbstractTestBase {
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_TRIGGER_NAME = "eventTriggerName";
    private static final String MESSAGE = "message";

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
    public void handleRequest_simpleSuccess() {
        final DeleteHandler handler = new DeleteHandler();

        final DeleteEventTriggerResponse deleteEventTriggerResponse = DeleteEventTriggerResponse.builder().message(MESSAGE).build();
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenReturn(deleteEventTriggerResponse);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final DeleteHandler handler = new DeleteHandler();
        BadRequestException exception = BadRequestException.builder().build();
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final DeleteHandler handler = new DeleteHandler();
        InternalServerException exception = InternalServerException.builder().build();
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final DeleteHandler handler = new DeleteHandler();
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final DeleteHandler handler = new DeleteHandler();
        ThrottlingException exception = mock(ThrottlingException.class);
        when(exception.getMessage()).thenReturn("ThrottlingException");
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }

    @Test
    public void handleRequest_andTaggingExceptionMessage_thenThrowCfnUnauthorizedTaggingOperationException() {
        final DeleteHandler handler = new DeleteHandler();
        AccessDeniedException exception = mock(AccessDeniedException.class);
        when(exception.getMessage()).thenReturn("is not authorized to perform profile:TagResource");
        when(proxyClient.client().deleteEventTrigger(any(DeleteEventTriggerRequest.class))).thenThrow(exception);
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnUnauthorizedTaggingOperationException.class, () -> handler.handleRequest(proxy, request, null, proxyClient, logger));
    }
}
