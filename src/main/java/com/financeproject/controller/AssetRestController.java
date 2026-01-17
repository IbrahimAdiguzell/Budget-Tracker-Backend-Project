package com.financeproject.controller;

import com.financeproject.entity.Asset;
import com.financeproject.repository.IAssetRepository;
import com.financeproject.repository.IUserRepository;
import com.financeproject.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/assets")
public class AssetRestController {

    private final IAssetRepository assetRepository;
    private final IUserRepository userRepository;

    public AssetRestController(IAssetRepository assetRepository, IUserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    // 1. ADD ASSET
    @PostMapping
    public ResponseEntity<?> addAsset(@RequestBody Asset asset, Principal principal) {
        try {
            User user = userRepository.findByEmail(principal.getName()).orElseThrow();
            asset.setUser(user);
            assetRepository.save(asset);
            return ResponseEntity.ok("Asset added successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // 2. DELETE ASSET
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id) {
        try {
            assetRepository.deleteById(id);
            return ResponseEntity.ok("Asset deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Deletion failed.");
        }
    }
}