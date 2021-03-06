package guru.springframework.controllers;

import guru.springframework.domain.Recipe;
import guru.springframework.services.RecipeService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by jt on 6/17/17.
 */
@Ignore
public class IndexControllerTest {

  @Mock
  private RecipeService recipeService;

  @Mock
  private Model model;

  private IndexController controller;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    controller = new IndexController(recipeService);
  }

  @Test
  public void testMockMVC() throws Exception {
    final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    when(recipeService.getRecipes()).thenReturn(Flux.empty());
    mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
  }

  @Test
  public void getIndexPage() throws Exception {

    //given
    final Set<Recipe> recipes = new HashSet<>();
    recipes.add(new Recipe());

    final Recipe recipe = new Recipe();
    recipe.setId("1");

    recipes.add(recipe);

    when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipes));

    final ArgumentCaptor<List<Recipe>> argumentCaptor = ArgumentCaptor.forClass(List.class);

    //when
    final String viewName = controller.getIndexPage(model);

    //then
    assertEquals("index", viewName);
    verify(recipeService, times(1)).getRecipes();
    verify(model, times(1)).addAttribute(eq("recipes"), argumentCaptor.capture());
    final List<Recipe> setInController = argumentCaptor.getValue();
    assertEquals(2, setInController.size());
  }
}
