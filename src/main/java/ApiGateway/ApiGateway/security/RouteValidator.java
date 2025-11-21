package ApiGateway.ApiGateway.security;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(

            "/authservice/registerUser",
            "/authservice/getToken",
            "/authservice/validateToken/**",
            "/authservice/**"
    );

    public Predicate<ServerHttpRequest> isSecure =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> {
                        if(uri.endsWith("/**")) {
                            String baseUri = uri.replace("/**", "");
                            return request.getURI().getPath().startsWith(baseUri);
                        } else {
                            return request.getURI().getPath().equals(uri);
                        }
                    });

}
