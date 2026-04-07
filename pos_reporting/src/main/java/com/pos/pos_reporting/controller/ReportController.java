package com.pos.pos_reporting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/")
public class ReportController {

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> mockDashboard = new HashMap<>();
        mockDashboard.put("dailySales", 5430.20);
        mockDashboard.put("totalOrders", 48);
        mockDashboard.put("lowStockItems", 12);
        mockDashboard.put("status", "Reporting service is up and running!");
        return mockDashboard;
    }
}
