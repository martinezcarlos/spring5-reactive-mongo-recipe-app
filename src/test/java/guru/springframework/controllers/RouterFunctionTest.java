package guru.springframework.controllers;

import guru.springframework.config.WebConfig;
import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

/**
 * Test for WebTestClient
 * <br><br>
 * User: carlosmartinez<br>
 * Date: 2019-03-29<br>
 * Time: 12:03<br>
 */
public class RouterFunctionTest {

  private WebTestClient webTestClient;

  @Mock
  private RecipeService recipeService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    final WebConfig webConfig = new WebConfig();
    final RouterFunction<?> routerFunction = webConfig.routes(recipeService);
    webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
  }

  @Test
  public void testGetRecipes() throws Exception {

    when(recipeService.getRecipes()).thenReturn(Flux.just());

    webTestClient.get()
        .uri("/api/recipes")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  public void testGetRecipesWithData() throws Exception {

    when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe(), new Recipe()));

    webTestClient.get()
        .uri("/api/recipes")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Recipe.class);
  }
}
