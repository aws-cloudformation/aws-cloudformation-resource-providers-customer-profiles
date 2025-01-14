package software.amazon.customerprofiles.segmentdefinition;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetSegmentDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends ProfileBaseHandler {

  public ReadHandler() {
    super();
  }

  protected ReadHandler(CustomerProfilesClient client) {
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

    final GetSegmentDefinitionResponse getDefinitionResponse;
    try {
      getDefinitionResponse = proxy.injectCredentialsAndInvokeV2(getDefinitionRequest,
          client::getSegmentDefinition);
      logger.log(String.format(
          "Got segment definition with domainName = %s, calculatedAttributeName = %s",
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

    final ResourceModel responseModel = ResourceModel.builder()
        .domainName(requestModel.getDomainName())
        .segmentDefinitionName(getDefinitionResponse.segmentDefinitionName())
        .displayName(getDefinitionResponse.displayName())
        .description(getDefinitionResponse.description())
        .createdAt(getDefinitionResponse.createdAt() == null ? null
            : getDefinitionResponse.createdAt().toString())
        .segmentDefinitionArn(getDefinitionResponse.segmentDefinitionArn())
        .segmentGroups(
            Translator.translateToInternalSegmentGroup(getDefinitionResponse.segmentGroups()))
        .tags(Translator.mapTagsToSet(getDefinitionResponse.tags()))
        .build();

    return ProgressEvent.defaultSuccessHandler(responseModel);
  }
}