package software.amazon.customerprofiles.segmentdefinition;

import java.util.Map;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateSegmentDefinitionResponse;
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

public class CreateHandler extends ProfileBaseHandler {

    public static final String DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE = "Segment definition with name %s already exists";

    public CreateHandler() {
        super();
    }

    public CreateHandler(CustomerProfilesClient client) {
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

        Map<String, String> resourceTags;
        if (request.getDesiredResourceTags() == null || request.getDesiredResourceTags().isEmpty()) {
            resourceTags = null;
        } else {
            resourceTags = request.getDesiredResourceTags();
        }

        final CreateSegmentDefinitionRequest createDefinitionRequest = CreateSegmentDefinitionRequest.builder()
                .domainName(requestModel.getDomainName())
                .segmentDefinitionName(requestModel.getSegmentDefinitionName())
                .displayName(requestModel.getDisplayName())
                .description(requestModel.getDescription())
                .segmentGroups(
                        Translator.translateFromInternalSegmentGroup(requestModel.getSegmentGroups()))
                .tags(resourceTags)
                .build();

        final CreateSegmentDefinitionResponse createSegmentDefinitionResponse;
        try {
            createSegmentDefinitionResponse = proxy.injectCredentialsAndInvokeV2(createDefinitionRequest,
                    client::createSegmentDefinition);
            logger.log(String.format(
                    "Created segment definition with domainName = %s, segmentDefinitionName = %s",
                    requestModel.getDomainName(), requestModel.getSegmentDefinitionName()));
        } catch (BadRequestException e) {
            if (e.getMessage() != null &&
                    e.getMessage().contains(String.format(DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE,
                            requestModel.getSegmentDefinitionName()))) {
                throw new CfnAlreadyExistsException(e);
            }
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw Translator.translateToCfnException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .domainName(requestModel.getDomainName())
                .segmentDefinitionName(createSegmentDefinitionResponse.segmentDefinitionName())
                .displayName(createSegmentDefinitionResponse.displayName())
                .description(createSegmentDefinitionResponse.description() == null ? null
                        : createSegmentDefinitionResponse.description())
                .segmentDefinitionArn(createSegmentDefinitionResponse.segmentDefinitionArn())
                .createdAt(createSegmentDefinitionResponse.createdAt() == null ? null
                        : createSegmentDefinitionResponse.createdAt().toString())
                .tags(Translator.mapTagsToSet(createSegmentDefinitionResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
