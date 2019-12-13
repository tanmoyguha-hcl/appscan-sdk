/**
 * © Copyright HCL Technologies Ltd. 2019. 
 * LICENSE: Apache License, Version 2.0 https://www.apache.org/licenses/LICENSE-2.0
 */

package com.hcl.appscan.sdk.configuration;

import com.hcl.appscan.sdk.auth.IASEAuthenticationProvider;
import com.hcl.appscan.sdk.http.HttpResponse;
import com.hcl.appscan.sdk.http.HttpsClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

public class ASETestPoliciesProvider implements IComponent{
    private Map<String, String> m_policies;
    private IASEAuthenticationProvider m_authProvider;

    public ASETestPoliciesProvider(IASEAuthenticationProvider provider) {
        this.m_authProvider=provider;
    }

    @Override
    public Map<String, String> getComponents() {
        if(m_policies == null)
        	loadPolicies();
		return m_policies;
    }

    @Override
    public String getComponentName(String id) {
        return getComponents().get(id);
    }
    
    private void loadPolicies() {
        if(m_authProvider.isTokenExpired())
			return;
		
        m_policies = new HashMap<String, String>();
        //String url =  m_authProvider.getServer() + ASE_APPS + "columns=name&sortBy=%2Bname"; //$NON-NLS-1$
        String url =  m_authProvider.getServer() + "/api/testpolicies";
        Map<String, String> headers = m_authProvider.getAuthorizationHeader(true);
        //headers.putAll(Collections.singletonMap("Range", "items=0-999999")); //$NON-NLS-1$ //$NON-NLS-2$
		
		HttpsClient client = new HttpsClient();
		
		try {
			HttpResponse response = client.get(url, headers, null);
			
			if (!response.isSuccess())
				return;
		
			JSONArray array = (JSONArray)response.getResponseBodyAsJSON();
			if(array == null)
				return;
			
			for(int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String id = object.getString("id");
				String path = object.getString("name");
				m_policies.put(id, path);
			}
		}
		catch(IOException | JSONException e) {
			m_policies = null;
		}
    }
}