package com.bobyte;

import com.byteplus.auth.impl.SignerV4Impl;
import com.byteplus.error.SdkError;
import com.byteplus.http.ClientConfiguration;
import com.byteplus.http.HttpClientFactory;
import com.byteplus.model.Credentials;
import com.byteplus.model.response.RawResponse;
import com.byteplus.service.SignableRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONObject;


public class ApiClient {
    // HTTP request client
    private HttpClient httpClient;
    // credentials object for calling the API
    private Credentials credentials;

    private static String BASE_URL = "open.byteplusapi.com";

    /* Constructor */
    public ApiClient(){
        httpClient = HttpClientFactory.create(new ClientConfiguration());
        credentials = new Credentials();
    }

    /* make BytePlus API request and return JSON object */
    public JSONObject request(SignableRequest req){
        RawResponse response = rawRequest(req, credentials);
        String responseStr = new String(response.getData(), StandardCharsets.UTF_8);
        JSONObject jsonObj = parseStringToJson(responseStr);
        return jsonObj;
    }


    /* make raw HTTP request */
    private RawResponse rawRequest(SignableRequest request, Credentials credentials) {

        // step 1 - sign request
        SignerV4Impl signer = new SignerV4Impl();
        try{
            signer.sign(request, credentials);
        } catch(Exception e){
            return new RawResponse(null, SdkError.ESIGN.getNumber(), e);
        }

        HttpClient client;
        HttpResponse response = null;
        try {
            if (getHttpClient() != null) {
                client = getHttpClient();
            } else {
                client = HttpClients.createDefault();
            }
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300) {
                String msg = SdkError.getErrorDesc(SdkError.EHTTP);
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                if (bytes != null && bytes.length > 0) {
                    msg = new String(bytes, StandardCharsets.UTF_8);
                }
                return new RawResponse(null, SdkError.EHTTP.getNumber(), new Exception(msg));
            }
            byte[] bytes = EntityUtils.toByteArray(response.getEntity());
            return new RawResponse(bytes, SdkError.SUCCESS.getNumber(), null);
        } catch (Exception e) {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
            return new RawResponse(null, SdkError.EHTTP.getNumber(), new Exception(SdkError.getErrorDesc(SdkError.EHTTP)));
        }
    }

    /* get the class http client */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    /* JSON parser from String */
    public JSONObject parseStringToJson(String string){
        final JSONObject jsonObj = new JSONObject(string);
        return jsonObj;
    }

    /* Set API credentials */
    public void setCredentials(String accessKey, String secretKey, String region, String serviceName){
        credentials.setAccessKeyID(accessKey);
        credentials.setSecretAccessKey(secretKey);
        credentials.setRegion(region);
        credentials.setService(serviceName);
    }

    /* Initialize an HTTP API request to BytePlus */
    public SignableRequest initHTTPRequest(String HTTPMethod, String HTTPScheme, String Path, List<NameValuePair> params){
        // step 2 - create a new HTTP request
        SignableRequest request = new SignableRequest();
        URIBuilder builder = request.getUriBuilder();
        request.setMethod(HTTPMethod);
        builder.setScheme(HTTPScheme);
        builder.setHost(BASE_URL);
        builder.setPath(Path);
        builder.setParameters(params);
        return request;
    }


}
