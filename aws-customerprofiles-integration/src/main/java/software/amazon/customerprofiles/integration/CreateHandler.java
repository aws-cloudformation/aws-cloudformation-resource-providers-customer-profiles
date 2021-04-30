package software.amazon.customerprofiles.integration;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.AccessDeniedException;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.ThrottlingException;
import software.amazon.cloudformation.exceptions.CfnAccessDeniedException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.exceptions.CfnThrottlingException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;
import static software.amazon.customerprofiles.integration.Translator.buildServiceFlowDefinition;

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

        // calls to GetIntegration without a URI result in a 400 so we can skip calling
        if (model.getUri() == null) {
            return createIntegration(proxy, request, logger);
        }

        final GetIntegrationRequest getIntegrationRequest = GetIntegrationRequest.builder()
                .domainName(model.getDomainName())
                .uri(model.getUri())
                .build();

        final GetIntegrationResponse getIntegrationResponse;
        try {
            getIntegrationResponse = proxy.injectCredentialsAndInvokeV2(getIntegrationRequest, client::getIntegration);
        } catch (Exception exc) {
            // 1. BadRequestException will also handled by PutIntegration
            // 2. ResourceNotFoundException is the exact exception we want before calling PutIntegration
            // 3. Whatever 5xx error GetIntegration call meet, it should not affect the performance of Create Action
            return createIntegration(proxy, request, logger);
        }

        // If GetIntegration Call succeed
        // Return a Bad Request Exception as Integration already existed
        final String errorMessage = String.format("Integration %s already exists with domainName = %s", getIntegrationResponse.uri(), getIntegrationResponse.domainName());
        logger.log(errorMessage);
        BadRequestException e = BadRequestException.builder()
                .statusCode(BAD_REQUEST_ERROR_CODE)
                .message(errorMessage)
                .build();
        throw new CfnAlreadyExistsException(e);
    }

    /**
     * Creates an integration
     * @param proxy
     * @param request
     * @param logger
     * @return
     */
    private ProgressEvent<ResourceModel, CallbackContext> createIntegration(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final Logger logger
    ) {
        final ResourceModel model = request.getDesiredResourceState();

        Map<String, String> resourceTag;
        if (request.getDesiredResourceTags() == null) {
            resourceTag = null;
        } else if (request.getDesiredResourceTags().isEmpty()) {
            resourceTag = null;
        } else {
            resourceTag = request.getDesiredResourceTags();
        }
        PutIntegrationRequest putIntegrationRequest = PutIntegrationRequest.builder()
                .domainName(model.getDomainName())
                .objectTypeName(model.getObjectTypeName())
                .tags(resourceTag)
                .flowDefinition(buildServiceFlowDefinition(model.getFlowDefinition()))
                .uri(model.getUri())
                .build();

        final PutIntegrationResponse putIntegrationResponse;
        try {
            putIntegrationResponse = proxy.injectCredentialsAndInvokeV2(putIntegrationRequest, client::putIntegration);
            logger.log(String.format("Integration Created with domainName = %s", model.getDomainName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (AccessDeniedException e) {
            throw new CfnAccessDeniedException(e);
        } catch (ThrottlingException e) {
            throw new CfnThrottlingException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .createdAt(putIntegrationResponse.createdAt().toString())
                .domainName(putIntegrationResponse.domainName())
                .lastUpdatedAt(putIntegrationResponse.lastUpdatedAt().toString())
                .objectTypeName(putIntegrationResponse.objectTypeName())
                .tags(Translator.mapTagsToList(putIntegrationResponse.tags()))
                .uri(putIntegrationResponse.uri())
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }

}

