package software.amazon.customerprofiles.eventstream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DestinationSummary;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamDestinationStatus;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamState;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamSummary;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListEventStreamsRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListEventStreamsResponse;
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
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_STREAM_NAME_1 = "eventStreamName1";
    private static final String EVENT_STREAM_NAME_2 = "eventStreamName2";
    private static final String EVENT_STREAM_ARN_1 = "arn:aws:profiles:us-east-1:123456789012:domains/domainName/event-stream/eventStreamName1";
    private static final String EVENT_STREAM_ARN_2 = "arn:aws:profiles:us-east-1:123456789012:domains/domainName/event-stream/eventStreamName2";
    private static final String KINESIS_DATA_STREAM_ARN = "arn:aws:kinesis:us-east-1:123456789012:stream/testStream";
    private static ResourceModel model;
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
            .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        DestinationSummary destinationSummary =
            DestinationSummary.builder().uri(KINESIS_DATA_STREAM_ARN).status(EventStreamDestinationStatus.HEALTHY.toString()).build();

        EventStreamSummary listEventStreamItem1 = EventStreamSummary.builder()
            .domainName(DOMAIN_NAME)
            .eventStreamName(EVENT_STREAM_NAME_1)
            .eventStreamArn(EVENT_STREAM_ARN_1)
            .state(EventStreamState.RUNNING)
            .stoppedSince(TIME)
            .destinationSummary(destinationSummary)
            .build();

        EventStreamSummary listEventStreamItem2 = EventStreamSummary.builder()
            .domainName(DOMAIN_NAME)
            .eventStreamName(EVENT_STREAM_NAME_2)
            .eventStreamArn(EVENT_STREAM_ARN_2)
            .state(EventStreamState.RUNNING)
            .stoppedSince(TIME)
            .destinationSummary(destinationSummary)
            .build();

        final ListEventStreamsResponse listDefinitionsResponse =
            ListEventStreamsResponse.builder()
                .items(Lists.newArrayList(listEventStreamItem1, listEventStreamItem2))
                .build();
        Mockito.doReturn(listDefinitionsResponse).when(proxy).injectCredentialsAndInvokeV2(
            any(ListEventStreamsRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getEventStreamName()).isEqualTo(EVENT_STREAM_NAME_1);
        assertThat(response.getResourceModels().get(1).getEventStreamName()).isEqualTo(EVENT_STREAM_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListEventStreamsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListEventStreamsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListEventStreamsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(ListEventStreamsRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
