package myapp.bitcoin_analyze.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                    name = "Ly Phu",
                    email = "noisim89@gmail.com",
                    url="https://toicodon-thanakha.com/"
                ),
                description = "OpenAPI documentation for crawl and analyze bitcoin price",
                title = "OpenAPI specification - Ly Phu",
                version = "1.0",
                license = @License(
                        name = "Ly Phu",
                        url="https://toicodon-thanakha.com/"
                )
        )
)
public class OpenApiConfig {
}
