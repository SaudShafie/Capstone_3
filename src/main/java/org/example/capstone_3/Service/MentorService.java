package org.example.capstone_3.Service;

import org.example.capstone_3.DTO.IN.MentorDTOIn;
import org.example.capstone_3.DTO.OUT.MentorDTOOut;
import org.example.capstone_3.Model.Mentor;
import org.example.capstone_3.Repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;

    public void addMentor(MentorDTOIn mentorDTOIn) {

        Mentor mentor = new Mentor();

        mentor.setFullName(mentorDTOIn.getFullName());
        mentor.setEmail(mentorDTOIn.getEmail());
        mentor.setPassword(mentorDTOIn.getPassword());
        mentor.setJobTitle(mentorDTOIn.getJobTitle());
        mentor.setCompany(mentorDTOIn.getCompany());
        mentor.setSpecialization(mentorDTOIn.getSpecialization());
        mentor.setYearsExperience(mentorDTOIn.getYearsExperience());
        mentor.setBio(mentorDTOIn.getBio());
        mentor.setVolunteer(mentorDTOIn.getVolunteer());
        mentor.setSessionPrice(mentorDTOIn.getSessionPrice());
        mentor.setAvailable(true);

        mentor.setRating(0.0);
        mentor.setCreatedAt(LocalDateTime.now());

        mentorRepository.save(mentor);
    }

    public List<MentorDTOOut> getAllMentors() {

        List<Mentor> mentors = mentorRepository.findAll();

        List<MentorDTOOut> mentorDTOOuts = new ArrayList<>();

        for (Mentor mentor : mentors) {
            mentorDTOOuts.add(mapToMentorDTOOut(mentor));
        }

        return mentorDTOOuts;
    }

    public MentorDTOOut getMentorById(Integer mentorId) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        return mapToMentorDTOOut(mentor);
    }

    public void updateMentor(Integer mentorId, MentorDTOIn mentorDTOIn) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        mentor.setFullName(mentorDTOIn.getFullName());
        mentor.setEmail(mentorDTOIn.getEmail());
        mentor.setPassword(mentorDTOIn.getPassword());
        mentor.setJobTitle(mentorDTOIn.getJobTitle());
        mentor.setCompany(mentorDTOIn.getCompany());
        mentor.setSpecialization(mentorDTOIn.getSpecialization());
        mentor.setYearsExperience(mentorDTOIn.getYearsExperience());
        mentor.setBio(mentorDTOIn.getBio());
        mentor.setVolunteer(mentorDTOIn.getVolunteer());
        mentor.setSessionPrice(mentorDTOIn.getSessionPrice());
        mentor.setAvailable(true);

        mentorRepository.save(mentor);
    }

    public void deleteMentor(Integer mentorId) {

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));

        mentorRepository.delete(mentor);
    }

    private MentorDTOOut mapToMentorDTOOut(Mentor mentor) {

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