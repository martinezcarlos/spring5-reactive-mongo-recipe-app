package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jt on 6/17/17.
 */
public class RecipeServiceImplTest {

  private RecipeServiceImpl recipeService;

  @Mock
  private RecipeReactiveRepository recipeReactiveRepository;

  @Mock
  private RecipeToRecipeCommand recipeToRecipeCommand;

  @Mock
  private RecipeCommandToRecipe recipeCommandToRecipe;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    recipeService = new RecipeServiceImpl(recipeReactiveRepository, recipeCommandToRecipe,
        recipeToRecipeCommand);
  }

  @Test
  public void getRecipeByIdTest() {
    final Recipe recipe = new Recipe();
    recipe.setId("1");

    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

    final Recipe recipeReturned = recipeService.findById("1").block();

    assertNotNull("Null recipe returned", recipeReturned);
    verify(recipeReactiveRepository, times(1)).findById(anyString());
    verify(recipeReactiveRepository, never()).findAll();
  }

  @Test//(expected = NotFoundException.class)
  public void getRecipeByIdTestNotFound() {

    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());

    final Recipe recipeReturned = recipeService.findById("1").block();

    //should go boom
    assertNull(recipeReturned);
  }

  @Test
  public void getRecipeCommandByIdTest() {
    final Recipe recipe = new Recipe();
    recipe.setId("1");

    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

    final RecipeCommand recipeCommand = new RecipeCommand();
    recipeCommand.setId("1");

    when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

    final RecipeCommand commandById = recipeService.findCommandById("1").block();

    assertNotNull("Null recipe returned", commandById);
    verify(recipeReactiveRepository, times(1)).findById(anyString());
    verify(recipeReactiveRepository, never()).findAll();
  }

  @Test
  public void getRecipesTest() {

    when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe()));

    final Flux<Recipe> recipes = recipeService.getRecipes();

    assertEquals(1, recipes.count().block().intValue());
    verify(recipeReactiveRepository, times(1)).findAll();
    verify(recipeReactiveRepository, never()).findById(anyString());
  }

  @Test
  public void testDeleteById() {

    //given
    final String idToDelete = "2";

    when(recipeReactiveRepository.deleteById(anyString())).thenReturn(Mono.empty());

    //when
    recipeService.deleteById(idToDelete);

    //then
    verify(recipeReactiveRepository, times(1)).deleteById(anyString());
  }
}
