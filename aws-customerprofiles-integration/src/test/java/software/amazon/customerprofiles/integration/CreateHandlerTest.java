package software.amazon.customerprofiles.integration;

import com.google.common.collect.ImmutableMap;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AccessDeniedException;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidFlowDefinition;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("Key2", "Value4", "Key3", "Value3");
    private static final Map<String, String> OBJECT_TYPE_NAMES = ImmutableMap.of("TestEventType", "TestObjectType");

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
                .objectTypeName("testObjectTypeName")
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.doThrow(new RuntimeException()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    private static Stream<String> getConnectors() {
        return Stream.of("Salesforce", "S3", "Zendesk", "Marketo", "ServiceNow");
    }

    @ParameterizedTest
    @MethodSource("getConnectors")
    public void handleRequest_withFlowDefinition_Success(String connectorType) {
        model = ResourceModel.builder()
                .domainName("testDomainName")
                .flowDefinition(getValidFlowDefinition(connectorType))
                .objectTypeName("testObjectTypeName")
                .build();

        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_withObjectTypeNames_Success() {
        model = ResourceModel.builder()
                .domainName("testDomainName")
                .uri("arn:aws:app-integrations:us-east-1:123456789012:event-integration/EventIntegration")
                .objectTypeNames(Translator.mapObjectTypeNamesToList(OBJECT_TYPE_NAMES))
                .build();

        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .uri("arn:aws:app-integrations:us-east-1:123456789012:event-integration/EventIntegration")
                .objectTypeNames(OBJECT_TYPE_NAMES)
                .build();

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModel().getObjectTypeNames()).isEqualTo(request.getDesiredResourceState().getObjectTypeNames());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsNotNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .tags(DESIRED_TAGS)
                .build();

        Mockito.doThrow(new RuntimeException()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(request.getDesiredResourceTags().get("Key2"));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsEmpty() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        PutIntegrationResponse result = PutIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.doThrow(new RuntimeException()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(ImmutableMap.of())
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(request.getDesiredResourceState().getDomainName());
        assertThat(response.getResourceModel().getTags()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_PutIntegration_BadRequestException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_PutIntegration_ResourceNotFoundException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_PutIntegration_InternalServerException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_PutIntegration_ThrottlingException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        ThrottlingException exc = ThrottlingException.builder().build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnThrottlingException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_PutIntegration_AccessDeniedException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        AccessDeniedException exc = AccessDeniedException.builder().build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAccessDeniedException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_PutIntegration_otherException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        RuntimeException exc = new RuntimeException();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_integrationAlreadyExisted() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        GetIntegrationResponse result = GetIntegrationResponse.builder()
                .createdAt(TIME)
                .domainName("testDomainName")
                .lastUpdatedAt(TIME)
                .objectTypeName("testObjectTypeName")
                .uri("arn:aws:flow:us-east-1:123456789012:URIOfIntegration1")
                .build();

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(GetIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    // test for if a flow with the desired flow name already exists (originates as an AppFlow 409 -> Locke 400)
    @Test
    public void handleRequest_withFlowDefinition_FlowAlreadyExists() {

        model = ResourceModel.builder()
                .domainName("testDomainName")
                .flowDefinition(getValidFlowDefinition("Salesforce"))
                .objectTypeName("testObjectTypeName")
                .build();

        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        String flowAlreadyExistsMessage = "Caused by: software.amazon.awssdk.services.customerprofiles.model.BadRequestException: " +
                "Conflict executing request: Flow with name " + getValidFlowDefinition("Salesforce").getFlowName()
                + " already exists.";

        BadRequestException e = BadRequestException.builder().message(flowAlreadyExistsMessage).build();

        Mockito.doThrow(e).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }


    @Test
    public void handleRequest_withFlowDefinition_BadRequestException() {
        model = ResourceModel.builder()
                .domainName("testDomainName")
                .flowDefinition(getValidFlowDefinition("Salesforce"))
                .objectTypeName("testObjectTypeName")
                .build();

        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        BadRequestException e = BadRequestException.builder().build();

        Mockito.doThrow(e).when(proxy).injectCredentialsAndInvokeV2(
                any(PutIntegrationRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
