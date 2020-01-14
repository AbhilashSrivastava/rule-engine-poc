package net.cloudburo.drools.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

import net.cloudburo.drools.model2.Journey;
import net.cloudburo.drools.model2.Location;
import net.cloudburo.drools.model2.Markup;
import net.cloudburo.drools.config.DroolsBeanFactory;
import net.cloudburo.drools.model2.ResourceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(MockitoJUnitRunner.class)
public class ResourceChangeSessionReinitializationTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft.xlsx";
    public static final String EXCEL_FILE_VERSION2 = "net/cloudburo/drools/rules/markup-draft-v2.xlsx";

    private KieSession kSession;


    @Mock
    ResourceService resourceService;


    public void setup() {
        System.out.println(new DroolsBeanFactory().getDrlFromExcel(EXCEL_FILE));
        Resource resource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);

    }

    @Test
    public void testNewSessionIsCreatedWhenNewExcelFileIsProvided() throws Exception {
        when(resourceService.getResource()).thenReturn(EXCEL_FILE).thenReturn(EXCEL_FILE_VERSION2);
        String v1 = resourceService.getResource();
        System.out.println("Resource: " +  new DroolsBeanFactory().getDrlFromExcel(v1));
        Resource resource1 = ResourceFactory.newClassPathResource(v1, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource1);
        int id1 = kSession.hashCode();

        String v2 = resourceService.getResource();
        System.out.println("Resource: " +  new DroolsBeanFactory().getDrlFromExcel(v2));
        Resource resource2 = ResourceFactory.newClassPathResource(v2, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource2);
        int id2 = kSession.hashCode();


        assertNotEquals(id1, id2);
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


