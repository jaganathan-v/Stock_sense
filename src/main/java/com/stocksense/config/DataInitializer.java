package com.stocksense.config;

import com.stocksense.model.Location;
import com.stocksense.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer runs on startup and seeds the default Location ("Main Warehouse")
 * if it does not already exist.
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
        if (locationRepository.count() == 0) {
            Location mainWarehouse = new Location("Main Warehouse");
            locationRepository.save(mainWarehouse);
            log.info("Seeded default location: 'Main Warehouse' (id={})", mainWarehouse.getId());
        }
    }
}
