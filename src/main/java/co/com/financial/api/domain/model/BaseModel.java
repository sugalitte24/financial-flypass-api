package co.com.financial.api.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
