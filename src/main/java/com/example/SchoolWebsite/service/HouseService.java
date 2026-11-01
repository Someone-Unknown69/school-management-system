package com.example.SchoolWebsite.service;

import com.example.SchoolWebsite.model.House;
import com.example.SchoolWebsite.model.Teacher;
import com.example.SchoolWebsite.repository.HouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HouseService {

    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {
        this.houseRepository = houseRepository;
    }

    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    public House getHouse(String color) {
        if (color == null || color.trim().isEmpty()) return null;
        return houseRepository.findByColor(color.trim());
    }

    public List<Teacher> getAvailableMentors(String color) {
        if (color == null || color.trim().isEmpty()) return List.of();
        return houseRepository.getAvailableMentors(color.trim());
    }

    @Transactional
    public void updateTournamentsWon(String color, int wins) {
        if (color == null || color.trim().isEmpty()) {
            throw new IllegalArgumentException("House color identifier is required.");
        }
        if (wins < 0) {
            throw new IllegalArgumentException("Tournaments won cannot be negative.");
        }
        houseRepository.updateTournamentsWon(color.trim(), wins);
    }

    @Transactional
    public void addMentor(String color, String teacherId) {
        if (color == null || teacherId == null || color.trim().isEmpty() || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("House color and Teacher ID are required.");
        }
        houseRepository.addMentor(color.trim(), teacherId.trim());
    }

    @Transactional
    public void removeMentor(String color, String teacherId) {
        if (color == null || teacherId == null || color.trim().isEmpty() || teacherId.trim().isEmpty()) {
            throw new IllegalArgumentException("House color and Teacher ID are required.");
        }
        houseRepository.removeMentor(color.trim(), teacherId.trim());
    }
}

