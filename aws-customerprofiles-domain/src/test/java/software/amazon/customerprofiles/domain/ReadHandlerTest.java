package software.amazon.customerprofiles.domain;

import com.google.common.collect.Lists;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DomainStats;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.MatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.RuleBasedMatchingResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest {
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

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;
    @Mock
    private ClientBuilder clientBuilder;
    private MatchingResponse matchingResponse;
    private RuleBasedMatchingResponse ruleBasedMatchingResponse;

    @BeforeEach
    public void setup() {
        proxy = Mockito.mock(AmazonWebServicesClientProxy.class);
        customerProfilesClient = Mockito.mock(CustomerProfilesClient.class);
        logger = Mockito.mock(Logger.class);
        clientBuilder = Mockito.mock(ClientBuilder.class);
        List<String> RULE_FIRSTNAME_LASTNAME = Lists.newArrayList("FIRST_NAME", "LAST_NAME");
        List<List<String>> matchingAttributesList = new ArrayList<>();
        matchingAttributesList.add(RULE_FIRSTNAME_LASTNAME);
        String attributeMatchingModel = "ONE_TO_ONE";
        List<String> emailTypesSelectorList = Lists.newArrayList("EmailAddress");
        List<String> addressTypesSelectorList = Lists.newArrayList("MailingAddress", "ShippingAddress");
        List<String> phoneNumberTypesSelectorList = Lists.newArrayList("PhoneNumber", "HomePhoneNumber");
        matchingResponse = MatchingResponse.builder()
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
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        final GetDomainResponse getDomainResponse = GetDomainResponse.builder()
            .createdAt(TIME)
            .deadLetterQueueUrl(QUEUE_URL)
            .defaultEncryptionKey(KEY_ARN)
            .lastUpdatedAt(TIME)
            .domainName(DOMAIN_NAME)
            .defaultExpirationDays(EXPIRATION_DAYS)
            .matching(matchingResponse)
            .ruleBasedMatching(ruleBasedMatchingResponse)
            .stats(DomainStats.builder().build())
            .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(getDomainResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertNotNull(response.getResourceModel().getMatching());
        assertTrue(response.getResourceModel().getRuleBasedMatching().getMatchingRules().size() > 0);
        assertNotNull(response.getResourceModel().getStats());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_BadRequestException() {
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
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
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

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
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

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
        final ReadHandler handler = new ReadHandler(customerProfilesClient);

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenThrow(exc);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
