package software.amazon.customerprofiles.segmentdefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends ProfileBaseHandler {

    public UpdateHandler() {
        super();
    }

    protected UpdateHandler(CustomerProfilesClient client) {
        super(client);
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {

        final ResourceModel requestModel = request.getDesiredResourceState();

        final GetSegmentDefinitionRequest getDefinitionRequest = GetSegmentDefinitionRequest.builder()
                .domainName(requestModel.getDomainName())
                .segmentDefinitionName(requestModel.getSegmentDefinitionName())
                .build();
        getSegmentDefinition(proxy, logger, requestModel, getDefinitionRequest);

        final Set<Tag> previousTags = request.getPreviousResourceTags() == null ?
                new HashSet<>() : Translator.mapTagsToSet(request.getPreviousResourceTags());

        if (previousTags != null) {
            final List<String> tagsToRemove = previousTags.stream()
                    .map(Tag::getKey)
                    .collect(Collectors.toList());

            // Remove previous tags
            if (!tagsToRemove.isEmpty()) {
                final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                        .resourceArn(Translator.toSegmentDefinitionArn(request))
                        .tagKeys(tagsToRemove)
                        .build();
                try {
                    proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
                } catch (Exception e) {
                    throw Translator.translateToCfnException(e);
                }
            }
        }

        if (request.getDesiredResourceTags() != null && !request.getDesiredResourceTags().isEmpty()) {
            final Map<String, String> resourceTags = request.getDesiredResourceTags();
            final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                    .resourceArn(Translator.toSegmentDefinitionArn(request))
                    .tags(resourceTags)
                    .build();
            try {
                proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);
            } catch (Exception e) {
                throw Translator.translateToCfnException(e);
            }
        }

        return new ReadHandler().handleRequest(proxy, request, callbackContext, logger);
    }

    private GetSegmentDefinitionResponse getSegmentDefinition(
            final AmazonWebServicesClientProxy proxy,
            final Logger logger,
            final ResourceModel requestModel,
            final GetSegmentDefinitionRequest getDefinitionRequest) {

        final GetSegmentDefinitionResponse getDefinitionResponse;
        try {
            getDefinitionResponse = proxy.injectCredentialsAndInvokeV2(getDefinitionRequest,
                    client::getSegmentDefinition);
            logger.log(
                    String.format("Got segment definition with domainName = %s, segmentDefinitionName = %s",
                            requestModel.getDomainName(), requestModel.getSegmentDefinitionName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }
        return getDefinitionResponse;
    }
}
