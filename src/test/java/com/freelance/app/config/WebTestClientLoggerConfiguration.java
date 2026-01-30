package com.freelance.app.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TestConfiguration
@ConditionalOnProperty(
    name = { "logging.level.com.freelance", "logging.level.reactor.netty.http.client.HttpClient" },
    havingValue = "DEBUG"
)
public class WebTestClientLoggerConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(WebTestClientLoggerConfiguration.class);

    private static final int MAX_BODY_LENGTH = 10_000;
    private static final List<String> BINARY_CONTENT_TYPES = List.of(
        "application/octet-stream",
        "image/",
        "video/",
        "audio/",
        "application/pdf"
    );
    private static final List<String> SENSITIVE_HEADERS = List.of("authorization", "cookie", "password", "token", "secret", "api-key");
    private static final String BOX_VERTICAL = "║ ";
    private static final String BOX_HORIZONTAL = "══".repeat(60);
    private static final String BOX_TOP = "╔".concat(BOX_HORIZONTAL);
    private static final String BOX_MID = "╠".concat(BOX_HORIZONTAL);
    private static final String BOX_BOTTOM = "╚".concat(BOX_HORIZONTAL);
    private static final String START_TIME_ATTR = "startTime";

    //    @Bean
    //    public WebTestClientBuilderCustomizer webTestClientBuilderCustomizer() {
    //        return (builder) -> builder
    //            .build()
    //            .bindToServer(
    //                new ReactorClientHttpConnector(
    //                HttpClient.create().wiretap(
    //                    "reactor.netty.http.client.HttpClient",
    //                    LogLevel.DEBUG,
    //                    AdvancedByteBufFormat.TEXTUAL
    //                )
    //            ));
    //    }

    /**
     * Create a WebTestClient with detailed logging for debugging
     */
    @Bean
    public WebTestClientBuilderCustomizer loggingWebTestClientCustomizer() {
        LOG.warn(">>> WebTestClientLoggerConfiguration ENABLED <<<");
        return builder -> builder.filter(logRequest()).filter(logResponse());
    }

    /**
     * Filter to log HTTP request details (method, URL, query parameters, headers, body)
     */
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            if (!LOG.isDebugEnabled()) {
                return Mono.just(request);
            }
            Instant startTime = Instant.now();
            ClientRequest mutatedRequest = ClientRequest.from(request).attribute(START_TIME_ATTR, startTime).build();
            logRequestDetails(mutatedRequest);
            return Mono.just(mutatedRequest);
        });
    }

    /**
     * Filter to log HTTP response details (status, headers, body)
     */
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (!LOG.isDebugEnabled()) {
                return Mono.just(response);
            }

            return DataBufferUtils.join(response.bodyToFlux(DataBuffer.class))
                .map(dataBuffer -> {
                    MediaType contentType = response.headers().contentType().orElse(null);
                    logResponseDetails(response, null, contentType, dataBuffer);
                    DataBufferUtils.release(dataBuffer);
                    return response.mutate().body(Flux.just(dataBuffer)).build();
                })
                .defaultIfEmpty(response)
                .doOnNext(resp -> {
                    if (resp == response) { // No body case
                        logResponseDetails(response, "<empty>", null, null);
                    }
                })
                .onErrorResume(e -> {
                    logResponseDetails(response, "<error: " + e.getMessage() + ">", null, null);
                    return Mono.just(response);
                });
        });
    }

    /**
     * Logs request details in a boxed format.
     */
    private static void logRequestDetails(ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(BOX_TOP).append("\n");
        sb.append(BOX_VERTICAL).append("📤 HTTP REQUEST\n");
        sb.append(BOX_MID).append("\n");
        sb.append(BOX_VERTICAL).append("Timestamp: ").append(Instant.now()).append("\n");
        sb.append(BOX_VERTICAL).append("Method: ").append(request.method()).append("\n");
        sb.append(BOX_VERTICAL).append("URL: ").append(request.url()).append("\n");

        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.url()).build().getQueryParams();
        if (!queryParams.isEmpty()) {
            sb.append(BOX_VERTICAL).append("Query Parameters:\n");
            queryParams.forEach((key, values) ->
                values.forEach(value -> sb.append(BOX_VERTICAL).append("  ").append(key).append(" = ").append(value).append("\n"))
            );
        }

        sb.append(BOX_VERTICAL).append("Headers:\n");
        appendHeaders(sb, request.headers());

        sb.append(BOX_VERTICAL).append("Body: <request body not logged in client-side filter>\n");
        sb.append(BOX_BOTTOM).append("\n");

        LOG.debug(sb.toString());
    }

    /**
     * Logs response details in a boxed format
     */
    private static void logResponseDetails(ClientResponse response, String body, MediaType contentType, DataBuffer dataBuffer) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append(BOX_TOP).append("\n");
        sb.append(BOX_VERTICAL).append("📥 HTTP RESPONSE\n");
        sb.append(BOX_MID).append("\n");
        sb.append(BOX_VERTICAL).append("Timestamp: ").append(Instant.now()).append("\n");

        HttpRequest request = response.request();
        sb.append(BOX_VERTICAL).append("Request Method: ").append(request.getMethod()).append("\n");
        sb.append(BOX_VERTICAL).append("Request URL: ").append(request.getURI()).append("\n");
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
        if (!queryParams.isEmpty()) {
            sb.append(BOX_VERTICAL).append("Request Query Parameters:\n");
            queryParams.forEach((key, values) ->
                values.forEach(value -> sb.append(BOX_VERTICAL).append("  ").append(key).append(" = ").append(value).append("\n"))
            );
        }

        HttpStatus status = HttpStatus.resolve(response.statusCode().value());
        sb
            .append(BOX_VERTICAL)
            .append("Status: ")
            .append(response.statusCode().value())
            .append(" (")
            .append(status != null ? status.getReasonPhrase() : "")
            .append(")\n");

        sb.append(BOX_VERTICAL).append("Headers:\n");
        appendHeaders(sb, response.headers().asHttpHeaders());

        appendBody(sb, body, contentType, dataBuffer);

        Object startTimeObj = response.request().getAttributes().get(START_TIME_ATTR);
        if (startTimeObj instanceof Instant startTime) {
            sb.append(BOX_VERTICAL).append("Duration: ").append(Duration.between(startTime, Instant.now()).toMillis()).append(" ms\n");
        }

        sb.append(BOX_BOTTOM).append("\n");
        LOG.debug(sb.toString());
    }

    private static String extractBody(DataBuffer buffer, MediaType contentType) {
        if (buffer == null || buffer.readableByteCount() == 0) {
            return "<empty>";
        }
        if (isBinaryContent(contentType)) {
            return "<binary data, " + buffer.readableByteCount() + " bytes>";
        }
        String body = buffer.toString(StandardCharsets.UTF_8);
        return body.length() > MAX_BODY_LENGTH
            ? body.substring(0, MAX_BODY_LENGTH) + "\n... [TRUNCATED - Total length: " + body.length() + " chars] ..."
            : body;
    }

    private static void appendHeaders(StringBuilder sb, MultiValueMap<String, String> headers) {
        headers.forEach((name, values) ->
            values.forEach(value -> {
                String displayValue = SENSITIVE_HEADERS.stream().anyMatch(name.toLowerCase()::contains) ? "***REDACTED***" : value;
                sb.append(BOX_VERTICAL).append("  ").append(name).append(": ").append(displayValue).append("\n");
            })
        );
    }

    private static void appendBody(StringBuilder sb, String body, MediaType contentType, DataBuffer dataBuffer) {
        if (body == null && dataBuffer == null) {
            sb.append(BOX_VERTICAL).append("  ").append("<null>").append("\n");
            return;
        }

        if (body != null && (body.isEmpty() || body.startsWith("<"))) {
            sb.append(BOX_VERTICAL).append("Body:\n");
            sb.append(BOX_VERTICAL).append("  ").append(body).append("\n");
            return;
        }

        sb.append(BOX_VERTICAL).append("Body Size: ").append(dataBuffer.readableByteCount()).append(" bytes\n");
        sb.append(BOX_VERTICAL).append("Body:\n");

        body = extractBody(dataBuffer, contentType);

        sb.append(BOX_VERTICAL).append("  ").append(body).append("\n");
    }

    private static boolean isBinaryContent(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        String type = contentType.toString().toLowerCase();
        return BINARY_CONTENT_TYPES.stream().anyMatch(type::startsWith);
    }
}
