package com.example.financeanalyzer.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun financeAnalyzerOpenApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Personal Finance Analyzer API")
                    .description("REST API for transaction management and personal finance analytics.")
                    .version("v1")
                    .contact(
                        Contact()
                            .name("Finance Analyzer")
                            .email("support@example.com")
                    )
            )
    }
}
