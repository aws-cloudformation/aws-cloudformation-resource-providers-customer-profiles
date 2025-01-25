package software.amazon.customerprofiles.eventtrigger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateEventTriggerResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {
        final ResourceModel requestModel = request.getDesiredResourceState();

        final GetEventTriggerRequest getEventTriggerRequest = GetEventTriggerRequest.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(requestModel.getEventTriggerName())
                .build();

        // check whether the event trigger exists
        try {
            proxy.injectCredentialsAndInvokeV2(getEventTriggerRequest, proxyClient.client()::getEventTrigger);
            logger.log(String.format("Got event trigger with domainName = %s, eventTriggerName = %s",
                    requestModel.getDomainName(), requestModel.getEventTriggerName()));
        } catch (BadRequestException exc) {
            throw new CfnInvalidRequestException(exc);
        } catch (InternalServerException exc) {
            throw new CfnServiceInternalErrorException(exc);
        } catch (ResourceNotFoundException exc) {
            throw new CfnNotFoundException(exc);
        } catch (Exception exc) {
            throw new CfnGeneralServiceException(exc);
        }

        final Set<Tag> previousTags = request.getPreviousResourceTags() == null ?
                new HashSet<>() : Translator.mapTagsToSet(request.getPreviousResourceTags());

        if (previousTags != null) {
            final List<String> tagsToRemove = previousTags.stream().map(Tag::getKey).toList();

            // remove previous tags
            if (tagsToRemove.size() > 0) {
                final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                        .resourceArn(Translator.toEventTriggerArn(request))
                        .tagKeys(tagsToRemove)
                        .build();

                try {
                    proxy.injectCredentialsAndInvokeV2(untagResourceRequest, proxyClient.client()::untagResource);
                } catch (Exception exc) {
                    throw Translator.translateToCfnException(exc);
                }
            }
        }

        if (request.getDesiredResourceTags() != null && !request.getDesiredResourceTags().isEmpty()) {
            final Map<String, String> resourceTags = request.getDesiredResourceTags();
            final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                    .resourceArn(Translator.toEventTriggerArn(request))
                    .tags(resourceTags)
                    .build();
            try {
                proxy.injectCredentialsAndInvokeV2(tagResourceRequest, proxyClient.client()::tagResource);
            } catch (Exception exc) {
                throw Translator.translateToCfnException(exc);
            }
        }

        final UpdateEventTriggerRequest updateEventTriggerRequest = UpdateEventTriggerRequest.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(requestModel.getEventTriggerName())
                .objectTypeName(requestModel.getObjectTypeName())
                .description(requestModel.getDescription())
                .eventTriggerConditions(Translator.translateFromInternalEventTriggerConditions(requestModel.getEventTriggerConditions()))
                .segmentFilter(requestModel.getSegmentFilter())
                .eventTriggerLimits(Translator.translateFromInternalEventTriggerLimits(requestModel.getEventTriggerLimits()))
                .build();
        final UpdateEventTriggerResponse updateEventTriggerResponse;

        try {
            updateEventTriggerResponse = proxy.injectCredentialsAndInvokeV2(updateEventTriggerRequest, proxyClient.client()::updateEventTrigger);
            logger.log(String.format("Updated event trigger with domainName = %s, eventTriggerName = %s",
                    requestModel.getDomainName(), requestModel.getEventTriggerName()));
        } catch (BadRequestException exc) {
            throw new CfnInvalidRequestException(exc);
        } catch (InternalServerException exc) {
            throw new CfnServiceInternalErrorException(exc);
        } catch (ResourceNotFoundException exc) {
            throw new CfnNotFoundException(exc);
        } catch (Exception exc) {
            throw Translator.translateToCfnException(exc);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(updateEventTriggerResponse.eventTriggerName())
                .objectTypeName(updateEventTriggerResponse.objectTypeName())
                .description(updateEventTriggerResponse.description())
                .eventTriggerConditions(Translator.translateToInternalEventTriggerConditions(updateEventTriggerResponse.eventTriggerConditions()))
                .segmentFilter(updateEventTriggerResponse.segmentFilter())
                .eventTriggerLimits(Translator.translateToInternalEventTriggerLimits(updateEventTriggerResponse.eventTriggerLimits()))
                .createdAt(updateEventTriggerResponse.createdAt() == null ? null : updateEventTriggerResponse.createdAt().toString())
                .lastUpdatedAt(updateEventTriggerResponse.lastUpdatedAt() == null ? null : updateEventTriggerResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToSet(updateEventTriggerResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
