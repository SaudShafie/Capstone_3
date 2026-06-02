package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.AdminDTOIn;
import org.example.capstone_3.DTO.OUT.AdminDTOOut;
import org.example.capstone_3.Model.Admin;
import org.example.capstone_3.Repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminDTOOut create(AdminDTOIn dto) {
        Admin admin = new Admin();
        applyDto(admin, dto);
        admin.setCreatedAt(LocalDateTime.now());
        return toDtoOut(adminRepository.save(admin));
    }

    public AdminDTOOut getById(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }
        return toDtoOut(admin);
    }

    public List<AdminDTOOut> getAll() {
        return adminRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public AdminDTOOut update(Integer id, AdminDTOIn dto) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }
        applyDto(admin, dto);
        return toDtoOut(adminRepository.save(admin));
    }

    public void delete(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }
        adminRepository.deleteById(id);
    }

    private void applyDto(Admin admin, AdminDTOIn dto) {
        admin.setFullName(dto.getFullName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
    }

    private AdminDTOOut toDtoOut(Admin admin) {
        return new AdminDTOOut(
                admin.getId(),
                admin.getFullName(),
                admin.getEmail()
        );
    }
}
