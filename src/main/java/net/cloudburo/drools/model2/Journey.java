package net.cloudburo.drools.model2;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Journey {
    Location deptLocation;
    LocalDateTime deptDateTime;

    Location arrLocation;
    LocalDateTime arrDateTime;
}
