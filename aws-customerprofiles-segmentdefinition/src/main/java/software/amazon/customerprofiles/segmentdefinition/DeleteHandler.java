package software.amazon.customerprofiles.segmentdefinition;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DeleteSegmentDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.DeleteSegmentDefinitionResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends ProfileBaseHandler {

  public DeleteHandler() {
    super();
  }

  protected DeleteHandler(CustomerProfilesClient client) {
    super(client);
  }

  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<CustomerProfilesClient> proxyClient,
      final Logger logger) {

    final ResourceModel requestModel = request.getDesiredResourceState();

    final DeleteSegmentDefinitionRequest deleteDefinitionRequest = DeleteSegmentDefinitionRequest.builder()
        .domainName(requestModel.getDomainName())
        .segmentDefinitionName(requestModel.getSegmentDefinitionName())
        .build();

    final DeleteSegmentDefinitionResponse deleteDefinitionResponse;
    try {
      deleteDefinitionResponse = proxy.injectCredentialsAndInvokeV2(deleteDefinitionRequest,
          client::deleteSegmentDefinition);
      logger.log(String.format(
          "Deleted segment definition with domainName = %s, segmentDefinitionName = %s",
          requestModel.getDomainName(), requestModel.getSegmentDefinitionName()));
    } catch (BadRequestException e) {
      throw new CfnInvalidRequestException(e);
    } catch (InternalServerException e) {
      throw new CfnServiceInternalErrorException(e);
    } catch (ResourceNotFoundException e) {
      throw new CfnNotFoundException(e);
    } catch (Exception e) {
        throw Translator.translateToCfnException(e);
    }

    return ProgressEvent.<ResourceModel, CallbackContext>builder()
        .resourceModel(null)
        .status(OperationStatus.SUCCESS)
        .message(deleteDefinitionResponse.message())
        .build();
  }
}
