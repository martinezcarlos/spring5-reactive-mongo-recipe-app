package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for Category class.
 * User: carlosmartinez
 * Date: 2019-03-26
 * Time: 16:28
 */
public interface CategoryReactiveRepository extends ReactiveMongoRepository<Category, String> {

  Mono<Category> findByDescription(String description);
}
