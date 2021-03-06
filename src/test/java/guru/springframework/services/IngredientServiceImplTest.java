package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.converters.UnitOfMeasureCommandToUnitOfMeasure;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IngredientServiceImplTest {

  private final IngredientToIngredientCommand ingredientToIngredientCommand;
  private final IngredientCommandToIngredient ingredientCommandToIngredient;

  @Mock
  private RecipeReactiveRepository recipeReactiveRepository;

  @Mock
  private UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

  private IngredientService ingredientService;

  //init converters
  public IngredientServiceImplTest() {
    ingredientToIngredientCommand = new IngredientToIngredientCommand(
        new UnitOfMeasureToUnitOfMeasureCommand());
    ingredientCommandToIngredient = new IngredientCommandToIngredient(
        new UnitOfMeasureCommandToUnitOfMeasure());
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    ingredientService = new IngredientServiceImpl(ingredientToIngredientCommand,
        ingredientCommandToIngredient, recipeReactiveRepository, unitOfMeasureReactiveRepository);
  }

  @Test
  public void findByRecipeIdAndId() throws Exception {
  }

  @Test
  public void findByRecipeIdAndReceipeIdHappyPath() throws Exception {
    //given
    final Recipe recipe = new Recipe();
    recipe.setId("1");

    final Ingredient ingredient1 = new Ingredient();
    ingredient1.setId("1");

    final Ingredient ingredient2 = new Ingredient();
    ingredient2.setId("1");

    final Ingredient ingredient3 = new Ingredient();
    ingredient3.setId("3");

    recipe.addIngredient(ingredient1);
    recipe.addIngredient(ingredient2);
    recipe.addIngredient(ingredient3);

    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

    //then
    final IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId("1",
        "3").block();

    //when
    assertEquals("3", ingredientCommand.getId());
    verify(recipeReactiveRepository, times(1)).findById(anyString());
  }

  @Test
  public void testSaveRecipeCommand() throws Exception {
    //given
    final IngredientCommand command = new IngredientCommand();
    command.setId("3");
    command.setRecipeId("2");
    final UnitOfMeasureCommand uom = new UnitOfMeasureCommand();
    uom.setId("1");
    command.setUom(uom);

    final Recipe recipe = new Recipe();
    recipe.addIngredient(new Ingredient());
    recipe.getIngredients().iterator().next().setId("3");

    final Recipe savedRecipe = new Recipe();
    savedRecipe.addIngredient(new Ingredient());
    savedRecipe.getIngredients().iterator().next().setId("3");

    when(unitOfMeasureReactiveRepository.findById(anyString())).thenReturn(Mono.empty());
    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
    when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(savedRecipe));

    //when
    final IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

    //then
    assertEquals("3", savedCommand.getId());
    verify(recipeReactiveRepository, times(1)).findById(anyString());
    verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
  }

  @Test
  public void testDeleteById() throws Exception {
    //given
    final Recipe recipe = new Recipe();
    final Ingredient ingredient = new Ingredient();
    ingredient.setId("3");
    recipe.addIngredient(ingredient);

    when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
    when(recipeReactiveRepository.save(any())).thenReturn(Mono.just(recipe));

    //when
    ingredientService.deleteById("1", "3");

    //then
    verify(recipeReactiveRepository, times(1)).findById(anyString());
    verify(recipeReactiveRepository, times(1)).save(any(Recipe.class));
  }
}
