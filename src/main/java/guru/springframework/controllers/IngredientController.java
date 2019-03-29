package guru.springframework.controllers;

import guru.springframework.commands.IngredientCommand;
import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.services.IngredientService;
import guru.springframework.services.RecipeService;
import guru.springframework.services.UnitOfMeasureService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Controller
public class IngredientController {

  private final IngredientService ingredientService;
  private final RecipeService recipeService;
  private final UnitOfMeasureService unitOfMeasureService;

  public IngredientController(final IngredientService ingredientService,
      final RecipeService recipeService, final UnitOfMeasureService unitOfMeasureService) {
    this.ingredientService = ingredientService;
    this.recipeService = recipeService;
    this.unitOfMeasureService = unitOfMeasureService;
  }

  @GetMapping("/recipe/{recipeId}/ingredients")
  public String listIngredients(@PathVariable final String recipeId, final Model model) {
    log.debug("Getting ingredient list for recipe id: " + recipeId);

    // use command object to avoid lazy load errors in Thymeleaf.
    model.addAttribute("recipe", recipeService.findCommandById(recipeId));

    return "recipe/ingredient/list";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
  public String showRecipeIngredient(@PathVariable final String recipeId,
      @PathVariable final String id, final Model model) {
    model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
    return "recipe/ingredient/show";
  }

  @GetMapping("recipe/{recipeId}/ingredient/new")
  public String newRecipeIngredient(@PathVariable final String recipeId, final Model model) {

    //make sure we have a good id value
    //final RecipeCommand recipeCommand = recipeService.findCommandById(recipeId).block();
    //todo raise exception if null

    //need to return back parent id for hidden form property
    final IngredientCommand ingredientCommand = new IngredientCommand();
    ingredientCommand.setRecipeId(recipeId);
    model.addAttribute("ingredient", ingredientCommand);

    //init uom
    ingredientCommand.setUom(new UnitOfMeasureCommand());

    return "recipe/ingredient/ingredientform";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
  public String updateRecipeIngredient(@PathVariable final String recipeId,
      @PathVariable final String id, final Model model) {
    model.addAttribute("ingredient",
        ingredientService.findByRecipeIdAndIngredientId(recipeId, id).block());
    return "recipe/ingredient/ingredientform";
  }

  @PostMapping("recipe/{recipeId}/ingredient")
  public String saveOrUpdate(@Valid @ModelAttribute final IngredientCommand command,
      final BindingResult bindingResult, final Model model) {
    if (bindingResult.hasErrors()) {

      bindingResult.getAllErrors().forEach(objectError -> {
        log.debug(objectError.toString());
      });

      model.addAttribute("ingredient", command);
      return "/recipe/ingredient/ingredientform";
    }

    final IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command).block();

    log.debug("saved ingredient id:" + savedCommand.getId());

    return "redirect:/recipe/"
        + savedCommand.getRecipeId()
        + "/ingredient/"
        + savedCommand.getId()
        + "/show";
  }

  @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
  public String deleteIngredient(@PathVariable final String recipeId,
      @PathVariable final String id) {

    log.debug("deleting ingredient id:" + id);
    ingredientService.deleteById(recipeId, id).block();

    return "redirect:/recipe/" + recipeId + "/ingredients";
  }

  @ModelAttribute("uomList")
  public Flux<UnitOfMeasureCommand> populateUomList() {
    return unitOfMeasureService.listAllUoms();
  }
}
