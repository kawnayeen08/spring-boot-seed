package com.kawnayeen.logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by kawnayeen on 1/5/17.
 */
@WebAppConfiguration
public class AbstractControllerTest extends AbstractTest {

    protected MockMvc mvc;
    protected static final String AUTHORIZATION = "Authorization";

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    protected String generateBasicAuth(String username, String password) {
        return "Basic " + Base64Utils.encodeToString((username + ":" + password).getBytes());
    }

    protected String generateTokenAuth(String token) {
        return "Bearer " + token;
    }

    protected RestDocumentationResultHandler getDocument(String identifier) {
        return document(identifier, preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
    }

    protected MockHttpServletRequestBuilder generatePostRequest(String uri, String authorizationData, String requestBody) {
        return post(uri)
                .header(AUTHORIZATION, authorizationData)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody);
    }

    protected MockHttpServletRequestBuilder generateGetRequest(String uri, String authorizationData) {
        return get(uri)
                .header(AUTHORIZATION, authorizationData)
                .accept(MediaType.APPLICATION_JSON_VALUE);
    }
}
