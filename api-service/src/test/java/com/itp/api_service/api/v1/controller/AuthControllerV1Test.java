package com.itp.api_service.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;
import com.itp.api_service.api.v1.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {
    "server.servlet.context-path=",
    "spring.mvc.servlet.path="
})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AuthControllerV1.class)
class AuthControllerV1Test {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockBean AuthService authService;

    @Test
    void tokenCreated_returns201() throws Exception {
        var req  = new TokenRequest("all read:stats");
        var resp = TokenResponse.of("jwt", 60, Set.of("read:stats"));
        when(authService.getToken(any())).thenReturn(resp);

        mvc.perform(post("/v1/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.data.token", is("jwt")));
    }

}
