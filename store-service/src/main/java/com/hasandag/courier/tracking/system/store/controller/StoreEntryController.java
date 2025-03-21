package com.hasandag.courier.tracking.system.store.controller;

import com.hasandag.courier.tracking.system.store.model.StoreEntry;
import com.hasandag.courier.tracking.system.store.service.StoreEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store-entries")
@RequiredArgsConstructor
public class StoreEntryController {

    private final StoreEntryService storeEntryService;

    @GetMapping("/couriers/{courierId}")
    public ResponseEntity<List<StoreEntry>> getEntriesForCourier(@PathVariable String courierId) {
        List<StoreEntry> entries = storeEntryService.getEntriesForCourier(courierId);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/couriers/{courierId}/paginated")
    public ResponseEntity<Page<StoreEntry>> getEntriesForCourierPaginated(
            @PathVariable String courierId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<StoreEntry> entries = storeEntryService.getEntriesForCourierPaginated(courierId, pageable);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<Page<StoreEntry>> getEntriesForStore(
            @PathVariable Long storeId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<StoreEntry> entries = storeEntryService.getEntriesForStore(storeId, pageable);
        return ResponseEntity.ok(entries);
    }
}
