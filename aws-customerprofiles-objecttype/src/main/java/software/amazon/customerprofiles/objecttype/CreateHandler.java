package software.amazon.customerprofiles.objecttype;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeResponse;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;

@NoArgsConstructor
public class CreateHandler extends BaseHandler<CallbackContext> {
    private static final int BAD_REQUEST_ERROR_CODE = 400;

    private CustomerProfilesClient client;

    public CreateHandler(CustomerProfilesClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        if (this.client == null) {
            this.client = ClientBuilder.getClient();
        }

        final ResourceModel model = request.getDesiredResourceState();

        final GetProfileObjectTypeRequest getProfileObjectTypeRequest = GetProfileObjectTypeRequest.builder()
                .domainName(model.getDomainName())
                .objectTypeName(model.getObjectTypeName())
                .build();
        final GetProfileObjectTypeResponse getProfileObjectTypeResponse;
        try {
            getProfileObjectTypeResponse = proxy.injectCredentialsAndInvokeV2(getProfileObjectTypeRequest, client::getProfileObjectType);
        } catch (Exception exc) {
            // 1. BadRequestException will also handled by PutProfileObjectType
            // 2. ResourceNotFoundException is the exact exception we want before calling PutProfileObjectType
            // 3. Whatever 5xx error GetProfileObjectType call meet, it should not affect the performance of Create Action
            Map<String, String> resourceTag;
            if (request.getDesiredResourceTags() == null) {
                resourceTag = null;
            } else if (request.getDesiredResourceTags().isEmpty()) {
                resourceTag = null;
            } else {
                resourceTag = request.getDesiredResourceTags();
            }
            final PutProfileObjectTypeRequest putProfileObjectTypeRequest = PutProfileObjectTypeRequest.builder()
                    .domainName(model.getDomainName())
                    .objectTypeName(model.getObjectTypeName())
                    .allowProfileCreation(model.getAllowProfileCreation())
                    .description(model.getDescription())
                    .encryptionKey(model.getEncryptionKey())
                    .expirationDays(model.getExpirationDays())
                    .fields(Translator.listFieldsToMap(model.getFields()))
                    .keys(Translator.listKeysToMap(model.getKeys()))
                    .tags(resourceTag)
                    .templateId(model.getTemplateId())
                    .build();

            final PutProfileObjectTypeResponse putProfileObjectTypeResponse;
            try {
                putProfileObjectTypeResponse = proxy.injectCredentialsAndInvokeV2(putProfileObjectTypeRequest, client::putProfileObjectType);
                logger.log(String.format("ProfileObjectType Created with domainName = %s, objectTypeName = %s",
                        model.getDomainName(), model.getObjectTypeName()));
            } catch (BadRequestException e) {
                throw new CfnInvalidRequestException(e);
            } catch (InternalServerException e) {
                throw new CfnServiceInternalErrorException(e);
            } catch (Exception e) {
                throw new CfnGeneralServiceException(e);
            }

            final ResourceModel responseModel = ResourceModel.builder()
                    .domainName(model.getDomainName())
                    .allowProfileCreation(putProfileObjectTypeResponse.allowProfileCreation())
                    .createdAt(putProfileObjectTypeResponse.createdAt().toString())
                    .description(putProfileObjectTypeResponse.description())
                    .encryptionKey(putProfileObjectTypeResponse.encryptionKey())
                    .expirationDays(putProfileObjectTypeResponse.expirationDays())
                    .fields(Translator.mapFieldsToList(putProfileObjectTypeResponse.fields()))
                    .keys(Translator.mapKeysToList(putProfileObjectTypeResponse.keys()))
                    .lastUpdatedAt(putProfileObjectTypeResponse.lastUpdatedAt().toString())
                    .objectTypeName(putProfileObjectTypeResponse.objectTypeName())
                    .tags(Translator.mapTagsToList(putProfileObjectTypeResponse.tags()))
                    .templateId(putProfileObjectTypeResponse.templateId())
                    .build();

            return ProgressEvent.defaultSuccessHandler(responseModel);
        }

        // If getProfileObjectType call succeed
        // Return a Bad Request Exception as ObjectType already existed
        String errorMessage = String.format("ObjectType %s already exists with domainName = %s",
                getProfileObjectTypeResponse.objectTypeName(), model.getDomainName());
        logger.log(errorMessage);
        BadRequestException e = BadRequestException.builder()
                .statusCode(BAD_REQUEST_ERROR_CODE)
                .message(errorMessage)
                .build();
        throw new CfnAlreadyExistsException(e);
    }
}
