package net.cloudburo.drools.model2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Markup {
    MarkupType type;
    String value;
}
