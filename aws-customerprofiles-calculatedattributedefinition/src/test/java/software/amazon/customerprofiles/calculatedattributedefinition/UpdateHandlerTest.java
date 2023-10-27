package software.amazon.customerprofiles.calculatedattributedefinition;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AttributeDetails;
import software.amazon.awssdk.services.customerprofiles.model.AttributeItem;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.Statistic;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.Unit;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.UpdateCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateCalculatedAttributeDefinitionResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String CALCULATED_ATTRIBUTE_NAME = "calculatedAttributeName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DESCRIPTION = "description";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String EXPRESSION = "expression";
    private static final int RANGE_VALUE = 30;
    private static final Map<String, String> PREVIOUS_TAGS = ImmutableMap.of("key1", "value1", "key2", "value2");
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key2", "newValue2", "key3", "value3");

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    private static ResourceModel model;
    private static Conditions conditions;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);

        conditions = Conditions.builder()
                .range(Range.builder()
                        .value(RANGE_VALUE)
                        .unit(Unit.DAYS.name())
                        .build())
                .build();
        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .conditions(conditions)
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse = buildUpdateDefinitionResponse(DESIRED_TAGS);
        Mockito.doReturn(updateDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getCalculatedAttributeName()).isEqualTo(CALCULATED_ATTRIBUTE_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getConditions()).isEqualTo(conditions);
        assertThat(response.getResourceModel().getTags()).isEqualTo(Translator.mapTagsToSet(DESIRED_TAGS));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andPreviousResourceTagsIsNull() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse = buildUpdateDefinitionResponse(DESIRED_TAGS);
        Mockito.doReturn(updateDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
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
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse = buildUpdateDefinitionResponse(DESIRED_TAGS);
        Mockito.doReturn(updateDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .previousResourceTags(ImmutableMap.of())
                .build();

        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
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
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse = buildUpdateDefinitionResponse(null);
        Mockito.doReturn(updateDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
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

        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse = buildUpdateDefinitionResponse(null);
        Mockito.doReturn(updateDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(ImmutableMap.of())
                .build();

        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
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
    public void handleRequest_andGetDefinition_hasBadRequestException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andGetDefinition_hasInternalServerException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andGetDefinition_hasResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andGetDefinition_hasOtherException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andUpdateDefinition_hasBadRequestException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andUpdateDefinition_hasInternalServerException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andUpdateDefinition_hasResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);
        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andUpdateDefinition_hasOtherException() {
        final UpdateHandler handler = new UpdateHandler();
        Mockito.doReturn(GetCalculatedAttributeDefinitionResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    private UpdateCalculatedAttributeDefinitionResponse buildUpdateDefinitionResponse(Map<String, String> tags) {
        return UpdateCalculatedAttributeDefinitionResponse.builder()
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .statistic(Statistic.AVERAGE)
                .attributeDetails(AttributeDetails.builder()
                        .attributes(AttributeItem.builder()
                                .name(ATTRIBUTE_NAME)
                                .build())
                        .expression(EXPRESSION)
                        .build())
                .conditions(Translator.translateFromInternalConditions(conditions))
                .tags(tags)
                .build();
    }
}
