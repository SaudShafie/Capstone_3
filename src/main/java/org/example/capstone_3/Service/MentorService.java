package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MentorDTOIn;
import org.example.capstone_3.DTO.OUT.MentorDTOOut;
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
public class MentorService {

    private final MentorRepository mentorRepository;
    private final AdminRepository adminRepository;

    public void create(MentorDTOIn dto) {

        if (mentorRepository.findMentorByEmail(dto.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }

        Mentor mentor = new Mentor();

        applyDto(mentor, dto);

        mentor.setRating(0.0);
        mentor.setAcceptedByAdmin(false);
        mentor.setCreatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(dto.getVolunteer())) {
            mentor.setSessionPrice(0.0);
        }

        mentorRepository.save(mentor);
    }

    public MentorDTOOut getById(Integer id) {

        Mentor mentor = mentorRepository.findMentorById(id);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }

        return toDtoOut(mentor);
    }

    public List<MentorDTOOut> getAll() {

        List<Mentor> mentors = mentorRepository.findAll();

        List<MentorDTOOut> mentorDTOOuts = new ArrayList<>();

        for (Mentor mentor : mentors) {
            mentorDTOOuts.add(toDtoOut(mentor));
        }

        return mentorDTOOuts;
    }

    public List<MentorDTOOut> getAcceptedMentors() {

        List<Mentor> mentors = mentorRepository.findMentorsByAcceptedByAdmin(true);

        List<MentorDTOOut> mentorDTOOuts = new ArrayList<>();

        for (Mentor mentor : mentors) {
            mentorDTOOuts.add(toDtoOut(mentor));
        }

        return mentorDTOOuts;
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

    public void update(Integer id, MentorDTOIn dto) {

        Mentor mentor = mentorRepository.findMentorById(id);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }

        Mentor emailOwner = mentorRepository.findMentorByEmail(dto.getEmail());

        if (emailOwner != null && !emailOwner.getId().equals(id)) {
            throw new ApiException("Email already exists");
        }

        applyDto(mentor, dto);

        if (Boolean.TRUE.equals(dto.getVolunteer())) {
            mentor.setSessionPrice(0.0);
        }

        mentorRepository.save(mentor);
    }

    public void delete(Integer id) {

        Mentor mentor = mentorRepository.findMentorById(id);

        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }

        mentorRepository.delete(mentor);
    }

    private void applyDto(Mentor mentor, MentorDTOIn dto) {
        mentor.setFullName(dto.getFullName());
        mentor.setEmail(dto.getEmail());
        mentor.setPassword(dto.getPassword());
        mentor.setJobTitle(dto.getJobTitle());
        mentor.setCompany(dto.getCompany());
        mentor.setSpecialization(dto.getSpecialization());
        mentor.setYearsExperience(dto.getYearsExperience());
        mentor.setBio(dto.getBio());
        mentor.setSessionPrice(dto.getSessionPrice());
    }

    private MentorDTOOut toDtoOut(Mentor mentor) {
        Boolean volunteer = mentor.getSessionPrice() != null && mentor.getSessionPrice() == 0.0;
        return new MentorDTOOut(
                mentor.getId(),
                mentor.getFullName(),
                mentor.getEmail(),
                mentor.getJobTitle(),
                mentor.getCompany(),
                mentor.getSpecialization(),
                mentor.getYearsExperience(),
                mentor.getBio(),
                volunteer,
                mentor.getSessionPrice(),
                mentor.getRating(),
                mentor.getAcceptedByAdmin()
        );
    }
}