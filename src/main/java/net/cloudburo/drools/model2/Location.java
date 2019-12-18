package net.cloudburo.drools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    String cityCode;
    String countryCode;
}
