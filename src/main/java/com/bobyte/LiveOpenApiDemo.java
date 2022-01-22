package com.bobyte;
import com.byteplus.service.SignableRequest;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LiveOpenApiDemo {

    static String ACCESS_KEY = "YOUR ACCESS KEY";
    static String SECRET_KEY = "YOUR SECRECT KEY";
    static String REGION = "ap-singapore-1";
    static String SERVICE = "livesaas";

    public static void main(String[] args) {

        // step 1 - create a new api client
        ApiClient apiClient = new ApiClient();

        // step 1 - set credentials
        apiClient.setCredentials(ACCESS_KEY, SECRET_KEY, REGION, SERVICE);

        // step 2 - create a new HTTP request with query params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ActivityId","1704139929742337"));
        params.add(new BasicNameValuePair("PageNo","1"));
        params.add(new BasicNameValuePair("PageItemCount","5"));
        params.add(new BasicNameValuePair("Action","ListMediasAPI"));
        params.add(new BasicNameValuePair("Version","2020-06-01"));
        SignableRequest newRequest = apiClient.initHTTPRequest("GET", "http", "/", params);

        // step 3 - make the API request
        JSONObject responseJson = apiClient.request(newRequest);
        System.out.println(responseJson.getJSONObject("Result").getInt("PageItemCount"));

    }



}
