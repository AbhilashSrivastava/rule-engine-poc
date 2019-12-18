package net.cloudburo.drools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Journey {
    Location deptLocation;
    Location arrLocation;
}
