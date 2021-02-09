package software.amazon.customerprofiles.domain;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateDomainRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateDomainResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Map;

@NoArgsConstructor
public class CreateHandler extends BaseHandler<CallbackContext> {

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

        // This is a weird place caused between Contract Test and SAM Test
        // The Tag is getting from DesiredResourceTags, the Contract Test will set it as Empty Map, SAM Test will not
        Map<String, String> resourceTag;
        if (request.getDesiredResourceTags() == null) {
            resourceTag = null;
        } else if (request.getDesiredResourceTags().isEmpty()) {
            resourceTag = null;
        } else {
            resourceTag = request.getDesiredResourceTags();
        }
        final CreateDomainRequest createDomainRequest = CreateDomainRequest.builder()
                .domainName(model.getDomainName())
                .deadLetterQueueUrl(model.getDeadLetterQueueUrl())
                .defaultEncryptionKey(model.getDefaultEncryptionKey())
                .defaultExpirationDays(model.getDefaultExpirationDays())
                // TODO: use getDesiredResourceTags to get both resource tags and stack tags
                .tags(resourceTag)
                .build();

        final CreateDomainResponse createDomainResponse;
        try {
            createDomainResponse = proxy.injectCredentialsAndInvokeV2(createDomainRequest, client::createDomain);
            logger.log(String.format("Domain Created with domainName = %s", model.getDomainName()));
        } catch (BadRequestException e) {
            if (e.getMessage().contains("Domain " + model.getDomainName() + " already exists")) {
                throw new CfnAlreadyExistsException(e);
            } else {
                throw new CfnInvalidRequestException(e);
            }
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .createdAt(createDomainResponse.createdAt().toString())
                .deadLetterQueueUrl(createDomainResponse.deadLetterQueueUrl())
                .defaultEncryptionKey(createDomainResponse.defaultEncryptionKey())
                .defaultExpirationDays(createDomainResponse.defaultExpirationDays())
                .domainName(createDomainResponse.domainName())
                .lastUpdatedAt(createDomainResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToList(createDomainResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
