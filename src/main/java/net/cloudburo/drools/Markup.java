package net.cloudburo.drools;

import lombok.Builder;
import lombok.Data;

@Data
    @Builder
    public class Markup {
        MarkupType type;
        String value;
    }
