package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.service.RoundService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testing:
 * {@link RoundController}
 *
 * TODO: still not working - preauthorization validation is not invoked in the controller method
 *
 */
@ContextConfiguration(classes = {RoundController.class})
@AutoConfigureMockMvc
@WebMvcTest
@Slf4j
class RoundControllerTest {

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private RoundService roundService;

  @MockBean
  private RoundController roundController;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser(
          username = "user_regular",
          authorities = {"ROLE_USER"},
          roles = {"USER"})
  void testRegularUser() throws Exception {
    final int value = 5;

    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();

    final MvcResult result = mockMvc
            .perform(MockMvcRequestBuilders.get("/rounds/dummyEndpoint/" + value)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNonAuthoritativeInformation())
            .andReturn();

    final String responseContent = result.getResponse().getContentAsString();
    final int responseValue = Integer.valueOf(responseContent);

    assertEquals(value * 2, responseValue);
  }

}
