package net.cloudburo.drools.service;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isOneOf;
import static org.hamcrest.number.OrderingComparison.comparesEqualTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import net.cloudburo.drools.config.DroolsBeanFactory;
import net.cloudburo.drools.model2.Journey;
import net.cloudburo.drools.model2.Location;
import net.cloudburo.drools.model2.Markup;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class MultithreadingSessionMultipleInputsTest {

    public static final String EXCEL_FILE = "net/cloudburo/drools/rules/markup-draft.xlsx";
    public static final int THREAD_QUANTITY = 1000;
    private RuleService ruleService;

    @Before
    public void setup() {

        if(ruleService != null) return; // make sure it's a singleton
        Resource markupResource = ResourceFactory.newClassPathResource(EXCEL_FILE, getClass());
        ruleService = new RuleService(new DroolsBeanFactory().getKieContainer(markupResource));
    }

    @Test
    public void testThreadSafe() throws ExecutionException, InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean running = new AtomicBoolean();
        AtomicInteger overlaps = new AtomicInteger();

        int threads = THREAD_QUANTITY;
        ExecutorService service = Executors.newFixedThreadPool(threads);
        Collection<Future<Markup>> futures = new ArrayList<>(threads);

        List<Journey> journeys = Arrays.asList(
            markup10Journey(),
            markup20Journey(),
            noMarkupJourney()
        );

        for ( int t = 0; t < threads; ++t) {
            futures.add((Future<Markup>) service.submit(() -> {

                latch.await(); //make the threads wait

                if (running.get()) {
                    overlaps.incrementAndGet();
                }
                running.set(true);
                Markup markup = ruleService.getMarkup(journeys.get(ThreadLocalRandom.current().nextInt(journeys.size()) % journeys.size()));
                running.set(false);
                return markup;
            }));
        }

        Collections.shuffle((List<?>) futures);

        latch.countDown(); //release the threads

        int tens = 0;
        int twenties = 0;
        int nulls = 0;
        for (Future<Markup> f : futures) {
             assertThat("markup should be any of 10, 20, null", f.get().getValue(), isOneOf("10", "20", null) );
             if(f.get().getValue().equals("10")) tens++;
             if(f.get().getValue().equals("20")) twenties++;
             if(f.get().getValue() == null) nulls++;
         }

        assertThat(tens, greaterThan(200));
        assertThat(twenties, greaterThan(200));
        assertThat(nulls, greaterThan(200));

        assertThat("Thread overlapping should be 999 (max)",overlaps.get(), comparesEqualTo(999));

    }


    public Journey markup10Journey() {
        return Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("GB")
                .build())
            .arrLocation(Location.builder()
                .countryCode("DE")
                .build())
            .build();
    }

    public Journey markup20Journey() {
        return Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("DE")
                .build())
            .arrLocation(Location.builder()
                .countryCode("FR")
                .build())
            .build();
    }

    public Journey noMarkupJourney() {
        return Journey.builder()
            .deptLocation(Location.builder()
                .countryCode("DE")
                .build())
            .arrLocation(Location.builder()
                .countryCode("ES")
                .build())
            .build();
    }

    @AllArgsConstructor
    class RuleService {

        private KieContainer KieContainer;

        public Markup getMarkup(Journey journey) {
            KieSession session = KieContainer.newKieSession();
            session.insert(journey);
            Markup markup = Markup.builder().build();
            session.setGlobal("markup", markup);
            session.fireAllRules();
            session.dispose();
            return markup;
        }
    }
}


