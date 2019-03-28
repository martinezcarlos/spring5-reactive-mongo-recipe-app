package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

  private final RecipeReactiveRepository recipeReactiveRepository;
  private final RecipeCommandToRecipe recipeCommandToRecipe;
  private final RecipeToRecipeCommand recipeToRecipeCommand;

  public RecipeServiceImpl(final RecipeReactiveRepository recipeReactiveRepository,
      final RecipeCommandToRecipe recipeCommandToRecipe,
      final RecipeToRecipeCommand recipeToRecipeCommand) {
    this.recipeReactiveRepository = recipeReactiveRepository;
    this.recipeCommandToRecipe = recipeCommandToRecipe;
    this.recipeToRecipeCommand = recipeToRecipeCommand;
  }

  @Override
  public Flux<Recipe> getRecipes() {
    log.debug("I'm in the service");
    return recipeReactiveRepository.findAll();
  }

  @Override
  public Mono<Recipe> findById(final String id) {
    return recipeReactiveRepository.findById(id);
  }

  @Override
  public Mono<RecipeCommand> findCommandById(final String id) {
    return findById(id).map(r -> {
      final RecipeCommand recipeCommand = recipeToRecipeCommand.convert(r);
      recipeCommand.getIngredients().forEach(i -> i.setRecipeId(recipeCommand.getId()));
      return recipeCommand;
    });
  }

  @Override
  public Mono<RecipeCommand> saveRecipeCommand(final RecipeCommand command) {
    return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
        .map(recipeToRecipeCommand::convert);
  }

  @Override
  public Mono<Void> deleteById(final String idToDelete) {
    recipeReactiveRepository.deleteById(idToDelete).block();
    return Mono.empty();
  }
}
