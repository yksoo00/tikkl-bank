package com.tikkl.bank.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class SimulationDateService {

    private LocalDate simulationDate = LocalDate.now(); // 최초는 오늘 날짜

    public LocalDate getCurrentDate() {
        return simulationDate;
    }

    public LocalDate plusOneDay() {
        simulationDate = simulationDate.plusDays(1);
        return simulationDate;
    }
}