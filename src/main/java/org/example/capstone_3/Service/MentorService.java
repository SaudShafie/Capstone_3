package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MentorDTOIn;
import org.example.capstone_3.DTO.OUT.MentorDTOOut;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Repository.MentorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    public void create(MentorDTOIn dto) {

        if (mentorRepository.findMentorByEmail(dto.getEmail()) != null) {
            throw new ApiException("Email already exists");
        }

        Mentor mentor = new Mentor();

        applyDto(mentor, dto);

        mentor.setRating(0.0);
        mentor.setAvailable(true);
        mentor.setCreatedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(mentor.getVolunteer())) {
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

        if (Boolean.TRUE.equals(mentor.getVolunteer())) {
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
        mentor.setVolunteer(dto.getVolunteer());
        mentor.setSessionPrice(dto.getSessionPrice());
    }

    private MentorDTOOut toDtoOut(Mentor mentor) {
        return new MentorDTOOut(
                mentor.getId(),
                mentor.getFullName(),
                mentor.getEmail(),
                mentor.getJobTitle(),
                mentor.getCompany(),
                mentor.getSpecialization(),
                mentor.getYearsExperience(),
                mentor.getBio(),
                mentor.getVolunteer(),
                mentor.getSessionPrice(),
                mentor.getRating(),
                mentor.getAvailable()
        );
    }
}