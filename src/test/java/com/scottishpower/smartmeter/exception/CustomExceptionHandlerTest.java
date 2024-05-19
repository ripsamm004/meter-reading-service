package com.scottishpower.smartmeter.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.scottishpower.smartmeter.enums.Error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CustomExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void whenBadRequest_thenReturnBadRequestResponse() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(Error.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value(Error.BAD_REQUEST.getMessage()));
    }

    @Test
    void whenDuplicateResource_thenReturnConflictResponse() throws Exception {
        mockMvc.perform(get("/test/duplicate-resource"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(Error.DUPLICATE_RESOURCE.getCode()))
                .andExpect(jsonPath("$.message").value(Error.DUPLICATE_RESOURCE.getMessage()));
    }

    @Test
    void whenInternalError_thenReturnInternalErrorResponse() throws Exception {
        mockMvc.perform(get("/test/internal-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(Error.INTERNAL_ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(Error.INTERNAL_ERROR.getMessage()));
    }

    @Test
    void whenMethodArgumentNotValid_thenReturnBadRequestResponse() throws Exception {
        String requestBody = "{\"field\":\"\"}";
        mockMvc.perform(post("/test/method-argument-not-valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(Error.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value("Field cannot be empty"));
    }

    @Test
    void whenMethodNotAllowed_thenReturnMethodNotAllowedResponse() throws Exception {
        mockMvc.perform(post("/test/method-not-allowed"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(Error.METHOD_NOT_ALLOWED.getCode()))
                .andExpect(jsonPath("$.message").value(Error.METHOD_NOT_ALLOWED.getMessage()));
    }
}
