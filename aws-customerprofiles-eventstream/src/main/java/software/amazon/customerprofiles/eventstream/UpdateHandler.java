package software.amazon.customerprofiles.eventstream;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamResponse;
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
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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

        // check whether the event stream exists
        final GetEventStreamRequest getEventStreamRequest = GetEventStreamRequest.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .build();
        getEventStream(proxy, logger, requestModel, getEventStreamRequest);

        final Set<Tag> previousTags = request.getPreviousResourceTags() == null ?
            new HashSet<>() : Translator.mapTagsToSet(request.getPreviousResourceTags());

        if (previousTags != null) {
            final List<String> tagsToRemove = previousTags.stream()
                .map(Tag::getKey)
                .collect(Collectors.toList());

            // remove previous tags
            if (tagsToRemove.size() > 0) {
                final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                    .resourceArn(Translator.toEventStreamArn(request))
                    .tagKeys(tagsToRemove)
                    .build();
                proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
            }
        }

        if (request.getDesiredResourceTags() != null && !request.getDesiredResourceTags().isEmpty()) {
            final Map<String, String> resourceTags = request.getDesiredResourceTags();
            final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                .resourceArn(Translator.toEventStreamArn(request))
                .tags(resourceTags)
                .build();
            proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);
        }

        return new ReadHandler().handleRequest(proxy, request, callbackContext, logger);
    }

    private GetEventStreamResponse getEventStream(AmazonWebServicesClientProxy proxy,
                                                  Logger logger,
                                                  ResourceModel requestModel,
                                                  GetEventStreamRequest getEventStreamRequest) {
        GetEventStreamResponse getEventStreamResponse;
        try {
            getEventStreamResponse = proxy.injectCredentialsAndInvokeV2(getEventStreamRequest, client::getEventStream);
            logger.log(String.format("Got event stream with domainName = %s, eventStreamName = %s",
                requestModel.getDomainName(), requestModel.getEventStreamName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }
        return getEventStreamResponse;
    }
}
