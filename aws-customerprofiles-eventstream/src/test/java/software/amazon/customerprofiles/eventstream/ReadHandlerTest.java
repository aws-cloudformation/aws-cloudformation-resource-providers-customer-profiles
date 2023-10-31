package software.amazon.customerprofiles.eventstream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamDestinationDetails;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamDestinationStatus;
import software.amazon.awssdk.services.customerprofiles.model.EventStreamState;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
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
public class ReadHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String EVENT_STREAM_NAME = "eventStreamName";
    private static final String EVENT_STREAM_ARN = "arn:aws:profiles:us-east-1:123456789012:domains/domainName/event-stream/eventStreamName";
    private static final String KINESIS_DATA_STREAM_ARN = "arn:aws:kinesis:us-east-1:123456789012:stream/testStream";
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key1", "value1", "key2", "value2");
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
            .eventStreamName(EVENT_STREAM_NAME)
            .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        EventStreamDestinationDetails destinationDetails =
            EventStreamDestinationDetails.builder().uri(KINESIS_DATA_STREAM_ARN).status(EventStreamDestinationStatus.HEALTHY.toString()).build();

        final GetEventStreamResponse getEventStreamResponse = GetEventStreamResponse.builder()
            .domainName(DOMAIN_NAME)
            .eventStreamArn(EVENT_STREAM_ARN)
            .createdAt(TIME)
            .state(EventStreamState.RUNNING)
            .stoppedSince(TIME)
            .destinationDetails(destinationDetails)
            .tags(DESIRED_TAGS)
            .build();
        Mockito.doReturn(getEventStreamResponse).when(proxy).injectCredentialsAndInvokeV2(
            any(GetEventStreamRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // event stream
        assertThat(response.getResourceModel().getEventStreamArn()).isEqualTo(EVENT_STREAM_ARN);
        assertThat(response.getResourceModel().getDestinationDetails().getUri()).isEqualTo(KINESIS_DATA_STREAM_ARN);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(GetEventStreamRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(GetEventStreamRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(GetEventStreamRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
            any(GetEventStreamRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}