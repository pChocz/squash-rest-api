package com.pj.squashrestapp.controller;

import com.pj.squashrestapp.dbinit.jsondto.JsonRound;
import com.pj.squashrestapp.service.RoundService;
import com.pj.squashrestapp.util.GsonUtil;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testing:
 * {@link RoundController}
 *
 * TODO: still not working - preauthorization validation is not invoked in the controller method!!
 *
 */
@ContextConfiguration(classes = {RoundController.class})
@AutoConfigureMockMvc
@WebMvcTest
@Slf4j
class RoundControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;

  @MockBean
  private RoundService roundService;

//  @Test
//  @WithMockUser(username = "user_admin", authorities = {"ROLE_ADMIN"})
//  void testRegularUser() throws Exception {
//    mockMvc = MockMvcBuilders
//            .webAppContextSetup(context)
//            .apply(springSecurity())
//            .build();
//
//    final String roundUuid = "c145b024-fded-480a-b46b-ccfb2aeb9228";
//
//    final MvcResult result = mockMvc
//            .perform(MockMvcRequestBuilders.get("/backup/rounds/" + roundUuid)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andReturn();
//
//    final String responseContent = result.getResponse().getContentAsString();
//    final JsonRound jsonRound = GsonUtil.gsonWithDate().fromJson(responseContent, JsonRound.class);
//
//    log.info("finished without errors");
//  }

}
