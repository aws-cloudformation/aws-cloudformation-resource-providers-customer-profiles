package software.amazon.customerprofiles.segmentdefinition;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;


/**
 * Abstract base handler for profile resource handlers.
 */
public abstract class ProfileBaseHandler extends BaseHandler<CallbackContext> {

  protected final CustomerProfilesClient client;

  public ProfileBaseHandler() {
    this(ClientBuilder.getClient());
  }

  public ProfileBaseHandler(CustomerProfilesClient serviceClient) {
    this.client = serviceClient;
  }

  private static final CustomerProfilesClient CLIENT = CustomerProfilesClient.builder().build();

  protected CustomerProfilesClient getClient() {
    return CLIENT;
  }

  @Override
  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final Logger logger) {

    return handleRequest(
        proxy,
        request,
        callbackContext != null ? callbackContext : new CallbackContext(),
        proxy.newProxy(this::getClient),
        logger
    );
  }

  /**
   * handleRequest method with proxyClient parameter.
   * This will help unit testing of the handlers by mocking the behavior of proxyClient.
   */
  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<CustomerProfilesClient> proxyClient,
      final Logger logger);
}
