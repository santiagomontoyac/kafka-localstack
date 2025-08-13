package com.store.demo.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server

@OpenAPIDefinition(
    info = Info(
        contact = Contact(
            name = "Santiago Montoya",
            email = "santiago.montoya@endava.com",
            url = "https://www.linkedin.com/in/santiago-montoya-a4497b54/"
        ),
        description = "OpenApi documentation for Spring Security",
        title = "Kotlin Rest API Project",
        version = "1.0",
        license = License(name = "License name", url = "https://some-url.com"),
        termsOfService = "Terms of service"
    ),
    servers = [Server(description = "Local ENV", url = "http://localhost:8081"), Server(
        description = "PROD ENV",
        url = "http://localhost:8081"
    )]
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class OpenApiConfig {
}
