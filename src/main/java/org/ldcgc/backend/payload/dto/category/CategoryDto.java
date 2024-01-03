package org.ldcgc.backend.payload.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Value
@Builder
@JsonInclude(NON_NULL)
public class CategoryDto implements Serializable {

    Integer id;
    String name;
    Boolean locked;
    @JsonInclude(NON_NULL)
    Category parent;
    @JsonInclude(NON_EMPTY)
    @Singular("category")
    List<CategoryDto> categories;

    public CategoryDto findCategorySonByName(String sonName){
        return categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase(sonName))
                .findFirst()
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND,
                        Messages.Error.CATEGORY_SON_NOT_FOUND
                                .formatted(name, sonName, name, categories.stream().map(CategoryDto::getName).toList().toString())));
    }
}
