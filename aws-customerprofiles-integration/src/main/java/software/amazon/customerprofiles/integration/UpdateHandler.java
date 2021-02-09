package software.amazon.customerprofiles.integration;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UpdateHandler extends BaseHandler<CallbackContext> {

    private CustomerProfilesClient client;

    public UpdateHandler(CustomerProfilesClient client) {
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

        final ResourceModel requestModel = request.getDesiredResourceState();

        final GetIntegrationRequest getIntegrationRequest = GetIntegrationRequest.builder()
                .domainName(requestModel.getDomainName())
                .uri(requestModel.getUri())
                .build();

        // If this integration is never created, can not be updated
        try {
            proxy.injectCredentialsAndInvokeV2(getIntegrationRequest, client::getIntegration);
            logger.log(String.format("Get Integration with domainName = %s, uri = %s",
                    requestModel.getDomainName(), requestModel.getUri()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final List<Tag> previousTags = request.getPreviousResourceTags() == null ? Lists.newArrayList() :
                Translator.mapTagsToList(request.getPreviousResourceTags());

        final List<String> tagsToRemove = previousTags.stream()
                .map(Tag::getKey)
                .collect(Collectors.toList());

        // Remove previous tags
        if (tagsToRemove.size() > 0) {
            final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                    .resourceArn(Translator.toIntegrationArn(request))
                    .tagKeys(tagsToRemove)
                    .build();
            proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
        }

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
        final PutIntegrationRequest putIntegrationRequest = PutIntegrationRequest.builder()
                .domainName(requestModel.getDomainName())
                // ObjectTypeName can be updated
                .objectTypeName(requestModel.getObjectTypeName())
                .uri(requestModel.getUri())
                .tags(resourceTag)
                .build();

        final PutIntegrationResponse putIntegrationResponse;
        try {
            putIntegrationResponse = proxy.injectCredentialsAndInvokeV2(putIntegrationRequest, client::putIntegration);
            logger.log(String.format("Update Integration with domainName = %s, uri = %s",
                    requestModel.getDomainName(), requestModel.getUri()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
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
