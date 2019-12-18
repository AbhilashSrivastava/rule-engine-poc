package net.cloudburo.drools.model2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Journey {
    Location deptLocation;
    Location arrLocation;
}
