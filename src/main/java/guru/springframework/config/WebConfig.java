package guru.springframework.config;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * Configuration bean to provide api services.
 * <br><br>
 * User: carlosmartinez<br>
 * Date: 2019-03-29<br>
 * Time: 11:43<br>
 */
@Configuration
public class WebConfig {

  @Bean
  public RouterFunction<?> routes(final RecipeService recipeService) {
    return RouterFunctions.route(GET("/api/recipes"), serverRequest -> ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(recipeService.getRecipes(), Recipe.class));
  }
}