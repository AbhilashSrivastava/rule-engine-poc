package net.cloudburo.drools.service;

import static org.junit.Assert.assertEquals;

import net.cloudburo.drools.model2.Journey;
import net.cloudburo.drools.model2.Location;
import net.cloudburo.drools.model2.Markup;
import net.cloudburo.drools.config.DroolsBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class MarkupDecisionTableTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft.xlsx";
    public static final String INPUT_FILE = "net/cloudburo/drools/rules/DroolsDiscount.xlsx";

    private KieSession kSession;

    @Before
    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);

    }

    @Test
    public void shouldMarkupWhenDepartureLocationMatchesRule() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .arrLocation(Location.builder()
                .countryCode("DE")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals("10", markup.getValue() );
    }

    @Test
    public void shouldMarkupWhenArrivalLocationMatchesRule() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("DE")
                .build())
            .arrLocation(Location.builder()
                .countryCode("FR")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals("20",markup.getValue());
    }

    @Test
    public void shouldNotMarkupWhenLocationsDontMatcheRule() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("DE")
                .build())
            .arrLocation(Location.builder()
                .countryCode("ES")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals(null, markup.getValue());
    }

    /**
     * 1. anja edits a csv/excel file with rules
     * 2. uploads it to a repo as a PR
     * 3. PR checkers validate rules
     * 4. once merged, it's on master as latest version
     * 4.1. write controller to serve the csv file
     * 5. embedded RE gets latest version (poll)
     * 6. feeds RE with latest rules
     * 7. plug this in pigeon
     */

}


