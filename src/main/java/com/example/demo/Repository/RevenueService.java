package com.example.demo.Repository;

import java.time.LocalDate;
import java.util.Map;

public interface RevenueService {
    Map<LocalDate, Double> getDailyRevenueForMonth(int year, int month);
}
