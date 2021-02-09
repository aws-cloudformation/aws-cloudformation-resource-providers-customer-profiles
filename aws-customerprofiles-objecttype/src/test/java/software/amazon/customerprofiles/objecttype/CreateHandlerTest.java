package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField;
import software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.StandardIdentifier;
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
import static org.mockito.ArgumentMatchers.any;

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

    @Mock
    private AmazonWebServicesClientProxy proxy;
    @Mock
    private CustomerProfilesClient customerProfilesClient;
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder()
                .domainName(DOMAIN_NAME)
                .objectTypeName(OBJECT_TYPE_NAME)
                .allowProfileCreation(false)
                .description(DESCRIPTION)
                .encryptionKey(KEY_ARN)
                .expirationDays(EXPIRATION_DAYS)
                .fields(Translator.mapFieldsToList(fields))
                .keys(Translator.mapKeysToList(keys))
                .build();

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
                .build();

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
        assertThat(response.getResourceModel().getDomainName()).isEqualTo(DOMAIN_NAME);
        assertThat(response.getResourceModel().getTemplateId()).isEqualTo(TEMPLATE_ID);
        assertThat(response.getResourceModel().getExpirationDays()).isEqualTo(EXPIRATION_DAYS);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
