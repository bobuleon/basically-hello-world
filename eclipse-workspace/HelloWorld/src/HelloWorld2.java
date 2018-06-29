
/*import java.awt.Desktop;
import java.net.URI;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.ws.rs.core.UriBuilderException;

import org.apache.oltu.oauth2.common.token.BasicOAuthToken;

import java.util.Scanner;

import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
import com.docusign.esign.model.*;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import java.awt.Desktop;

import java.io.IOException;
import com.sun.jersey.core.impl.provider.entity.*;
//import com.sun.jersey.core.impl.provider.entity.StringProvider;
//import com.sun.jersey.core.impl.provider.entity.ByteArrayProvider;
//import com.sun.jersey.core.impl.provider.entity.FileProvider;
//import com.sun.jersey.core.impl.provider.entity.InputStreamProvider;
//import com.sun.jersey.core.impl.provider.entity.DataSourceProvider;
//import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider$General;
//import com.sun.jersey.core.impl.provider.entity.ReaderProvider;
//import com.sun.jersey.core.impl.provider.entity.DocumentProvider;
//import com.sun.jersey.core.impl.provider.entity.SourceProvider$StreamSourceReader;
/*import com.sun.jersey.core.impl.provider.entity.SourceProvider$SAXSourceReader;
import com.sun.jersey.core.impl.provider.entity.SourceProvider$DOMSourceReader;
import com.sun.jersey.json.impl.provider.entity.JSONJAXBElementProvider$General;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
/*import com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider$General;
import com.sun.jersey.core.impl.provider.entity.XMLListElementProvider$General;
import com.sun.jersey.core.impl.provider.entity.XMLRootObjectProvider$General;
import com.sun.jersey.core.impl.provider.entity.EntityHolderReader;
//import com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider$General;
//import com.sun.jersey.json.impl.provider.entity.JSONListElementProvider$General;

public class HelloWorld2 {
	public static void main(String[] args) {

		String RedirectURI = "https://www.google.com/";
		String ClientSecret = "65e1ef2c-73d1-49ac-9412-9d7dea882acc";
		String IntegratorKey = "69d7c233-5cb5-49b5-aebe-ab8e6bbc4a7d";
		String AuthServerUrl = "https://account-d.docusign.com";
		String RestApiUrl = "https://demo.docusign.net/restapi";

		ApiClient apiClient = new ApiClient(AuthServerUrl, "docusignAccessCode", IntegratorKey, ClientSecret);

		apiClient.setBasePath(RestApiUrl);
		apiClient.configureAuthorizationFlow(IntegratorKey, ClientSecret, RedirectURI);
		Configuration.setDefaultApiClient(apiClient);

		try {
			String randomState = "";
			//String oAuthLoginUrl= apiClient.getAuthorizationUri();
			java.util.List<String> scopes = new java.util.ArrayList<String>();
			scopes.add(OAuth.Scope_SIGNATURE);
			// get DocuSign OAuth authorization url
			URI oauthLoginUrl = apiClient.getAuthorizationUri(IntegratorKey, scopes, RedirectURI, OAuth.CODE, randomState);
			System.out.println(oauthLoginUrl);
			
			// open DocuSign OAuth login in the browser
			Desktop.getDesktop().browse(oauthLoginUrl);

			//get code
			System.out.println("enter code from browser");
			Scanner sc = new Scanner(System.in);
			String code = sc.nextLine();
			System.out.println(code);
			apiClient.getTokenEndPoint().setCode(code);
	
			// get access token and user info
			OAuth.OAuthToken oAuthToken = apiClient.generateAccessToken(IntegratorKey, ClientSecret, code);
			com.docusign.esign.client.auth.OAuth.UserInfo userInfo = apiClient.getUserInfo(oAuthToken.getAccessToken());
			 apiClient.setBasePath(userInfo.getAccounts().get(0).getBaseUri());
			 //String accountId = userInfo.getAccounts().get(0).getAccountId();
			 System.out.println(oAuthToken);
			 apiClient.setBasePath("https://account-d.docusign.com/oauth/userinfo");
			 Configuration.setDefaultApiClient(apiClient);
			 System.out.println("UserInfo: " + userInfo);
			
			 apiClient.updateAccessToken();
			AuthenticationApi authApi= new AuthenticationApi(apiClient);
			LoginInformation logininfo = authApi.login();
			String accountId = logininfo.getLoginAccounts().get(0).getAccountId();
			String baseUrl = logininfo.getLoginAccounts().get(0).getBaseUrl();
			String[] accountDomain = baseUrl.split("/v2");
			apiClient.setBasePath(accountDomain[0]);
			System.out.println("Configuring api client with following base URI: " + accountDomain[0]);
			


	
		

			// create a template role with a valid templateId
			// and roleName and assign signer info
			TemplateRole tRole = new TemplateRole();
			tRole.setRoleName("signer");
			tRole.setName("Diego Pierce");
			tRole.setEmail("bobuleon@gmail.com");
			System.out.println("1");

			tRole.setClientUserId(IntegratorKey);

			// create a list of template roles and add our newly created role
			java.util.List<TemplateRole> templateRolesList = new java.util.ArrayList<TemplateRole>();
			templateRolesList.add(tRole);

			// assign template role(s) to the envelope
			EnvelopeDefinition envDef = new EnvelopeDefinition();
			envDef.setEmailSubject("DocuSign Java SDK-Sample Signature Request");
			envDef.setTemplateId("787180fc-009a-4be4-b7a7-c495b748b8b8");
			envDef.setTemplateRoles(templateRolesList);
			envDef.setStatus("sent");
			

			// send the envelope by setting |status| to "sent".
			// To save as a draft set to "created"
			
			// instantiate a new EnvelopesApi object
			EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
			System.out.println("1");
			EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
			System.out.println("hi");
			String envID = envelopeSummary.getEnvelopeId();
			System.out.println("2");
			System.out.println("Envelope has been sent to " + tRole.getEmail());


			RecipientViewRequest view = new RecipientViewRequest();
			view.setReturnUrl("https://demo.docusign.net");
			view.setAuthenticationMethod("email");

			view.setEmail("bobuleon@gmail.com");
			view.setUserName("Diego Pierce");
			view.setRecipientId(ClientSecret);
			view.setClientUserId(IntegratorKey);
			ViewUrl recipientView = envelopesApi.createRecipientView(accountId, envID, view);
			System.out.println("Signing URL = " + recipientView.getUrl());
			Desktop.getDesktop().browse(URI.create(recipientView.getUrl()));
			sc.close();

		} catch (ApiException ex) {
			System.out.println("Exception: " + ex);
			ex.printStackTrace();
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}*/

import com.docusign.esign.api.*;
import com.docusign.esign.client.*;
import com.docusign.esign.model.*;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.client.auth.OAuth.UserInfo;
import java.awt.Desktop;
import java.util.Scanner;
import java.awt.Desktop;
import java.net.URI;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.ws.rs.core.UriBuilderException;

import java.io.IOException;

public class HelloWorld2 {
  public static void main(String[] args) {
    String RedirectURI = "https://www.google.com/";
    String ClientSecret = "b9affef0-76b4-443b-9c5e-3fbccaec2d16";
    String IntegratorKey = "a3d199e4-eed5-40f1-9bfd-b89e3bad3d05";
    String BaseUrl = "https://demo.docusign.net/restapi";
    
    ApiClient apiClient = new ApiClient(BaseUrl);
    try
    {
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // STEP 1: AUTHENTICATE TO RETRIEVE ACCOUNTID & BASEURL         
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
                      
        String randomState = "";
        java.util.List<String> scopes = new java.util.ArrayList<String>();
        scopes.add(OAuth.Scope_SIGNATURE);
        // get DocuSign OAuth authorization url
        URI oauthLoginUrl = apiClient.getAuthorizationUri(IntegratorKey, scopes, RedirectURI, OAuth.CODE, randomState);
        // open DocuSign OAuth login in the browser
		Desktop.getDesktop().browse(oauthLoginUrl);
        // IMPORTANT: after the login, DocuSign will send back a fresh
        // authorization code as a query param of the redirect URI.
        // You should set up a route that handles the redirect call to get
        // that code and pass it to token endpoint as shown in the next
        // lines:System.out.println("enter code from browser");
		Scanner sc = new Scanner(System.in);
		String code = sc.nextLine();
		System.out.println(code);
        OAuth.OAuthToken oAuthToken = apiClient.generateAccessToken(IntegratorKey, ClientSecret, code);

        System.out.println("OAuthToken: " + oAuthToken);

        // now that the API client has an OAuth token, let's use it in all
        // DocuSign APIs
        UserInfo userInfo = apiClient.getUserInfo(oAuthToken.getAccessToken());

        System.out.println("UserInfo: " + userInfo);
        // parse first account's baseUrl
        // below code required for production, no effect in demo (same
        // domain)
        //apiClient.setBasePath(userInfo.getAccounts().get(0).getBaseUri() + "/restapi");
       // Configuration.setDefaultApiClient(apiClient);
		//String accountId = userInfo.getAccounts().get(0).getAccountId();
          
          /////////////////////////////////////////////////////////////////////////////////////////////////////////
          // *** STEP 2: CREATE ENVELOPE FROM TEMPLATE       
          /////////////////////////////////////////////////////////////////////////////////////////////////////////
          
          // create a new envelope to manage the signature request
          EnvelopeDefinition envDef = new EnvelopeDefinition();
          envDef.setEmailSubject("DocuSign Java SDK - Sample Signature Request");
          
          // assign template information including ID and role(s)
          envDef.setTemplateId("787180fc-009a-4be4-b7a7-c495b748b8b8");
          
          // create a template role with a valid templateId and roleName and assign signer info
          TemplateRole tRole = new TemplateRole();
          tRole.setRoleName("signer");
          tRole.setName("Diego Pierce");
          tRole.setEmail("bobuleon@gmail.com");
        
          // create a list of template roles and add our newly created role
          java.util.List<TemplateRole> templateRolesList = new java.util.ArrayList<TemplateRole>();
          templateRolesList.add(tRole);
        
          // assign template role(s) to the envelope 
          envDef.setTemplateRoles(templateRolesList);
          
          // send the envelope by setting |status| to "sent". To save as a draft set to "created"
          envDef.setStatus("sent");
        
          // instantiate a new EnvelopesApi object
          EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        
          // call the createEnvelope() API
          String accountId= userInfo.getAccounts().get(0).getAccountId();
          EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(accountId, envDef);
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
