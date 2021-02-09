package software.amazon.customerprofiles.integration;

import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationResponse;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {
    private static final Instant TIME = Instant.now();

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
                .domainName("testDomainName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final GetIntegrationResponse result = GetIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(result);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.
                getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_InternalServerException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_BadRequestException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_otherException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
