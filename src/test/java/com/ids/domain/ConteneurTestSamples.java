package com.ids.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConteneurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Conteneur getConteneurSample1() {
        return new Conteneur().id(1L).statut(1).zone(1).ligne(1).colonne(1).commentaire("commentaire1");
    }

    public static Conteneur getConteneurSample2() {
        return new Conteneur().id(2L).statut(2).zone(2).ligne(2).colonne(2).commentaire("commentaire2");
    }

    public static Conteneur getConteneurRandomSampleGenerator() {
        return new Conteneur()
            .id(longCount.incrementAndGet())
            .statut(intCount.incrementAndGet())
            .zone(intCount.incrementAndGet())
            .ligne(intCount.incrementAndGet())
            .colonne(intCount.incrementAndGet())
            .commentaire(UUID.randomUUID().toString());
    }
}
