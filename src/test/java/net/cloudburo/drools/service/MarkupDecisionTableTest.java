package net.cloudburo.drools.service;

import static org.junit.Assert.assertEquals;

import net.cloudburo.drools.Journey;
import net.cloudburo.drools.Location;
import net.cloudburo.drools.Markup;
import net.cloudburo.drools.config.DroolsBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class MarkupDecisionTableTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft.xlsx";
    private KieSession kSession;

    @Before
    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);

    }

    @Test
    public void giveIndvidualLongStanding_whenFireRule_thenCorrectDiscount() throws Exception {

        Journey journey = Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .arrLocation(Location.builder()
                .countryCode("FR")
                .build())
            .build();
        kSession.insert(journey);

        Markup markup = Markup.builder().build();
        kSession.setGlobal("markup", markup);
        kSession.fireAllRules();

        assertEquals(markup.getValue(), "10");
//        assertEquals(markup.getType(), MarkupType.PERCENTAGE);
    }


}


