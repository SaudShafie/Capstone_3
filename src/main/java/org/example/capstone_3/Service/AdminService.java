package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.AdminDTOIn;
import org.example.capstone_3.DTO.OUT.AdminDTOOut;
import org.example.capstone_3.Model.Admin;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Repository.AdminRepository;
import org.example.capstone_3.Repository.MentorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final MentorRepository mentorRepository;


    public void create(AdminDTOIn dto) {

        if (adminRepository.findAdminByEmail(dto.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }

        Admin admin = new Admin();

        applyDto(admin, dto);
        admin.setCreatedAt(LocalDateTime.now());

        adminRepository.save(admin);
    }

    public AdminDTOOut getById(Integer id) {

        Admin admin = adminRepository.findAdminById(id);

        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }

        return toDtoOut(admin);
    }

    public List<AdminDTOOut> getAll() {

        List<Admin> admins = adminRepository.findAll();

        List<AdminDTOOut> adminDTOOuts = new ArrayList<>();

        for (Admin admin : admins) {
            adminDTOOuts.add(toDtoOut(admin));
        }

        return adminDTOOuts;
    }

    public void update(Integer id, AdminDTOIn dto) {

        Admin admin = adminRepository.findAdminById(id);

        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }

        Admin emailOwner = adminRepository.findAdminByEmail(dto.getEmail());

        if (emailOwner != null && !emailOwner.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }

        applyDto(admin, dto);

        adminRepository.save(admin);
    }

    public void delete(Integer id) {

        Admin admin = adminRepository.findAdminById(id);

        if (admin == null) {
            throw new ApiException("Admin with id " + id + " not found");
        }

        adminRepository.delete(admin);
    }

    public void approveMentor(Integer adminId, Integer mentorId) {

        Admin admin = adminRepository.findAdminById(adminId);

        if (admin == null) {
            throw new ApiException("Admin with id " + adminId + " not found");
        }

        Mentor mentor = mentorRepository.findMentorById(mentorId);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + mentorId + " not found");
        }

        mentor.setAcceptedByAdmin(true);

        mentorRepository.save(mentor);
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
