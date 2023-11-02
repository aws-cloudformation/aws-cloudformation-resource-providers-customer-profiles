package software.amazon.customerprofiles.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateDomainResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.MatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.RuleBasedMatchingResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String ACCOUNT_ID = "123456789012";
    private static final String KEY_ARN = "arn:aws:kms:us-east-1:" + ACCOUNT_ID
            + ":key/1234abcd-12ab-34cd-56ef-1234567890ab";
    private static final String QUEUE_URL = "https://queue/url";
    private static final int EXPIRATION_DAYS = 100;
    private static final String DOMAIN_NAME = "testDomainName";
    private static final String conflictResolvingModel = "SOURCE";
    private static final String sourceName = "Salesforce-Account";
    private static final String dayOfTheWeek = "MONDAY";
    private static final String time = "10:00";
    private static final String s3KeyName = "domain-matching-rulebasedmatching-testing";
    private static ResourceModel model;
    private static CreateDomainResponse result;

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
        List<String> RULE_FIRSTNAME_LASTNAME = Lists.newArrayList("FIRST_NAME", "LAST_NAME");
        List<List<String>> matchingAttributesList = new ArrayList<>();
        matchingAttributesList.add(RULE_FIRSTNAME_LASTNAME);
        String attributeMatchingModel = "ONE_TO_ONE";
        List<String> emailTypesSelectorList = Lists.newArrayList("EmailAddress");
        List<String> addressTypesSelectorList = Lists.newArrayList("MailingAddress", "ShippingAddress");
        List<String> phoneNumberTypesSelectorList = Lists.newArrayList("PhoneNumber", "HomePhoneNumber");

        Matching matching = Matching.builder()
            .enabled(true)
            .autoMerging(AutoMerging.builder()
                .conflictResolution(ConflictResolution.builder().conflictResolvingModel(conflictResolvingModel).sourceName(sourceName).build())
                .enabled(true)
                .consolidation(Consolidation.builder().matchingAttributesList(matchingAttributesList).build())
                .minAllowedConfidenceScoreForMerging(0.4)
                .build())
            .exportingConfig(ExportingConfig.builder().s3Exporting(S3ExportingConfig.builder()
                .s3KeyName(s3KeyName).s3BucketName("test/").build()).build())
            .jobSchedule(JobSchedule.builder().dayOfTheWeek(dayOfTheWeek).time(time).build())
            .build();

        MatchingResponse matchingResponse = MatchingResponse.builder()
            .enabled(true)
            .autoMerging(software.amazon.awssdk.services.customerprofiles.model.AutoMerging.builder()
                .conflictResolution(
                    software.amazon.awssdk.services.customerprofiles.model.ConflictResolution.builder().conflictResolvingModel(conflictResolvingModel)
                        .sourceName(
                            sourceName).build())
                .enabled(true)
                .consolidation(
                    software.amazon.awssdk.services.customerprofiles.model.Consolidation.builder().matchingAttributesList(matchingAttributesList)
                        .build())
                .minAllowedConfidenceScoreForMerging(0.4)
                .build())
            .exportingConfig(software.amazon.awssdk.services.customerprofiles.model.ExportingConfig.builder()
                .s3Exporting(software.amazon.awssdk.services.customerprofiles.model.S3ExportingConfig.builder()
                    .s3KeyName(s3KeyName).s3BucketName("test/").build()).build())
            .jobSchedule(software.amazon.awssdk.services.customerprofiles.model.JobSchedule.builder().dayOfTheWeek(dayOfTheWeek).time(time).build())
            .build();

        RuleBasedMatching ruleBasedMatching = RuleBasedMatching.builder()
            .enabled(true)
            .exportingConfig(ExportingConfig.builder().s3Exporting(S3ExportingConfig.builder()
                .s3KeyName(s3KeyName).s3BucketName("test/").build()).build())
            .conflictResolution(ConflictResolution.builder().conflictResolvingModel(conflictResolvingModel).sourceName(sourceName).build())
            .matchingRules(Lists.newArrayList(
                MatchingRule.builder()
                    .rule(RULE_FIRSTNAME_LASTNAME)
                    .build()))
            .attributeTypesSelector(AttributeTypesSelector.builder().attributeMatchingModel(attributeMatchingModel)
                .emailAddress(emailTypesSelectorList)
                .address(addressTypesSelectorList)
                .phoneNumber(phoneNumberTypesSelectorList).build())
            .maxAllowedRuleLevelForMatching(1)
            .maxAllowedRuleLevelForMerging(1)
            .build();

        RuleBasedMatchingResponse ruleBasedMatchingResponse = RuleBasedMatchingResponse.builder()
            .enabled(true)
            .conflictResolution(
                software.amazon.awssdk.services.customerprofiles.model.ConflictResolution.builder().conflictResolvingModel(conflictResolvingModel)
                    .sourceName(
                        sourceName).build())
            .matchingRules(Lists.newArrayList(
                software.amazon.awssdk.services.customerprofiles.model.MatchingRule.builder().rule(RULE_FIRSTNAME_LASTNAME).build()))
            .attributeTypesSelector(
                software.amazon.awssdk.services.customerprofiles.model.AttributeTypesSelector.builder().attributeMatchingModel(attributeMatchingModel)
                    .emailAddress(emailTypesSelectorList)
                    .address(addressTypesSelectorList)
                    .phoneNumber(phoneNumberTypesSelectorList).build())
            .maxAllowedRuleLevelForMatching(1)
            .maxAllowedRuleLevelForMerging(1)
            .exportingConfig(software.amazon.awssdk.services.customerprofiles.model.ExportingConfig.builder()
                .s3Exporting(software.amazon.awssdk.services.customerprofiles.model.S3ExportingConfig.builder()
                    .s3KeyName(s3KeyName).s3BucketName("test/").build()).build())
            .status("ACTIVE")
            .build();

        model = ResourceModel.builder()
            .defaultEncryptionKey(KEY_ARN)
            .defaultExpirationDays(EXPIRATION_DAYS)
            .deadLetterQueueUrl(QUEUE_URL)
            .domainName(DOMAIN_NAME)
            .matching(matching)
            .ruleBasedMatching(ruleBasedMatching)
            .build();

        result = CreateDomainResponse.builder()
            .createdAt(TIME)
            .deadLetterQueueUrl(QUEUE_URL)
            .domainName(DOMAIN_NAME)
            .defaultEncryptionKey(KEY_ARN)
            .defaultExpirationDays(EXPIRATION_DAYS)
            .lastUpdatedAt(TIME)
            .matching(matchingResponse)
            .ruleBasedMatching(ruleBasedMatchingResponse)
            .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(result);

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
        assertThat(response.getResourceModel().getMatching()).isEqualTo(request.getDesiredResourceState().getMatching());
        assertThat(response.getResourceModel().getRuleBasedMatching().getMatchingRules()).isEqualTo(
            request.getDesiredResourceState().getRuleBasedMatching().getMatchingRules());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsEmpty() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(result);

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
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsNotNull() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .desiredResourceTags(ImmutableMap.of("Key", "Value"))
                .build();

        result = CreateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .domainName(DOMAIN_NAME)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .tags(ImmutableMap.of("Key", "Value"))
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(result);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(request.getDesiredResourceTags().get("Key"));
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_domainAlreadyExisted() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("Domain " + DOMAIN_NAME + " already exists")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnAlreadyExistsException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_otherBadRequestException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("Other Bad Request")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_internalServerException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_resourceNotFoundException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_otherException() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
