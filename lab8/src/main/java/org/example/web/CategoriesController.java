package org.example.web;

import org.example.models.VehicleCategoryConfig;
import org.example.services.VehicleServiceInterface;
import org.example.services.impl.VehicleCategoryConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoriesController {

    public final VehicleCategoryConfigService vehicleCategoryConfig;

    public CategoriesController(VehicleCategoryConfigService vehicleCategoryConfig) {
        this.vehicleCategoryConfig = vehicleCategoryConfig;
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        VehicleCategoryConfig config = vehicleCategoryConfig.getByCategory(category);
        if(config == null) {
            throw new IllegalArgumentException("Nieznana kategoria pojazdu: " + category);
        }
        return config;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return vehicleCategoryConfig.findAllCategories();
    }
}
