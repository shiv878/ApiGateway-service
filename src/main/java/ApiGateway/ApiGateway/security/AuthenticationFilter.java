package ApiGateway.ApiGateway.security;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${authservice.validateToken.url}")
    private String authservice;
    private final Validation validation;

    private final WebClient.Builder webClientBuilder;


    @Autowired
    private RouteValidator routeValidator;

    public AuthenticationFilter(Validation validation, @LoadBalanced WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validation = validation;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
               System.out.println(exchange.getRequest());
            if(routeValidator.isSecure.test(exchange.getRequest())){
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                   throw new RuntimeException("Authorization header is missing ,please check your authorization header.");
                }

                String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if(token!=null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                try{
//                  return  webClientBuilder.build()
//                            .get()
//                            .uri("http://localhost:8084/validateToken/{token}", token)
//                            .retrieve()
//                            .bodyToMono(String.class)
//                            .flatMap(response -> {
//                                if ("valid".equalsIgnoreCase(response)) {
//                                    return chain.filter(exchange); // Continue to downstream service
//                                } else {
//                                    return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
//                                }
//                            })
//                            .onErrorResume(ex -> {
//                                ex.printStackTrace();
//                                return onError(exchange, "Auth service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
//                            });

                    validation.validateToken(token);

                }catch (Exception e){
                    throw new RuntimeException("Authorization header is invalid");
                }
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    // Inner static config class (can hold custom args from YAML)
    public static class Config {
        // Put configuration fields here (e.g., secret key, excluded paths)
    }
}
