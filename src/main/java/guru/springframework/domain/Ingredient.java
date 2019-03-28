package guru.springframework.domain;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by jt on 6/13/17.
 */
@Getter
@Setter
public class Ingredient {

  private String id = UUID.randomUUID().toString();
  private String description;
  private BigDecimal amount;
  private UnitOfMeasure uom;

  public Ingredient() {

  }

  public Ingredient(final String description, final BigDecimal amount, final UnitOfMeasure uom) {
    this.description = description;
    this.amount = amount;
    this.uom = uom;
  }

  public Ingredient(final String description, final BigDecimal amount, final UnitOfMeasure uom,
      final Recipe recipe) {
    this.description = description;
    this.amount = amount;
    this.uom = uom;
  }
}
