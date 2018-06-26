import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
import com.docusign.esign.model.*;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import java.awt.Desktop;
import java.io.IOException;

public class DocuSignExample {
  public static void main(String[] args) {
    String RedirectURI = "https://www.google.com/";
    String ClientSecret = "65e1ef2c-73d1-49ac-9412-9d7dea882acc";
    String IntegratorKey = "69d7c233-5cb5-49b5-aebe-ab8e6bbc4a7d";
    String BaseUrl = "https://demo.docusign.net/restapi";
    
    ApiClient apiClient = new ApiClient(BaseUrl);
    try
    {
        String randomState = "[random_string]";
        java.util.List<String> scopes = new java.util.ArrayList<String>();
        scopes.add(OAuth.Scope_SIGNATURE);
        // get DocuSign OAuth authorization url
        URI oauthLoginUrl = apiClient.getAuthorizationUri
        (IntegratorKey, scopes, RedirectURI, OAuth.CODE, randomState);
        // open DocuSign OAuth login in the browser
		Desktop.getDesktop().browse(oauthLoginUrl);
        // IMPORTANT: after the login, DocuSign will send back a fresh
        // authorization code as a query param of the redirect URI.
        // You should set up a route that handles the redirect call to get
        // that code and pass it to token endpoint as shown in the next
        // lines:
        /*String code = "[once_you_get_the_oauth_code_put_it_here]";
        OAuth.OAuthToken oAuthToken =
        apiClient.generateAccessToken(IntegratorKey, ClientSecret, code);

        System.out.println("OAuthToken: " + oAuthToken);

        // now that the API client has an OAuth token, let's use it in all
        // DocuSign APIs
        UserInfo userInfo = 
        apiClient.getUserInfo(oAuthToken.getAccessToken());

        System.out.println("UserInfo: " + userInfo);
        // parse first account's baseUrl
        // below code required for production, no effect in demo (same
        // domain)
        apiClient.setBasePath
        (userInfo.getAccounts().get(0).getBaseUri() + "/restapi");
        Configuration.setDefaultApiClient(apiClient);
		String accountId =
		userInfo.getAccounts().get(0).getAccountId();*/
                 // create a new envelope to manage the signature request
          EnvelopeDefinition envDef = new EnvelopeDefinition();
       envDef.setEmailSubject("DocuSign Java SDK-Sample Signature Request");
          
          // assign template information including ID and role(s)
          envDef.setTemplateId("[TEMPLATE_ID]");
          
          // create a template role with a valid templateId
          //and roleName and assign signer info
          TemplateRole tRole = new TemplateRole();
          tRole.setRoleName("[ROLE_NAME]");
          tRole.setName("[SIGNER_NAME]");
          tRole.setEmail("diego@goslinerpierce.com");
        
          // create a list of template roles and add our newly created role
          java.util.List<TemplateRole> templateRolesList =
          new java.util.ArrayList<TemplateRole>();
          templateRolesList.add(tRole);
        
          // assign template role(s) to the envelope 
          envDef.setTemplateRoles(templateRolesList);
          
          // send the envelope by setting |status| to "sent".
          // To save as a draft set to "created"
          envDef.setStatus("sent");
        
          // instantiate a new EnvelopesApi object
          EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        
          // call the createEnvelope() API
          EnvelopeSummary envelopeSummary =
          envelopesApi.createEnvelope(accountId, envDef);
        }
        catch (ApiException ex)
        {
          System.out.println("Exception: " + ex);
        }
        catch (Exception e)
        {
          System.out.println("Exception: " + e.getLocalizedMessage());
        }
  }
} 



