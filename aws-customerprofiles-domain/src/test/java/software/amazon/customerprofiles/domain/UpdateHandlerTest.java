package software.amazon.customerprofiles.domain;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.MatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.RuleBasedMatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceResponse;
import software.amazon.awssdk.services.customerprofiles.model.UpdateDomainRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateDomainResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String ACCOUNT_ID = "123456789012";
    private static final String KEY_ARN = "arn:aws:kms:us-east-1:" + ACCOUNT_ID
            + ":key/1234abcd-12ab-34cd-56ef-1234567890ab";
    private static final String QUEUE_URL = "https://queue/url";
    private static final int EXPIRATION_DAYS = 100;
    private static final String DOMAIN_NAME = "testDomainName";
    private static final Map<String, String> PREVIOUS_TAGS = ImmutableMap.of("Key1", "Value1", "Key2", "Value2");
    private static final Map<String, String> DESIRED_TAGS = ImmutableMap.of("Key2", "Value4", "Key3", "Value3");
    private static final String conflictResolvingModel = "SOURCE";
    private static final String sourceName = "Salesforce-Account";
    private static final String dayOfTheWeek = "MONDAY";
    private static final String time = "10:00";
    private static final String s3KeyName = "domain-matching-rulebasedmatching-testing";
    private static ResourceModel model;

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;
    private MatchingResponse matchingResponse;
    private RuleBasedMatchingResponse ruleBasedMatchingResponse;

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

        matchingResponse = MatchingResponse.builder()
            .enabled(true)
            .autoMerging(software.amazon.awssdk.services.customerprofiles.model.AutoMerging.builder()
                .conflictResolution(
                    software.amazon.awssdk.services.customerprofiles.model.ConflictResolution.builder().conflictResolvingModel(conflictResolvingModel)
                        .sourceName(
                            sourceName).build())
                .enabled(true)
                .consolidation(
                    software.amazon.awssdk.services.customerprofiles.model.Consolidation.builder().matchingAttributesList(matchingAttributesList).build())
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

        ruleBasedMatchingResponse = RuleBasedMatchingResponse.builder()
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
            .domainName(DOMAIN_NAME)
            .deadLetterQueueUrl(QUEUE_URL)
            .defaultEncryptionKey(KEY_ARN)
            .defaultExpirationDays(EXPIRATION_DAYS)
            .matching(matching)
            .ruleBasedMatching(ruleBasedMatching)
            .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
            .createdAt(TIME)
            .domainName(DOMAIN_NAME)
            .deadLetterQueueUrl(QUEUE_URL)
            .defaultEncryptionKey(KEY_ARN)
            .defaultExpirationDays(EXPIRATION_DAYS)
            .lastUpdatedAt(TIME)
            .tags(DESIRED_TAGS)
            .matching(matchingResponse)
            .ruleBasedMatching(ruleBasedMatchingResponse)
            .build();

        Mockito.doReturn(GetDomainResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(GetDomainRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.doReturn(TagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        Mockito.doReturn(updateDomainResponse).when(proxy).injectCredentialsAndInvokeV2(any(UpdateDomainRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.verify(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any());
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getTags().get(0).getValue()).isEqualTo(
                DESIRED_TAGS.get(response.getResourceModel().getTags().get(0).getKey()));
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getMatching()).isEqualTo(request.getDesiredResourceState().getMatching());
        assertThat(response.getResourceModel().getRuleBasedMatching().getMatchingRules()).isEqualTo(
            request.getDesiredResourceState().getRuleBasedMatching().getMatchingRules());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_desiredResourceTagIsEmpty() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(ImmutableMap.of())
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .build();

        Mockito.doReturn(GetDomainResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(GetDomainRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.doReturn(updateDomainResponse).when(proxy).injectCredentialsAndInvokeV2(any(UpdateDomainRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequest_desiredResourceTagIsNull() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .build();

        Mockito.doReturn(GetDomainResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(GetDomainRequest.class), any());
        Mockito.doReturn(UntagResourceResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
        Mockito.doReturn(updateDomainResponse).when(proxy).injectCredentialsAndInvokeV2(any(UpdateDomainRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

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
    public void handleRequest_previousTagIsNull() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .build();

        Mockito.doReturn(GetDomainResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(GetDomainRequest.class), any());
        Mockito.doReturn(updateDomainResponse).when(proxy).injectCredentialsAndInvokeV2(any(UpdateDomainRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
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
    public void handleRequest_previousTagIsEmpty() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(ImmutableMap.of())
                .build();

        final UpdateDomainResponse updateDomainResponse = UpdateDomainResponse.builder()
                .createdAt(TIME)
                .deadLetterQueueUrl(QUEUE_URL)
                .defaultEncryptionKey(KEY_ARN)
                .defaultExpirationDays(EXPIRATION_DAYS)
                .lastUpdatedAt(TIME)
                .build();

        Mockito.doReturn(GetDomainResponse.builder().build()).when(proxy).injectCredentialsAndInvokeV2(any(GetDomainRequest.class), any());
        Mockito.doReturn(updateDomainResponse).when(proxy).injectCredentialsAndInvokeV2(any(UpdateDomainRequest.class), any());

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        Mockito.verify(proxy, Mockito.times(0)).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any());
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
    public void handleRequest_getDomain_BadRequestException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());


        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_getDomain_InternalServerException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_getDomain_ResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_getDomain_otherException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_updateDomain_BadRequestException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
                .build();

        Mockito.doReturn(getDomainResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateDomainRequest.class), any());

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_updateDomain_InternalServerException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
                .build();

        Mockito.doReturn(getDomainResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateDomainRequest.class), any());

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_updateDomain_ResourceNotFoundException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
                .build();

        Mockito.doReturn(getDomainResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateDomainRequest.class), any());

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_updateDomain_otherException() {
        final UpdateHandler handler = new UpdateHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceTags(PREVIOUS_TAGS)
                .desiredResourceTags(DESIRED_TAGS)
                .build();

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
                .build();

        Mockito.doReturn(getDomainResponse).when(proxy).injectCredentialsAndInvokeV2(
                any(GetDomainRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(UntagResourceRequest.class), any());

        Mockito.doReturn(null).when(proxy).injectCredentialsAndInvokeV2(
                any(TagResourceRequest.class), any());

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(
                any(UpdateDomainRequest.class), any());

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
