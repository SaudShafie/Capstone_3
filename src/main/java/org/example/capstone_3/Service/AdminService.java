package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.AdminDTOIn;
import org.example.capstone_3.DTO.OUT.AdminDTOOut;
import org.example.capstone_3.Model.Admin;
import org.example.capstone_3.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public void addAdmin(AdminDTOIn adminDTOIn) {

        Admin admin = new Admin();

        admin.setFullName(adminDTOIn.getFullName());
        admin.setEmail(adminDTOIn.getEmail());
        admin.setPassword(adminDTOIn.getPassword());
        admin.setCreatedAt(LocalDateTime.now());

        adminRepository.save(admin);
    }

    public List<AdminDTOOut> getAllAdmins() {

        List<Admin> admins = adminRepository.findAll();

        List<AdminDTOOut> adminDTOOuts = new ArrayList<>();

        for (Admin admin : admins) {
            adminDTOOuts.add(mapToAdminDTOOut(admin));
        }

        return adminDTOOuts;
    }

    public AdminDTOOut getAdminById(Integer adminId) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return mapToAdminDTOOut(admin);
    }

    public void updateAdmin(Integer adminId, AdminDTOIn adminDTOIn) {

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setFullName(adminDTOIn.getFullName());
        admin.setEmail(adminDTOIn.getEmail());
        admin.setPassword(adminDTOIn.getPassword());

        adminRepository.save(admin);
    }

    public void deleteAdmin(Integer adminId) {

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));

        adminRepository.delete(admin);
    }

    private AdminDTOOut mapToAdminDTOOut(Admin admin) {

        return new AdminDTOOut(
                admin.getId(),
                admin.getFullName(),
                admin.getEmail()
        );
    }
}