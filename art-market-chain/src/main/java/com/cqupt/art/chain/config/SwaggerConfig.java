package com.cqupt.art.chain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


//@Configuration
//@EnableWebMvc
public class SwaggerConfig {

//    @Bean
//    public OpenAPI springShopOpenApi(){
//        return new OpenAPI()
//                .info(new Info().title("链上操作服务文档")
//                        .description("spring doc创建文档")
//                        .version("v0.0.1"))
//                .externalDocs(new ExternalDocumentation()
//                        .description("SpringShop Wiki Documentation")
//                        .url("https://springshop.wiki.github.org/docs"));
//    }


//    @Bean
//    public Docket docket(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .useDefaultResponseMessages(false)
//                .apiInfo(apiInfo())
//                .groupName("chain")
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo(){
//        return new ApiInfoBuilder()
//                .title("链上操作服务文档")
//                .version("0.0.1")
//                .build();
//    }

//    @Bean
//    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
//                                                                         ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier,
//                                                                         EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties,
//                                                                         WebEndpointProperties webEndpointProperties, Environment environment) {
//        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
//        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
//        allEndpoints.addAll(webEndpoints);
//        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
//        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
//        String basePath = webEndpointProperties.getBasePath();
//        EndpointMapping endpointMapping = new EndpointMapping(basePath);
//        boolean shouldRegisterLinksMapping =
//                webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath)
//                        || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
//        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes,
//                corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath),
//                shouldRegisterLinksMapping, null);
//    }

}
