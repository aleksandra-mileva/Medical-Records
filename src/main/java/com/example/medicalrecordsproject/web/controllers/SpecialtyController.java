package com.example.medicalrecordsproject.web.controllers;

import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyRequest;
import com.example.medicalrecordsproject.data.dtos.specialties.SpecialtyResponse;
import com.example.medicalrecordsproject.service.SpecialtyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpecialtyResponse createSpecialty(@RequestBody @Valid SpecialtyRequest specialtyRequest) {
        return specialtyService.createSpecialty(specialtyRequest);
    }

    @GetMapping
    public List<SpecialtyResponse> retrieveAll() {
        return specialtyService.getAllSpecialties();
    }

    @GetMapping("/{id}")
    public SpecialtyResponse retrieveSingle(@PathVariable Long id) {
        return specialtyService.getSpecialtyById(id);
    }

    @PutMapping("/{id}")
    public SpecialtyResponse updateSpecialty(@PathVariable Long id, @RequestBody @Valid SpecialtyRequest specialtyRequest) {
        return specialtyService.updateSpecialty(id, specialtyRequest);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
    }
}
