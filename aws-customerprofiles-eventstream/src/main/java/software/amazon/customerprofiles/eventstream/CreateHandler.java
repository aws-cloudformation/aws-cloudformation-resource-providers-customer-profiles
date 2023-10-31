package software.amazon.customerprofiles.eventstream;

import java.util.Map;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateEventStreamRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateEventStreamResponse;
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

@NoArgsConstructor
public class CreateHandler extends BaseHandler<CallbackContext> {
    public static final String EVENT_STREAM_ALREADY_EXISTS_ERROR_MESSAGE =
        "The Customer Profiles Domain: %s is already associated with an active EventStream";
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

        ResourceModel requestModel = request.getDesiredResourceState();

        Map<String, String> resourceTags;
        if (request.getDesiredResourceTags() == null || request.getDesiredResourceTags().isEmpty()) {
            resourceTags = null;
        } else {
            resourceTags = request.getDesiredResourceTags();
        }

        CreateEventStreamRequest createEventStreamRequest = CreateEventStreamRequest.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .uri(requestModel.getUri())
            .tags(resourceTags)
            .build();
        final CreateEventStreamResponse createEventStreamResponse;

        try {
            createEventStreamResponse = proxy.injectCredentialsAndInvokeV2(createEventStreamRequest, client::createEventStream);
            logger.log(String.format("Created event stream = %s for domain = %s",
                requestModel.getEventStreamName(), requestModel.getDomainName()));
        } catch (BadRequestException e) {
            if (e.getMessage() != null &&
                e.getMessage().contains(String.format(EVENT_STREAM_ALREADY_EXISTS_ERROR_MESSAGE, requestModel.getDomainName()))) {
                throw new CfnAlreadyExistsException(e);
            }
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .uri(requestModel.getUri())
            .eventStreamArn(createEventStreamResponse.eventStreamArn())
            .tags(Translator.mapTagsToSet(createEventStreamResponse.tags()))
            .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}