
import java.awt.Desktop;
import java.net.URI;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.ws.rs.core.UriBuilderException;
import java.util.Scanner;

import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
import com.docusign.esign.model.*;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import java.awt.Desktop;

import java.io.IOException;


public class HelloWorld2 {
  public static void main(String[] args) {
	
    String RedirectURI = "https://www.google.com/";
    String ClientSecret = "65e1ef2c-73d1-49ac-9412-9d7dea882acc";
    String IntegratorKey = "69d7c233-5cb5-49b5-aebe-ab8e6bbc4a7d";
    String BaseUrl = "https://demo.docusign.net";
    String AuthServerUrl = "https://account-d.docusign.com";
    ApiClient apiClient = new ApiClient(BaseUrl);
    		
    apiClient.setBasePath(BaseUrl);
    apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, RedirectURI);
    Configuration.setDefaultApiClient(apiClient);
    
    try
    {
        String randomState = "";
        java.util.List<String> scopes = new java.util.ArrayList<String>();
        scopes.add(OAuth.Scope_SIGNATURE);
        // get DocuSign OAuth authorization url
        URI oauthLoginUrl = apiClient.getAuthorizationUri
        (IntegratorKey, scopes, RedirectURI, OAuth.CODE, randomState);
        // open DocuSign OAuth login in the browser
		Desktop.getDesktop().browse(oauthLoginUrl);
		URL url= (oauthLoginUrl.toURL());
		
		//URLConnection con = (( url ).openConnection());
		//System.out.println( "orignal url: " + con.getURL() );
		//con.connect();
		//System.out.println( "connected url: " + con.getURL() );
		//InputStream is = con.getInputStream();
		//System.out.println( "redirected url: " + con.getURL() );
		//String line= con.getURL().getQuery();
		//is.close();
		
		//HttpURLConnection con = (HttpURLConnection)(new URL( url ).openConnection());
		//con.setInstanceFollowRedirects( false );
		//con.connect();
		//int responseCode = con.getResponseCode();
		//System.out.println( responseCode );
		//String location = con.getHeaderField( "Location" );
		//System.out.println( location );
		
		//String line= url.toString();
		//System.out.println(line);
		System.out.println("enter code from browser");
		Scanner sc= new Scanner(System.in);
		String line= sc.nextLine();
		
		
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
        (userInfo.getAccounts().get(0).getBaseUri());
        Configuration.setDefaultApiClient(apiClient);
		String accountId =
		userInfo.getAccounts().get(0).getAccountId();
       // String accountId= "3c7db62a-f7c2-4ae9-83d0-c1232f35f330";
                 // create a new envelope to manage the signature request
          EnvelopeDefinition envDef = new EnvelopeDefinition();
       envDef.setEmailSubject("DocuSign Java SDK-Sample Signature Request");
        //  System.out.println(IntegratorKey);

          // assign template information including ID and role(s)
          envDef.setTemplateId("787180fc-009a-4be4-b7a7-c495b748b8b8");
          
          // create a template role with a valid templateId
          //and roleName and assign signer info
          TemplateRole tRole = new TemplateRole();
          tRole.setRoleName("signer");
          tRole.setName("Diego Pierce");
          tRole.setEmail("bobuleon@gmail.com");
          
          tRole.setClientUserId(IntegratorKey);
        //  System.out.println(IntegratorKey);
        
          // create a list of template roles and add our newly created role
          java.util.List<TemplateRole> templateRolesList =
          new java.util.ArrayList<TemplateRole>();
          templateRolesList.add(tRole);
          
          
      //  System.out.println(IntegratorKey);
          // assign template role(s) to the envelope 
          envDef.setTemplateRoles(templateRolesList);
          
          // send the envelope by setting |status| to "sent".
          // To save as a draft set to "created"
          envDef.setStatus("sent");
          
        
          // instantiate a new EnvelopesApi object
          EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
       //   System.out.println(IntegratorKey);
        //accountId= IntegratorKey;
          // call the createEnvelope() API
          EnvelopeSummary envelopeSummary =
          envelopesApi.createEnvelope(accountId, envDef);
          System.out.println("Envelope has been sent to "+tRole.getEmail());
          
       //   EnvelopesApi envelopesapi = new EnvelopesApi();
          
          RecipientViewRequest view = new RecipientViewRequest();
          view.setReturnUrl("https://demo.docusign.net");
          view.setAuthenticationMethod("email");
          
          view.setEmail("bobuleon@gmail.com");
          view.setUserName("Diego Pierce");
          view.setRecipientId(ClientSecret);
          view.setClientUserId(IntegratorKey);
          
          ViewUrl recipientView = 
        envelopesApi.createRecipientView(accountId, envelopeSummary.getEnvelopeId(), view);
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



