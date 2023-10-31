package software.amazon.customerprofiles.calculatedattributedefinition;

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
import software.amazon.awssdk.services.customerprofiles.model.Conditions;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.Range;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.Statistic;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.Unit;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "domainName";
    private static final String CALCULATED_ATTRIBUTE_NAME = "calculatedAttributeName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String DESCRIPTION = "description";
    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String EXPRESSION = "expression";
    private static final int RANGE_VALUE = 30;

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    private static ResourceModel model;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);

        model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .build();
    }

    @Test
    public void handleRequest_simpleSuccess() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        final AttributeDetails attributeDetails = AttributeDetails.builder()
                .attributes(AttributeItem.builder()
                        .name(ATTRIBUTE_NAME)
                        .build())
                .expression(EXPRESSION)
                .build();
        final Conditions conditions = Conditions.builder()
                .range(Range.builder()
                        .value(RANGE_VALUE)
                        .unit(Unit.DAYS)
                        .build())
                .build();
        final GetCalculatedAttributeDefinitionResponse getDefinitionResponse = GetCalculatedAttributeDefinitionResponse.builder()
                .calculatedAttributeName(CALCULATED_ATTRIBUTE_NAME)
                .displayName(DISPLAY_NAME)
                .description(DESCRIPTION)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .statistic(Statistic.AVERAGE)
                .attributeDetails(attributeDetails)
                .conditions(conditions)
                .build();
        Mockito.doReturn(getDefinitionResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());

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
        assertThat(response.getResourceModel().getAttributeDetails()).isEqualTo(Translator.translateToInternalAttributeDetails(attributeDetails));
        assertThat(response.getResourceModel().getConditions()).isEqualTo(Translator.translateToInternalConditions(conditions));
        assertThat(response.getResourceModel().getStatistic()).isEqualTo(Statistic.AVERAGE.name());

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_andBadRequestException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);
        BadRequestException exception = BadRequestException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
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
                any(GetCalculatedAttributeDefinitionRequest.class), any());
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
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_andOtherException() {
        final ReadHandler handler = new ReadHandler();
        ThrottlingException exception = ThrottlingException.builder().build();
        Mockito.doThrow(exception).when(proxy).injectCredentialsAndInvokeV2(
                any(GetCalculatedAttributeDefinitionRequest.class), any());
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
