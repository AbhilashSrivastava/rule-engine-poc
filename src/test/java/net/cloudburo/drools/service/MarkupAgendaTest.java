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
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class MarkupAgendaTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft-4.xlsx";

    private KieSession kSession;
    private KieServices ks;

    @Before
    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);
        ks = KieServices.Factory.get();
    }

    @Test
    public void shouldNotExecuteRulesWithoutAgenda() throws Exception {

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

        Markup markup = Markup.builder().value("12").build();
        kSession.setGlobal("markup", markup);

        kSession.fireAllRules();
        System.out.println("the{}"+kSession.getGlobals());

        assertEquals(null, markup.getValue() );
    }

    @Test
    public void shouldExecuteMarkupGroupFirst() throws Exception {

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

        kSession.getAgenda().getAgendaGroup("markup").setFocus();

        kSession.fireAllRules();

        assertEquals("5", markup.getValue() );
    }

    @Test
    public void shouldExecuteAdditionalMarkupGroupFirst() throws Exception {

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

        kSession.getAgenda().getAgendaGroup("additional-markup").setFocus();

        kSession.fireAllRules();

        assertEquals("1", markup.getValue() );
    }

}


