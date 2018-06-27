
import java.awt.Desktop;
import java.net.URI;
//import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
//import java.util.ArrayList;
//import javax.ws.rs.core.UriBuilderException;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.TemplateRole;
import com.docusign.esign.model.ViewUrl;

public class HelloWorld2 {
  public static void main(String[] args) {
	
    String RedirectURI = "https://www.google.com/";
    String ClientSecret = "65e1ef2c-73d1-49ac-9412-9d7dea882acc";
    String IntegratorKey = "69d7c233-5cb5-49b5-aebe-ab8e6bbc4a7d";
    String BaseUrl = "https://demo.docusign.net/restapi";
    String AuthServerUrl = "https://account-d.docusign.com";
    ApiClient apiClient = new ApiClient(BaseUrl);
    		
    apiClient.setBasePath(BaseUrl);
    apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, RedirectURI);
    Configuration.setDefaultApiClient(apiClient);
    
    try
    {
        String randomState = "https://account-d.docusign.com";
        java.util.List<String> scopes = new java.util.ArrayList<String>();
        scopes.add(OAuth.Scope_SIGNATURE);
        // get DocuSign OAuth authorization url
        URI oauthLoginUrl = apiClient.getAuthorizationUri
        (IntegratorKey, scopes, RedirectURI, OAuth.CODE, randomState);
        // open DocuSign OAuth login in the browser
		Desktop.getDesktop().browse(oauthLoginUrl);
		URL url= (oauthLoginUrl.toURL());
		URLConnection con = url.openConnection();
	//	InputStream is = con.getInputStream();
		
		System.out.println( "orignal url: " + con.getURL() );
		con.connect();
		System.out.println( "connected url: " + con.getURL() );
		String line = con.getURL().getQuery();
			
		System.out.println("code may be: "+line);
		
        // IMPORTANT: after the login, DocuSign will send back a fresh
        // authorization code as a query param of the redirect URI.
        // You should set up a route that handles the redirect call to get
        // that code and pass it to token endpoint as shown in the next
        // lines:
        String code = line;
        OAuth.OAuthToken oAuthToken =
        apiClient.generateAccessToken(IntegratorKey, ClientSecret, code);

        System.out.println("OAuthToken: " + oAuthToken);

        // now that the API client has an OAuth token, let's use it in all
        // DocuSign APIs
        com.docusign.esign.client.auth.OAuth.UserInfo userInfo = 
        apiClient.getUserInfo(oAuthToken.getAccessToken());

        System.out.println("UserInfo: " + userInfo);
        // parse first account's baseUrl
        // below code required for production, no effect in demo (same
        // domain)
        apiClient.setBasePath
        (userInfo.getAccounts().get(0).getBaseUri() + "\restapi");
        Configuration.setDefaultApiClient(apiClient);
		String accountId =
		userInfo.getAccounts().get(0).getAccountId();
                 // create a new envelope to manage the signature request
          EnvelopeDefinition envDef = new EnvelopeDefinition();
       envDef.setEmailSubject("DocuSign Java SDK-Sample Signature Request");
          
          // assign template information including ID and role(s)
          envDef.setTemplateId("an attempt");
          
          // create a template role with a valid templateId
          //and roleName and assign signer info
          TemplateRole tRole = new TemplateRole();
          tRole.setRoleName("Tester");
          tRole.setName("Diego Pierce");
          tRole.setEmail("diego@goslinerpierce.com");
          
          tRole.setClientUserId("1001");
        
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
      //    EnvelopeSummary envelopeSummary =
          envelopesApi.createEnvelope(accountId, envDef);
          System.out.println("Envelope has been sent to "+tRole.getEmail());
          
     //     EnvelopesApi envelopesapi = new EnvelopesApi();
          
          RecipientViewRequest view = new RecipientViewRequest();
          view.setReturnUrl("https:\\docusign.com");
          view.setAuthenticationMethod("email");
          
          view.setEmail("diego@goslinerpierce.com");
          view.setUserName("Diego Pierce");
          view.setRecipientId("65e1ef2c-73d1-49ac-9412-9d7dea882acc");
          view.setClientUserId("69d7c233-5cb5-49b5-aebe-ab8e6bbc4a7d");
          
          ViewUrl recipientView = 
        envelopesApi.createRecipientView(accountId, "envelopeId", view);
          System.out.println("Signing URL = " + recipientView.getUrl());
          Desktop.getDesktop().browse(URI.create(recipientView.getUrl()));
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



