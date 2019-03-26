package guru.springframework.services;

import guru.springframework.commands.UnitOfMeasureCommand;
import guru.springframework.converters.UnitOfMeasureToUnitOfMeasureCommand;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.reactive.UnitOfMeasureReactiveRepository;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UnitOfMeasureServiceImplTest {

  private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand
      = new UnitOfMeasureToUnitOfMeasureCommand();
  private UnitOfMeasureService service;

  @Mock
  private UnitOfMeasureReactiveRepository unitOfMeasureRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    service = new UnitOfMeasureServiceImpl(unitOfMeasureRepository,
        unitOfMeasureToUnitOfMeasureCommand);
  }

  @Test
  public void listAllUoms() throws Exception {
    //given
    final UnitOfMeasure uom1 = new UnitOfMeasure();
    uom1.setId("1");

    final UnitOfMeasure uom2 = new UnitOfMeasure();
    uom2.setId("2");

    when(unitOfMeasureRepository.findAll()).thenReturn(Flux.just(uom1, uom2));

    //when
    final List<UnitOfMeasureCommand> commands = service.listAllUoms().collectList().block();

    //then
    assertEquals(2, commands.size());
    verify(unitOfMeasureRepository, times(1)).findAll();
  }
}
