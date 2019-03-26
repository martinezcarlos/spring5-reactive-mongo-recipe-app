package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Created by carlosmartinez on 2019-03-26 12:21
 */
public interface UnitOfMeasureReactiveRepository
    extends ReactiveMongoRepository<UnitOfMeasure, String> {

  Mono<UnitOfMeasure> findByDescription(String description);
}
