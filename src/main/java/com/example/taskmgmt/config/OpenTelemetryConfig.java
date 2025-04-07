package com.example.taskmgmt.config;

import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

@Configuration
public class OpenTelemetryConfig {

    @PostConstruct
    public void initOpenTelemetry() {
        Resource serviceNameResource = Resource.create(
            Attributes.of(AttributeKey.stringKey("service.name"), "taskmgmt")
        );
        
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:4317")
            .build();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .setResource(Resource.getDefault().merge(serviceNameResource))
            .build();
        
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
            .setTracerProvider(tracerProvider)
            .buildAndRegisterGlobal();
    }

    public static Tracer tracer() {
        return GlobalOpenTelemetry.getTracer("taskmgmt");
    }
}
