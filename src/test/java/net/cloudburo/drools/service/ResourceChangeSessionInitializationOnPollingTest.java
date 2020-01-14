package net.cloudburo.drools.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.timeout;
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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RunWith(MockitoJUnitRunner.class)
public class ResourceChangeSessionInitializationOnPollingTest {

    public static final String[] files = {
            "net/cloudburo/drools/rules/markup-draft.xlsx",
            "net/cloudburo/drools/rules/markup-draft-v2.xlsx",
            "net/cloudburo/drools/rules/markup-draft-2.xlsx",
            "net/cloudburo/drools/rules/markup-draft-3.xlsx"
    };
    private KieSession kSession;

    // File Count for the endpoint of polling
    private int count = -1;

    // To Store the array of kSessions created at different point of time
    private ArrayList<Integer> resultsArray = new ArrayList<Integer>();

    @Mock
    ResourceService resourceService;

    // Container Setup should be synchronized
    private synchronized void setupContainer(String resFile) {
        System.out.println("Setting up new container as per resource: " + resFile);
        Resource resource = ResourceFactory.newClassPathResource(resFile, getClass());
        kSession = new DroolsBeanFactory().getKieSession(resource);
    }

    // File Read should be synchronized
    private synchronized String getNewFile() throws InterruptedException {
        System.out.println("Waiting to fetch file..........");
        wait(2000 + count);
        System.out.println("Returning file number:" + count );
        count++;
        return files[count];
    }

    public synchronized int getSession() throws Exception {
        System.out.println("Creating New Session.......");
        return kSession.hashCode();
    }

    @Test
    public void testIfSessionsAreDifferent() throws Exception {
        int threads = 4;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Collection<Future<Integer>> futures = new ArrayList<>(threads);
        CountDownLatch latch = new CountDownLatch(1);

        for ( int t = 0; t < threads; ++t) {
            futures.add(service.submit(() -> {
                System.out.println("Polling........");
                String file = getNewFile();
                latch.await();
                setupContainer(file);
                return getSession();
            }));
        }

        // Wait for all to complete
        latch.countDown();

        for (Future<Integer> f : futures) {
            resultsArray.add(f.get());
        }
        System.out.println("Sessions are:" + resultsArray);
        assertNotEquals(resultsArray.get(0), resultsArray.get(1));
        assertNotEquals(resultsArray.get(1), resultsArray.get(2));
        assertNotEquals(resultsArray.get(2), resultsArray.get(3));
    }
}


