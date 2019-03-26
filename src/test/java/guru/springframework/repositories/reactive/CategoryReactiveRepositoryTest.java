package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: carlosmartinez
 * Date: 2019-03-26
 * Time: 16:55
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {

  @Autowired
  CategoryReactiveRepository categoryReactiveRepository;

  @Before
  public void setUp() {
    categoryReactiveRepository.deleteAll().block();
  }

  @Test
  public void insertRecord() {
    // Given
    final Category cat = new Category();
    cat.setDescription("Dummy desc");
    categoryReactiveRepository.save(cat).block();
    // When
    final long count = Optional.ofNullable(categoryReactiveRepository.count().block()).orElse(0L);
    // Then
    assertEquals(1L, count);
  }

  @Test
  public void findByDescription() {
    // Given
    final Category cat = new Category();
    final String dummy_desc = "Dummy desc";
    cat.setDescription(dummy_desc);
    categoryReactiveRepository.save(cat).block();
    // When
    final Category catFetched = categoryReactiveRepository.findByDescription(dummy_desc).block();
    // Then
    assertNotNull(catFetched.getId());
  }
}
