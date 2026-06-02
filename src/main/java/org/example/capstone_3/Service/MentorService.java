package org.example.capstone_3.Service;

import lombok.RequiredArgsConstructor;
import org.example.capstone_3.Api.ApiException;
import org.example.capstone_3.DTO.IN.MentorDTOIn;
import org.example.capstone_3.DTO.OUT.MentorDTOOut;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Repository.MentorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    public MentorDTOOut create(MentorDTOIn dto) {
        Mentor mentor = new Mentor();
        applyDto(mentor, dto);
        mentor.setRating(0.0);
        mentor.setAvailable(true);
        mentor.setCreatedAt(LocalDateTime.now());
        return toDtoOut(mentorRepository.save(mentor));
    }

    public MentorDTOOut getById(Integer id) {
        Mentor mentor = mentorRepository.findMentorById(id);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }
        return toDtoOut(mentor);
    }

    public List<MentorDTOOut> getAll() {
        return mentorRepository.findAll().stream().map(this::toDtoOut).toList();
    }

    public MentorDTOOut update(Integer id, MentorDTOIn dto) {
        Mentor mentor = mentorRepository.findMentorById(id);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }
        applyDto(mentor, dto);
        return toDtoOut(mentorRepository.save(mentor));
    }

    public void delete(Integer id) {
        Mentor mentor = mentorRepository.findMentorById(id);
        if (mentor == null) {
            throw new ApiException("Mentor with id " + id + " not found");
        }
        mentorRepository.deleteById(id);
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
                mentor.getAvailable(),
                mentor.getCreatedAt()
        );
    }
}
