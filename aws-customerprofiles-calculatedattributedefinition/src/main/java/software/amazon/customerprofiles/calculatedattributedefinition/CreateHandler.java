package software.amazon.customerprofiles.calculatedattributedefinition;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.CreateCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.CreateCalculatedAttributeDefinitionResponse;
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
    public static final String DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE = "Calculated attribute definition with name %s already exists";

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

        final ResourceModel requestModel = request.getDesiredResourceState();

        Map<String, String> resourceTags;
        if (request.getDesiredResourceTags() == null || request.getDesiredResourceTags().isEmpty()) {
            resourceTags = null;
        } else {
            resourceTags = request.getDesiredResourceTags();
        }

        CreateCalculatedAttributeDefinitionRequest createDefinitionRequest = CreateCalculatedAttributeDefinitionRequest.builder()
                .domainName(requestModel.getDomainName())
                .calculatedAttributeName(requestModel.getCalculatedAttributeName())
                .displayName(requestModel.getDisplayName())
                .description(requestModel.getDescription())
                .attributeDetails(Translator.translateFromInternalAttributeDetails(requestModel.getAttributeDetails()))
                .conditions(Translator.translateFromInternalConditions(requestModel.getConditions()))
                .statistic(requestModel.getStatistic())
                .tags(resourceTags)
                .build();
        final CreateCalculatedAttributeDefinitionResponse createDefinitionResponse;

        try {
            createDefinitionResponse = proxy.injectCredentialsAndInvokeV2(createDefinitionRequest, client::createCalculatedAttributeDefinition);
            logger.log(String.format("Created calculated attribute definition with domainName = %s, calculatedAttributeName = %s",
                    requestModel.getDomainName(), requestModel.getCalculatedAttributeName()));
        } catch (BadRequestException e) {
            if (e.getMessage() != null &&
                    e.getMessage().contains(String.format(DEFINITION_ALREADY_EXISTS_ERROR_MESSAGE, requestModel.getCalculatedAttributeName()))) {
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
                .calculatedAttributeName(createDefinitionResponse.calculatedAttributeName())
                .displayName(createDefinitionResponse.displayName())
                .description(createDefinitionResponse.description())
                .attributeDetails(Translator.translateToInternalAttributeDetails(createDefinitionResponse.attributeDetails()))
                .conditions(Translator.translateToInternalConditions(createDefinitionResponse.conditions()))
                .statistic(createDefinitionResponse.statisticAsString())
                .createdAt(createDefinitionResponse.createdAt().toString())
                .lastUpdatedAt(createDefinitionResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToSet(createDefinitionResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
