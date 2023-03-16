package com.openvelog.openvelogbe.dummy;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Lazy
public class RandomLocalDateGenerator {
    public LocalDate generateRandomLocalDateFromTo(LocalDate from, LocalDate to) {
        long totalDays = Period.between(from, to).getDays();
        Random random = new Random();
        long randomDays = random.nextInt((int) totalDays);
        return from.plusDays(randomDays);
    }
}
