package guru.springframework.domain;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by jt on 6/13/17.
 */
@Getter
@Setter
@Document
public class Category {
  @Id
  private String id;
  private String description;
  private Set<Recipe> recipes;
}
