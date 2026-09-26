package com.stocksense.config;

import com.stocksense.model.Location;
import com.stocksense.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer runs on startup and seeds the default Locations
 * ("Main Warehouse" and "Production Floor") if they do not already exist.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final LocationRepository locationRepository;

    public DataInitializer(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(String... args) {
        seedLocationIfMissing("Main Warehouse");
        seedLocationIfMissing("Production Floor");
    }

    private void seedLocationIfMissing(String name) {
        if (!locationRepository.existsByNameIgnoreCase(name)) {
            Location loc = locationRepository.save(new Location(name));
            log.info("Seeded location: '{}' (id={})", name, loc.getId());
        }
    }
}
