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

public class MarkupDifferentConditionsSameFileTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft-2.xlsx";

    private KieSession kSession;

    @Before
    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);

    }

    @Test
    public void shouldMarkupWhenDepartureLocationAndDeptDateMatchesRule() throws Exception {

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

    @Test
    public void shouldNotMarkupWhenDepartureLocationAndDeptDateMatchesRule() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .deptDateTime(LocalDateTime.of(2019, Month.DECEMBER, 25, 06, 30))
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
    public void test() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .deptDateTime(LocalDateTime.of(2019, Month.DECEMBER, 26, 06, 30))
            .arrLocation(Location.builder()
                .countryCode("DE")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals("35", markup.getValue() );
    }

}


