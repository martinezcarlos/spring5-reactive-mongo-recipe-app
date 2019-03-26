package guru.springframework.repositories.reactive;

import guru.springframework.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Reactive repository for Recipe class.
 * User: carlosmartinez
 * Date: 2019-03-26
 * Time: 16:30
 */
public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {
}
