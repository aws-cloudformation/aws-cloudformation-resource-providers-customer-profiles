package software.amazon.customerprofiles.eventstream;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventStreamResponse;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@NoArgsConstructor
public class ReadHandler extends BaseHandler<CallbackContext> {
    private CustomerProfilesClient client;

    public ReadHandler(CustomerProfilesClient client) {
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

        final GetEventStreamRequest getEventStreamRequest = GetEventStreamRequest.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .build();
        final GetEventStreamResponse getEventStreamResponse;

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
        final ResourceModel responseModel = ResourceModel.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .uri(getEventStreamResponse.destinationDetails().uri())
            .eventStreamArn(getEventStreamResponse.eventStreamArn())
            .createdAt(getEventStreamResponse.createdAt().toString())
            .state(getEventStreamResponse.state().toString())
            .destinationDetails(Translator.translateToInternalDestinationDetails(getEventStreamResponse.destinationDetails()))
            .tags(Translator.mapTagsToSet(getEventStreamResponse.tags()))
            .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}