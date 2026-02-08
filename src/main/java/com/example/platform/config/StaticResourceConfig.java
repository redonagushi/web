//Rregullon si shërbehen file-t statikë (/static/**: html, css, js).
//
//Shpesh përdoret që index.html, admin.html, profile.html të hapen
// si faqe statike dhe të mos bllokohen nga security.

package com.example.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
