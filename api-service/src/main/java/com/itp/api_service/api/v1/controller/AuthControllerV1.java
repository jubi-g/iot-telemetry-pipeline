package com.itp.api_service.api.v1.controller;

import com.itp.api_service._commons.model.dto.TokenRequest;
import com.itp.api_service._commons.model.dto.TokenResponse;
import com.itp.api_service.api.v1.model.dto.ApiResponseBody;
import com.itp.api_service.api.v1.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth")
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthService service;

    @PostMapping("/token")
    @Operation(
        summary = "Issue JWT",
        description = "Returns a short-lived JWT. Public endpoint.",
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenRequest.class),
                examples = @ExampleObject(
                    name = "TokenRequest",
                    value = "{\"scope\":\"admin read:stats\"}"
                )
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Token issued",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
        }
    )
    public ApiResponseBody<TokenResponse> getSensorStatsById(@RequestBody TokenRequest request) {
        return new ApiResponseBody<>(HttpStatus.CREATED, service.getToken(request));
    }

}
