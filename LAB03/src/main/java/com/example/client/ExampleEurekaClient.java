package com.example.client;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Scanner;

/**
 * Sample Eureka client that discovers the restaurant service using Eureka and sends requests.
 */
public class ExampleEurekaClient {

    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;
    public static String vipAddress = "book";

    private static synchronized void initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }
    }

    private static synchronized void initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        if (eurekaClient == null) {
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }
    }

    /**
     * Gets the list of books by calling the API
     * @return string representation of all the books currently in the restaurant
     */
    private static String getBooks() {
        // Create closeable http client to execute requests with
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Creating the request to execute
            String url = getServiceURL() + "/bookform";
            HttpGet httpget = new HttpGet(url);

            // Executing the request using the http client and obtaining the response
            CloseableHttpResponse response = client.execute(httpget);
            return readResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to get books";
        }
    }

    /**
     * Method for creating a book using the REST API
     * @param title of book
     * @param author of book
     * @param isbn of book
     */
    @Bean
    private static void createBook(String title, String author, String isbn) {
        // In this example we are using some classes included in the spring framework
        // to make  a simple POST request easily. You can also use your existing
        // implementation for the POST request.
        String url = getServiceURL();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
        map.add("title", title);
        map.add("author", author);
        map.add("isbn", isbn);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        // With RestTemplate we can make a POST request using HttpEntity
        String httpResponse = restTemplate().postForEntity(url + "/bookform", request , String.class).toString();

        System.out.println(httpResponse);
    }

    /**
     * Method for getting the home page url of the eureka service, using vipAddress
     * @return string with service url
     */
    public static String getServiceURL(){
        InstanceInfo nextServerInfo = null;
        try {
            nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
        } catch (Exception e) {
            System.err.println("Cannot get an instance of example service to talk to from eureka");
            System.exit(-1);
        }
        return nextServerInfo.getHomePageUrl() + vipAddress;
    }

    /**
     * Reads the response and converts it into a string
     * @param response response from http request
     * @return string of the response
     * @throws IOException
     */
    public static String readResponse(CloseableHttpResponse response) throws IOException {
        // Handling the IO Stream from the response using scanner
        Scanner sc = new Scanner(response.getEntity().getContent());
        StringBuilder stringResponse = new StringBuilder();
        while (sc.hasNext()) {
            stringResponse.append(sc.nextLine());
            stringResponse.append("\n");
        }
        response.close();
        return stringResponse.toString();
    }

    /**
     * Returns an instance of the class RestTemplate, to be used for HTTP requests
     * @return instance of RestTemplate
     */
    @Bean
    static RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        // initialize the client
        initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());

        createBook("title1", "author1", "3e4r5t");
        createBook("title2", "author2", "5e4r5t");
        createBook("title3", "author3", "3e6r7t");

        System.out.println(getBooks());

        // shutdown the client
        eurekaClient.shutdown();
    }

}