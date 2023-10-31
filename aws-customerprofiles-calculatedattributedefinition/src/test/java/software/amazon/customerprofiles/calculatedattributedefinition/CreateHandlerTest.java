package software.amazon.customerprofiles.calculatedattributedefinition;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateCalculatedAttributeDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.Operator;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.Statistic;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.Unit;
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

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.customerprofiles.calculatedattributedefinition.CreateHandler.DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String CALCULATED_ATTRIBUTE_NAME = "calculatedAttributeName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DESCRIPTION = "description";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String EXPRESSION = "expression";
    private static final int RANGE_VALUE = 30;
    private static final String THRESHOLD_VALUE = "thresholdValue";
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("key1", "value1", "key2", "value2");

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    private static ResourceModel model;
    private static AttributeDetails attributeDetails;
    private static Conditions conditions;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);

        attributeDetails = AttributeDetails.builder()
                .attributes(Collections.singleton(AttributeItem.builder().name(ATTRIBUTE_NAME).build()))
                .expression(EXPRESSION)
                .build();
        conditions = Conditions.builder()
                .range(Range.builder()
                        .value(RANGE_VALUE)
                        .unit(Unit.DAYS.name())
                        .build())
                .threshold(Threshold.builder()
                        .value(THRESHOLD_VALUE)
                        .operator(Operator.GREATER_THAN.name())
                        .build())
                .build();

        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .attributeDetails(attributeDetails)
                .conditions(conditions)
                .statistic(Statistic.AVERAGE.name())
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateCalculatedAttributeDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(DESIRED_TAGS);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(DESIRED_TAGS)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getCalculatedAttributeName()).isEqualTo(CALCULATED_ATTRIBUTE_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getAttributeDetails()).isEqualTo(attributeDetails);
        assertThat(response.getResourceModel().getConditions()).isEqualTo(conditions);
        assertThat(response.getResourceModel().getStatistic()).isEqualTo(Statistic.AVERAGE.name());
        assertThat(response.getResourceModel().getTags()).isEqualTo(Translator.mapTagsToSet(DESIRED_TAGS));

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateCalculatedAttributeDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(null);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getCalculatedAttributeName()).isEqualTo(CALCULATED_ATTRIBUTE_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getAttributeDetails()).isEqualTo(attributeDetails);
        assertThat(response.getResourceModel().getConditions()).isEqualTo(conditions);
        assertThat(response.getResourceModel().getStatistic()).isEqualTo(Statistic.AVERAGE.name());
        assertThat(response.getResourceModel().getTags()).isNull();

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andDesiredResourceTagsIsEmpty() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final CreateCalculatedAttributeDefinitionResponse createDefinitionResponse = buildCreateDefinitionResponse(null);
        Mockito.doReturn(createDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(ImmutableMap.of())
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        // calculated attribute definition
        assertThat(response.getResourceModel().getCalculatedAttributeName()).isEqualTo(CALCULATED_ATTRIBUTE_NAME);
        assertThat(response.getResourceModel().getDisplayName()).isEqualTo(DISPLAY_NAME);
        assertThat(response.getResourceModel().getDescription()).isEqualTo(DESCRIPTION);
        assertThat(response.getResourceModel().getAttributeDetails()).isEqualTo(attributeDetails);
        assertThat(response.getResourceModel().getConditions()).isEqualTo(conditions);
        assertThat(response.getResourceModel().getStatistic()).isEqualTo(Statistic.AVERAGE.name());
        assertThat(response.getResourceModel().getTags()).isNull();

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andCalculatedAttributeDefinitionAlreadyExists() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder()
                .message(String.format(DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE, CALCULATED_ATTRIBUTE_NAME))
                .build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder()
                .message("BadRequestException")
                .build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andInternalServerException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        InternalServerException exception = InternalServerException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andResourceNotFoundException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);
        ResourceNotFoundException exception = ResourceNotFoundException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final CreateHandler handler = new CreateHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(CreateCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    private CreateCalculatedAttributeDefinitionResponse buildCreateDefinitionResponse(Map<String, String> tags) {
        return CreateCalculatedAttributeDefinitionResponse.builder()
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .attributeDetails(Translator.translateFromInternalAttributeDetails(attributeDetails))
                .conditions(Translator.translateFromInternalConditions(conditions))
                .statistic(Statistic.AVERAGE)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .tags(tags)
                .build();
    }
}
