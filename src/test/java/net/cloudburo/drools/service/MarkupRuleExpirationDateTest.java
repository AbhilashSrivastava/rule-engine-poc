package net.cloudburo.drools.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import net.cloudburo.drools.config.DroolsBeanFactory;
import net.cloudburo.drools.model2.Journey;
import net.cloudburo.drools.model2.Location;
import net.cloudburo.drools.model2.Markup;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class MarkupRuleExpirationDateTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft-3.xlsx";

    private KieSession kSession;

    @Before
    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);

    }

    @Test
    public void shouldNotRunExpiredRule() throws Exception {

        /**
         * expiration column takes a specific format dd-MMM-yyyy and in
         * excel the cell containing the date must either have text or custom date format.
         *
         * FYI, date-effective column is also available: https://docs.jboss.org/drools/release/7.31.0.Final/drools-docs/html_single/index.html#rules-attributes-ref_drl-rules
         */

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .deptDateTime(LocalDateTime.of(2019, Month.DECEMBER, 24, 06, 30))
            .arrLocation(Location.builder()
                .countryCode("DE")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals("15", markup.getValue() );
    }

}


