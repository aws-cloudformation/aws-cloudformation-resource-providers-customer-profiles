package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.Lists;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListProfileObjectTypeItem;
import software.amazon.awssdk.services.customerprofiles.model.ListProfileObjectTypesResponse;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeResponse;
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
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {

    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME = "testDomainName";
    private static final String DESCRIPTION_1 = "description1";
    private static final String DESCRIPTION_2 = "description2";
    private static final String OBJECT_TYPE_NAME_1 = "testObjectTypeName1";
    private static final String OBJECT_TYPE_NAME_2 = "testObjectTypeName2";

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
                .build();
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem1 = ListProfileObjectTypeItem.builder()
                .createdAt(TIME)
                .description(DESCRIPTION_1)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_1)
                .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem2 = ListProfileObjectTypeItem.builder()
                .createdAt(TIME)
                .description(DESCRIPTION_2)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_2)
                .build();

        final ListProfileObjectTypesResponse listProfileObjectTypesResponse = ListProfileObjectTypesResponse.builder()
                .items(Lists.newArrayList(listProfileObjectTypeItem1, listProfileObjectTypeItem2))
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(listProfileObjectTypesResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_1);
        assertThat(response.getResourceModels().get(1).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_whenCreatedAtIsNull() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem1 = ListProfileObjectTypeItem.builder()
            .createdAt(null)
            .description(DESCRIPTION_1)
            .lastUpdatedAt(TIME)
            .objectTypeName(OBJECT_TYPE_NAME_1)
            .build();

        ListProfileObjectTypeItem listProfileObjectTypeItem2 = ListProfileObjectTypeItem.builder()
            .createdAt(TIME)
            .description(DESCRIPTION_2)
            .lastUpdatedAt(TIME)
            .objectTypeName(OBJECT_TYPE_NAME_2)
            .build();

        final ListProfileObjectTypesResponse listProfileObjectTypesResponse = ListProfileObjectTypesResponse.builder()
            .items(Lists.newArrayList(listProfileObjectTypeItem1, listProfileObjectTypeItem2))
            .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
            .thenReturn(listProfileObjectTypesResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_1);
        assertThat(response.getResourceModels().get(0).getCreatedAt()).isNull();
        assertThat(response.getResourceModels().get(1).getObjectTypeName()).isEqualTo(OBJECT_TYPE_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_throwsCfnGeneralServiceExceptionWhenConvertingApiResponse() {
        try (MockedStatic<Translator> translatorMockedStatic = mockStatic(Translator.class)) {
            translatorMockedStatic.when(() -> Translator.mapTagsToList(any()))
                .thenThrow(new NullPointerException());

            final ListHandler handler = new ListHandler(customerProfilesClient);

            final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

            ListProfileObjectTypeItem listProfileObjectTypeItem1 = ListProfileObjectTypeItem.builder()
                .createdAt(null)
                .description(DESCRIPTION_1)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_1)
                .build();

            ListProfileObjectTypeItem listProfileObjectTypeItem2 = ListProfileObjectTypeItem.builder()
                .createdAt(TIME)
                .description(DESCRIPTION_2)
                .lastUpdatedAt(TIME)
                .objectTypeName(OBJECT_TYPE_NAME_2)
                .build();

            final ListProfileObjectTypesResponse listProfileObjectTypesResponse = ListProfileObjectTypesResponse.builder()
                .items(Lists.newArrayList(listProfileObjectTypeItem1, listProfileObjectTypeItem2))
                .build();

            Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(listProfileObjectTypesResponse);

            assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
        }
    }

    @Test
    public void handleRequest_BadRequestException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        BadRequestException exc = BadRequestException.builder()
                .message("BadRequestException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnInvalidRequestException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_InternalServerException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        InternalServerException exc = InternalServerException.builder()
                .message("InternalServerException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnServiceInternalErrorException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_ResourceNotFoundException() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        ResourceNotFoundException exc = ResourceNotFoundException.builder()
                .message("ResourceNotFoundException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnNotFoundException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }

    @Test
    public void handleRequest_otherException() {
        final ListHandler handler = new ListHandler();

        ThrottlingException exc = ThrottlingException.builder()
                .message("ThrottlingException")
                .build();

        Mockito.doThrow(exc).when(proxy).injectCredentialsAndInvokeV2(any(), any());

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        assertThrows(CfnGeneralServiceException.class, () -> handler.handleRequest(proxy, request, null, logger));
    }
}
