package net.n2oapp.demo;

import net.n2oapp.framework.boot.N2oFrameworkAutoConfiguration;
import net.n2oapp.framework.boot.sql.jdbc.RoutingDataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFlux
@SpringBootApplication
//@EnableRoutingDataSource
@Configuration
@Import({
        MessageSourceAutoConfiguration.class,
//        JndiDataSourceAutoConfiguration.class,
        EmbeddedDataSourceConfiguration.class,
        DataSourceAutoConfiguration.class,
//        JdbcTemplateAutoConfiguration.class,
//        TransactionAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
//        ErrorMvcAutoConfiguration.class,
        HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class,
//        JacksonAutoConfiguration.class,
//        PropertyPlaceholderAutoConfiguration.class,
//        ThymeleafAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
//        WebSocketAutoConfiguration.class,
        RoutingDataSourceAutoConfiguration.class,
        N2oFrameworkAutoConfiguration.class
})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Component
    public class CustomWebFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            if (exchange.getRequest().getURI().getPath().equals("/")) {
                return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build()).build());
            }

            return chain.filter(exchange);
        }
    }

    @Bean
    public RouterFunction<ServerResponse> imgRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource("public/"));
    }

}
