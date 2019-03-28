package guru.springframework.services;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.converters.IngredientCommandToIngredient;
import guru.springframework.converters.IngredientToIngredientCommand;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

  private final IngredientToIngredientCommand ingredientToIngredientCommand;
  private final IngredientCommandToIngredient ingredientCommandToIngredient;
  private final RecipeReactiveRepository recipeReactiveRepository;
  private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

  public IngredientServiceImpl(final IngredientToIngredientCommand ingredientToIngredientCommand,
      final IngredientCommandToIngredient ingredientCommandToIngredient,
      final RecipeReactiveRepository recipeReactiveRepository,
      final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
    this.ingredientToIngredientCommand = ingredientToIngredientCommand;
    this.ingredientCommandToIngredient = ingredientCommandToIngredient;
    this.recipeReactiveRepository = recipeReactiveRepository;
    this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
  }

  @Override
  public Mono<IngredientCommand> findByRecipeIdAndIngredientId(final String recipeId,
      final String ingredientId) {
    return recipeReactiveRepository.findById(recipeId)
        .flatMapIterable(Recipe::getIngredients)
        .filter(i -> i.getId().equalsIgnoreCase(ingredientId))
        .single()
        .map(i -> {
          final IngredientCommand ic = ingredientToIngredientCommand.convert(i);
          ic.setRecipeId(recipeId);
          return ic;
        });
  }

  @Override
  public Mono<IngredientCommand> saveIngredientCommand(final IngredientCommand command) {
    final Recipe recipe = recipeReactiveRepository.findById(command.getRecipeId()).block();

    if (recipe == null) {

      //todo toss error if not found!
      log.error("Recipe not found for id: " + command.getRecipeId());
      return Mono.just(new IngredientCommand());
    } else {
      final Optional<Ingredient> ingredientOptional = recipe.getIngredients()
          .stream()
          .filter(ingredient -> ingredient.getId().equals(command.getId()))
          .findFirst();

      final UnitOfMeasure unitOfMeasure = unitOfMeasureReactiveRepository.findById(
          command.getUom().getId()).block();
      if (ingredientOptional.isPresent()) {
        final Ingredient ingredientFound = ingredientOptional.get();
        ingredientFound.setDescription(command.getDescription());
        ingredientFound.setAmount(command.getAmount());
        ingredientFound.setUom(unitOfMeasure);
      } else {
        //add new Ingredient
        command.setId(UUID.randomUUID().toString());
        final Ingredient ingredient = ingredientCommandToIngredient.convert(command);
        ingredient.setUom(unitOfMeasure);
        //  ingredient.setRecipe(recipe);
        recipe.addIngredient(ingredient);
      }

      final Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

      Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients()
          .stream()
          .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
          .findFirst();

      //check by description
      if (!savedIngredientOptional.isPresent()) {
        //not totally safe... But best guess
        savedIngredientOptional = savedRecipe.getIngredients()
            .stream()
            .filter(recipeIngredients -> recipeIngredients.getDescription()
                .equals(command.getDescription()))
            .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
            .filter(recipeIngredients -> recipeIngredients.getUom()
                .getId()
                .equals(command.getUom().getId()))
            .findFirst();
      }

      //todo check for fail

      //enhance with id value
      final IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(
          savedIngredientOptional.get());
      ingredientCommandSaved.setRecipeId(recipe.getId());

      return Mono.just(ingredientCommandSaved);
    }
  }

  @Override
  public Mono<Void> deleteById(final String recipeId, final String idToDelete) {

    log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

    final Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

    if (recipe != null) {
      log.debug("found recipe");

      final Optional<Ingredient> ingredientOptional = recipe.getIngredients()
          .stream()
          .filter(ingredient -> ingredient.getId().equals(idToDelete))
          .findFirst();

      if (ingredientOptional.isPresent()) {
        log.debug("found Ingredient");
        recipe.getIngredients().remove(ingredientOptional.get());
        recipeReactiveRepository.save(recipe).block();
      }
    } else {
      log.debug("Recipe Id Not found. Id:" + recipeId);
    }
    return Mono.empty();
  }
}
