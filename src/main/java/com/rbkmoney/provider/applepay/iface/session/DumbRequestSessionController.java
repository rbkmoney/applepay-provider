package com.rbkmoney.provider.applepay.iface.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rbkmoney.provider.applepay.domain.SessionRequest;
import com.rbkmoney.provider.applepay.service.CertNotFoundException;
import com.rbkmoney.provider.applepay.service.SessionService;
import com.rbkmoney.woody.api.flow.error.WErrorType;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by vpankrashkin on 04.04.18.
 */

@RestController
@RequestMapping("/api/v1")
@Api(description = "Session creation API")
public class DumbRequestSessionController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionService service;

    private ObjectWriter writer = new ObjectMapper().writer();


    @ApiOperation(value = "Request ApplePay session", notes = "")
    @PostMapping(value = "/session", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, headers = "Content-Type=application/json")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "Apple Pay session object"),
            @ApiResponse(code = 500, message = "Internal service error"),
            @ApiResponse(code = 503, message = "Apple Pay service unavailable")
    })
    @CrossOrigin

    public ResponseEntity<String> getSession(@RequestBody SessionRequest request) {
        log.info("New Session request: {}", request);

        try {
            return ResponseEntity.ok(service.requestSession(request.getMerchantId(), request.getValidationURL(), writer.writeValueAsString(request.getBody())));
        } catch (CertNotFoundException e) {
            log.error("Merchant not found: " + request.getMerchantId(), e);
            return ResponseEntity.badRequest().body("Merchant not found");
        } catch (WRuntimeException e) {
            WErrorType errorType = e.getErrorDefinition().getErrorType();
            if (errorType == WErrorType.UNDEFINED_RESULT || errorType == WErrorType.UNAVAILABLE_RESULT) {
                log.warn("Apple pay service unavailable", e);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Third party service unavailable");
            } else {
                log.error("Failed to request session", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request session");
            }
        } catch (Exception e) {
            log.error("Failed to request session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request session");
        }

    }

}
