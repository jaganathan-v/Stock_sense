package com.stocksense.controller;

import com.stocksense.model.Location;
import com.stocksense.dto.LocationRequestDto;
import com.stocksense.exception.ConflictException;
import com.stocksense.exception.ResourceNotFoundException;
import com.stocksense.repository.LocationRepository;
import com.stocksense.repository.StockMoveRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationRepository locationRepository;
    private final StockMoveRepository stockMoveRepository;

    public LocationController(LocationRepository locationRepository, StockMoveRepository stockMoveRepository) {
        this.locationRepository = locationRepository;
        this.stockMoveRepository = stockMoveRepository;
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationRepository.findAllByOrderByNameAsc();
        return ResponseEntity.ok(locations);
    }

    @org.springframework.web.bind.annotation.PostMapping
    public ResponseEntity<Location> createLocation(@Valid @org.springframework.web.bind.annotation.RequestBody LocationRequestDto dto) {
        String name = dto.getName().trim();
        if (locationRepository.existsByNameIgnoreCase(name)) throw new ConflictException("A location named '" + name + "' already exists.");
        return ResponseEntity.status(HttpStatus.CREATED).body(locationRepository.save(new Location(name)));
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}")
    public ResponseEntity<Location> renameLocation(@org.springframework.web.bind.annotation.PathVariable Long id,
                                                    @Valid @org.springframework.web.bind.annotation.RequestBody LocationRequestDto dto) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        String name = dto.getName().trim();
        if (locationRepository.findByNameIgnoreCase(name).filter(existing -> !existing.getId().equals(id)).isPresent()) {
            throw new ConflictException("A location named '" + name + "' already exists.");
        }
        location.setName(name);
        return ResponseEntity.ok(locationRepository.save(location));
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Location location = locationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        if (stockMoveRepository.existsByLocationId(id)) throw new ConflictException("Cannot delete '" + location.getName() + "' because stock moves reference it. Rename it instead.");
        locationRepository.delete(location);
        return ResponseEntity.noContent().build();
    }
}
