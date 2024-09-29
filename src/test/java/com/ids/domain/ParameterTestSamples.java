package com.ids.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParameterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Parameter getParameterSample1() {
        return new Parameter()
            .id(1L)
            .label("label1")
            .lib2("lib21")
            .lib3("lib31")
            .refExterne("refExterne1")
            .val1("val11")
            .val2("val21")
            .val3("val31")
            .ordre(1);
    }

    public static Parameter getParameterSample2() {
        return new Parameter()
            .id(2L)
            .label("label2")
            .lib2("lib22")
            .lib3("lib32")
            .refExterne("refExterne2")
            .val1("val12")
            .val2("val22")
            .val3("val32")
            .ordre(2);
    }

    public static Parameter getParameterRandomSampleGenerator() {
        return new Parameter()
            .id(longCount.incrementAndGet())
            .label(UUID.randomUUID().toString())
            .lib2(UUID.randomUUID().toString())
            .lib3(UUID.randomUUID().toString())
            .refExterne(UUID.randomUUID().toString())
            .val1(UUID.randomUUID().toString())
            .val2(UUID.randomUUID().toString())
            .val3(UUID.randomUUID().toString())
            .ordre(intCount.incrementAndGet());
    }
}
