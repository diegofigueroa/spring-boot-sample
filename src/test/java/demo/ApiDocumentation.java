package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.domain.model.User;
import demo.domain.respository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static javax.servlet.RequestDispatcher.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.RestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class ApiDocumentation {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration()).build();
    }

    @Test
    public void errorExample() throws Exception {
        mockMvc.perform(get("/error")
                        .requestAttr(ERROR_STATUS_CODE, 400)
                        .requestAttr(ERROR_REQUEST_URI, "/v1/users")
                        .requestAttr(ERROR_MESSAGE, "The user 'http://localhost:8080/users/123' does not exist"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is("Bad Request")))
                .andExpect(jsonPath("timestamp", is(notNullValue())))
                .andExpect(jsonPath("status", is(400)))
                .andExpect(jsonPath("path", is(notNullValue())))
                .andDo(document("error-example")
                        .withResponseFields(
                                fieldWithPath("error").description("The HTTP error that occurred, e.g. `Bad Request`"),
                                fieldWithPath("message").description("A description of the cause of the error"),
                                fieldWithPath("path").description("The path to which the request was made"),
                                fieldWithPath("status").description("The HTTP status code, e.g. `400`"),
                                fieldWithPath("timestamp").description("The time, in milliseconds, at which the error occurred")));
    }

    @Test
    public void usersListExample() throws Exception {
        userRepository.deleteAll();

        createUser("Anakin Skywalker", "anakin.skywalker@empire.com");
        createUser("Luke Skywalkler", "luke@rebels.com");
        createUser("Han Solo", "han@solo.com");

        this.mockMvc.perform(get("/v1/users").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("users-list-example"));
    }

    @Test
    public void usersCreateExample() throws Exception {
        final Map<String, String> user = new HashMap<>();
        user.put("name", "Luke Skywalker");
        user.put("email", "luke@rebels.com");

        mockMvc.perform(
                post("/v1/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getHeader("Location");

        mockMvc.perform(
                post("/v1/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("users-create-example")
                        .withRequestFields(
                                fieldWithPath("name").description("The name of the user."),
                                fieldWithPath("email").description("The email of the user.")));
    }

    @Test
    public void userGetExample() throws Exception {
        final Map<String, String> user = new HashMap<>();
        user.put("name", "Luke Skywalker");
        user.put("email", "luke@rebels.com");

        final String userLocation = mockMvc
                .perform(post("/v1/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getHeader("Location");

        mockMvc.perform(get(userLocation))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(user.get("name"))))
                .andExpect(jsonPath("email", is(user.get("email"))))
                .andDo(document("user-get-example")
                        .withResponseFields(
                                fieldWithPath("name").description("The name of the user"),
                                fieldWithPath("email").description("The email of the user"),
                                fieldWithPath("id").description("The auto-generated id of the user.")));

    }

//    @Test()
//    public void userUpdateExample() throws Exception {
//        final Map<String, String> user = new HashMap<>();
//        user.put("name", "Anakin Skywalker");
//        user.put("email", "anakin@jedi.com");
//
//        final String tagLocation = mockMvc
//                .perform(post("/v1/users").contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isCreated()).andReturn().getResponse()
//                .getHeader("Location");
//
//        final Map<String, Object> updatedUser = new HashMap<>();
//        updatedUser.put("name", "Darth Vader");
//        updatedUser.put("email", "lord.vader@empire.com");
//
//        mockMvc.perform(
//                patch(tagLocation).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUser)))
//                .andExpect(status().isNoContent())
//                .andDo(document("user-update-example")
//                        .withRequestFields(
//                                fieldWithPath("name").description("The name of the user"),
//                                fieldWithPath("email").description("The email of the user"),
//                                fieldWithPath("id").description("The auto-generated id of the user.")));
//    }

    private void createUser(final String name, final String email) {
        final User user = new User();
        user.setName(name);
        user.setEmail(email);

        userRepository.save(user);
    }
}
