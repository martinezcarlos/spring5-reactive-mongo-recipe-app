package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
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
 * Time: 17:54
 */
@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {

  @Autowired
  private UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

  @Before
  public void setUp() {
    unitOfMeasureReactiveRepository.deleteAll().block();
  }

  @Test
  public void insertRecord() {
    // Given
    final UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
    unitOfMeasure.setDescription("Dummy desc");
    unitOfMeasureReactiveRepository.save(unitOfMeasure).block();
    // When
    final long count = Optional.ofNullable(unitOfMeasureReactiveRepository.count().block())
        .orElse(0L);
    // Then
    assertEquals(1L, count);
  }

  @Test
  public void findByDescription() {
    // Given
    final UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
    final String dummy_desc = "Dummy desc";
    unitOfMeasure.setDescription(dummy_desc);
    unitOfMeasureReactiveRepository.save(unitOfMeasure).block();
    // When
    final UnitOfMeasure uomFetched = unitOfMeasureReactiveRepository.findByDescription(dummy_desc)
        .block();
    // Then
    assertNotNull(uomFetched.getId());
  }
}
