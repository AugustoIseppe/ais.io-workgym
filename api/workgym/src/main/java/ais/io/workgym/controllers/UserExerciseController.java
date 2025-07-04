package ais.io.workgym.controllers;

import ais.io.workgym.dto.userExercise.UserExerciseListRequestDTO;
import ais.io.workgym.dto.userExercise.UserExerciseRequestDTO;
import ais.io.workgym.dto.userExercise.UserExerciseResponseDTO;
import ais.io.workgym.projections.UserExerciseProjection;
import ais.io.workgym.projections.UserExerciseProjectionDTO;
import ais.io.workgym.services.UserExerciseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-exercises")
public class UserExerciseController {

    @Autowired
    private UserExerciseService userExerciseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserExerciseResponseDTO> insert(@RequestBody @Valid UserExerciseRequestDTO dto) {
        UserExerciseResponseDTO response = userExerciseService.insert(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserExerciseResponseDTO> updateUserExercise(@PathVariable UUID id,
                                                                      @RequestBody @Valid UserExerciseRequestDTO userExerciseRequestDTO) {
        // Chama o service para atualizar o UserExercise
        UserExerciseResponseDTO updatedUserExercise = userExerciseService.update(id, userExerciseRequestDTO);

        // Retorna a resposta com o status 200 OK
        return ResponseEntity.ok(updatedUserExercise);
    }

    @GetMapping("/{userId}/day/{weekDay}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<UserExerciseProjectionDTO>> getByUserAndDay(
            @PathVariable UUID userId,
            @PathVariable String weekDay) {

        List<UserExerciseProjectionDTO> list = userExerciseService.getExercisesByUserAndWeekDay(userId, weekDay);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<UserExerciseProjectionDTO>> getAll() {
        List<UserExerciseProjectionDTO> list = userExerciseService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{userId}/weekdays")
    @PreAuthorize("hasAnyRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<String>> getWeekDaysByUser(@PathVariable UUID userId) {
        List<String> orderedWeekDays = userExerciseService.getOrderedWeekDaysByUser(userId);
        return ResponseEntity.ok(orderedWeekDays);
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserExerciseResponseDTO>> insertBatch(@RequestBody @Valid UserExerciseListRequestDTO dto) {
        List<UserExerciseResponseDTO> responses = userExerciseService.insertBatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserExerciseProjection>> getUserExercises(@PathVariable UUID userId) {
        List<UserExerciseProjection> exercises = userExerciseService.getUserExercises(userId);
        return ResponseEntity.ok(exercises);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userExerciseService.deleteUserExercise(id);
        return ResponseEntity.noContent().build();
    }
}
