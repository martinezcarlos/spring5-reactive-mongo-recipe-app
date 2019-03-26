package guru.springframework.repositories.reactive;

import guru.springframework.domain.Recipe;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * User: carlosmartinez
 * Date: 2019-03-26
 * Time: 17:51
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {

  @Autowired
  private RecipeReactiveRepository recipeReactiveRepository;

  @Before
  public void setUp() {
    recipeReactiveRepository.deleteAll().block();
  }

  @Test
  public void insertRecord() {
    // Given
    final Recipe recipe = new Recipe();
    recipe.setDescription("Dummy desc");
    recipeReactiveRepository.save(recipe).block();
    // When
    final long count = Optional.ofNullable(recipeReactiveRepository.count().block()).orElse(0L);
    // Then
    assertEquals(1L, count);
  }
}
