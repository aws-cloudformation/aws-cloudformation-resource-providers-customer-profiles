package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField;
import software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.StandardIdentifier;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String ACCOUNT_ID = "123456789012";
    private static final String KEY_ARN = "arn:aws:kms:us-east-1:" + ACCOUNT_ID
            + ":key/1234abcd-12ab-34cd-56ef-1234567890ab";
    private static final int EXPIRATION_DAYS = 100;
    private static final String DOMAIN_NAME = "testDomainName";
    private static final String OBJECT_TYPE_NAME = "testObjectTypeName";
    private static final String DESCRIPTION = "description";
    private static final String KEY_NAME = "domainKey";
    private static final Map<String, ObjectTypeField> fields = ImmutableMap.of("sfdcContactId",
            ObjectTypeField.builder()
                    .source("_source.Id")
                    .target("_profile.Attributes.sfdcContactId")
                    .build());
    private static final Map<String, List<ObjectTypeKey>> keys = ImmutableMap.of(
            KEY_NAME,
            Lists.newArrayList(
                    ObjectTypeKey.builder()
                            .standardIdentifiers(EnumSet.of(StandardIdentifier.UNIQUE, StandardIdentifier.PROFILE))
                            .fieldNames(Lists.newArrayList("sfdcContactId"))
                            .build()));
    private static final String TEMPLATE_ID = "templateId";
    private static final String SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("Key2", "Value4", "Key3", "Value3");

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
                .objectTypeName(OBJECT_TYPE_NAME)
                .allowProfileCreation(false)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(Translator.mapFieldsToList(fields))
                .keys(Translator.mapKeysToList(keys))
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final PutProfileObjectTypeResponse result = PutProfileObjectTypeResponse.builder()
                .allowProfileCreation(false)
                .createdAt(TIME)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(fields)
                .keys(keys)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .templateId(TEMPLATE_ID)
                .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
                .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getTemplateId()).isEqualTo(TEMPLATE_ID);
        assertThat(response.getResourceModel().getExpirationDays()).isEqualTo(EXPIRATION_DAYS);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsNotNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final PutProfileObjectTypeResponse result = PutProfileObjectTypeResponse.builder()
                .allowProfileCreation(false)
                .createdAt(TIME)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(fields)
                .keys(keys)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .templateId(TEMPLATE_ID)
                .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
                .tags(DESIRED_TAGS)
                .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

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
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getTemplateId()).isEqualTo(TEMPLATE_ID);
        assertThat(response.getResourceModel().getExpirationDays()).isEqualTo(EXPIRATION_DAYS);
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(request.getDesiredResourceTags().get("Key2"));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_whenCreatedAtIsNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final PutProfileObjectTypeResponse result = PutProfileObjectTypeResponse.builder()
            .allowProfileCreation(false)
            .createdAt(null)
            .description(DESCRIPTION)
            .encryptionKey(KEY_ARN)
            .expirationDays(EXPIRATION_DAYS)
            .fields(fields)
            .keys(keys)
            .lastUpdatedAt(null)
            .objectTypeName(OBJECT_TYPE_NAME)
            .templateId(TEMPLATE_ID)
            .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
            .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
            .message("ResourceNotFoundException")
            .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
            any(GetProfileObjectTypeRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
            any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getTemplateId()).isEqualTo(TEMPLATE_ID);
        assertThat(response.getResourceModel().getExpirationDays()).isEqualTo(EXPIRATION_DAYS);
        assertThat(response.getResourceModel().getCreatedAt()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_throwsCfnGeneralServiceExceptionWhenConvertingApiResponse() {
        try (MockedStatic<Translator> translatorMockedStatic = mockStatic(Translator.class)) {
            translatorMockedStatic.when(() -> Translator.mapKeysToList(any()))
                .thenThrow(new NullPointerException());

            final CreateHandler handler = new CreateHandler(customerProfilesClient);

            final PutProfileObjectTypeResponse result = PutProfileObjectTypeResponse.builder()
                .allowProfileCreation(false)
                .createdAt(TIME)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(fields)
                .keys(keys)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .templateId(TEMPLATE_ID)
                .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
                .build();

            ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

            Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

            Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

            final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

            assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
        }
    }

    @Test
    public void handleRequest_desiredResourceTagIsEmpty() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final PutProfileObjectTypeResponse result = PutProfileObjectTypeResponse.builder()
                .allowProfileCreation(false)
                .createdAt(TIME)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(fields)
                .keys(keys)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .templateId(TEMPLATE_ID)
                .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
                .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

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
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getTemplateId()).isEqualTo(TEMPLATE_ID);
        assertThat(response.getResourceModel().getExpirationDays()).isEqualTo(EXPIRATION_DAYS);
        assertThat(response.getResourceModel().getTags()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_ObjectTypeAlreadyExist() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final GetProfileObjectTypeResponse result = GetProfileObjectTypeResponse.builder()
                .allowProfileCreation(false)
                .createdAt(TIME)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(fields)
                .keys(keys)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .templateId(TEMPLATE_ID)
                .sourceLastUpdatedTimestampFormat(SOURCE_LAST_UPDATED_TIMESTAMP_FORMAT)
                .build();

        Mockito.doReturn(result).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_BadRequestException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_InternalServerException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_otherException() {
        final CreateHandler handler = new CreateHandler();

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetProfileObjectTypeRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(PutProfileObjectTypeRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
