package org.example.capstone_3.Controller;

import org.example.capstone_3.DTO.IN.AdminDTOIn;
import org.example.capstone_3.Service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/admins")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<?> addAdmin(@RequestBody @Valid AdminDTOIn adminDTOIn) {
        adminService.addAdmin(adminDTOIn);
        return ResponseEntity.status(201).body("Admin added successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllAdmins() {
        return ResponseEntity.status(200).body(adminService.getAllAdmins());
    }

    @GetMapping("/{adminId}")
    public ResponseEntity<?> getAdminById(@PathVariable Integer adminId) {
        return ResponseEntity.status(200).body(adminService.getAdminById(adminId));
    }

    @PutMapping("/{adminId}")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer adminId,
                                         @RequestBody @Valid AdminDTOIn adminDTOIn) {
        adminService.updateAdmin(adminId, adminDTOIn);
        return ResponseEntity.status(200).body("Admin updated successfully");
    }

    @DeleteMapping("/{adminId}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Integer adminId) {
        adminService.deleteAdmin(adminId);
        return ResponseEntity.status(200).body("Admin deleted successfully");
    }
}