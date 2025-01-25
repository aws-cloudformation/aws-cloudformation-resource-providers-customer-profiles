package software.amazon.customerprofiles.eventtrigger;

import java.util.Map;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnAlreadyExistsException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class CreateHandler extends BaseHandlerStd {
    public static final String EVENT_TRIGGER_ALREADY_EXISTS_ERROR_MESSAGE = "Event trigger with name %s already exists";

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {
        final ResourceModel requestModel = request.getDesiredResourceState();

        Map<String, String> resourceTags;
        if (request.getDesiredResourceTags() == null || request.getDesiredResourceTags().isEmpty()) {
            resourceTags = null;
        } else {
            resourceTags = request.getDesiredResourceTags();
        }

        final CreateEventTriggerRequest createEventTriggerRequest = CreateEventTriggerRequest.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(requestModel.getEventTriggerName())
                .objectTypeName(requestModel.getObjectTypeName())
                .description(requestModel.getDescription())
                .eventTriggerConditions(Translator.translateFromInternalEventTriggerConditions(requestModel.getEventTriggerConditions()))
                .segmentFilter(requestModel.getSegmentFilter())
                .eventTriggerLimits(Translator.translateFromInternalEventTriggerLimits(requestModel.getEventTriggerLimits()))
                .tags(resourceTags)
                .build();
        final CreateEventTriggerResponse createEventTriggerResponse;

        try {
            createEventTriggerResponse = proxyClient.injectCredentialsAndInvokeV2(createEventTriggerRequest, proxyClient.client()::createEventTrigger);
            logger.log(String.format("Created event trigger with domainName = %s, eventTriggerName = %s", requestModel.getDomainName(),
                    requestModel.getEventTriggerName()));
        } catch (BadRequestException exc) {
            if (exc.getMessage() != null &&
                    exc.getMessage().contains(String.format(EVENT_TRIGGER_ALREADY_EXISTS_ERROR_MESSAGE, requestModel.getEventTriggerName()))) {
                throw new CfnAlreadyExistsException(exc);
            }
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
                .eventTriggerName(createEventTriggerResponse.eventTriggerName())
                .objectTypeName(createEventTriggerResponse.objectTypeName())
                .description(createEventTriggerResponse.description())
                .eventTriggerConditions(Translator.translateToInternalEventTriggerConditions(createEventTriggerResponse.eventTriggerConditions()))
                .segmentFilter(createEventTriggerResponse.segmentFilter())
                .eventTriggerLimits(Translator.translateToInternalEventTriggerLimits(createEventTriggerResponse.eventTriggerLimits()))
                .createdAt(createEventTriggerResponse.createdAt() == null ? null : createEventTriggerResponse.createdAt().toString())
                .lastUpdatedAt(createEventTriggerResponse.lastUpdatedAt() == null ? null : createEventTriggerResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToSet(createEventTriggerResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
