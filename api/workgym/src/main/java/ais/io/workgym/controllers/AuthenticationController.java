package ais.io.workgym.controllers;

import ais.io.workgym.dto.AuthenticationDTO;
import ais.io.workgym.dto.LoginResponseDTO;
import ais.io.workgym.dto.RegisterDTO;
import ais.io.workgym.dto.user.ChangePasswordDTO;
import ais.io.workgym.dto.user.UserDTO;
import ais.io.workgym.dto.user.UserRegisterResponseDTO;
import ais.io.workgym.entities.User;
import ais.io.workgym.infra.security.TokenService;
import ais.io.workgym.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // @PostMapping("/register")
    // public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
    //
    // if (userRepository.findByLogin(data.login()) != null) {
    // return ResponseEntity.status(400).body("User already exists");
    // }
    //
    // String encryptedPassword = new
    // BCryptPasswordEncoder().encode(data.password());
    // User user = new User(
    // data.name(),
    // data.email(),
    // encryptedPassword,
    // data.cpf(),
    // data.login(),
    // data.role());
    // userRepository.save(user);
    // return ResponseEntity
    // .status(201) // 201 CREATED é mais apropriado para criação de recursos
    // .body(user);
    // }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data) {
        if (userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.status(400).body(Map.of("error", "User already exists"));
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User user = new User(
                data.name(),
                data.email(),
                encryptedPassword,
                data.cpf(),
                data.login(),
                data.role());

        userRepository.save(user);

        // Retorne o usuário criado como JSON (sem senha)
        return ResponseEntity.status(201).body(new UserRegisterResponseDTO(user));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String login = tokenService.validateToken(token);

        if (login.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByLogin(login);
        return ResponseEntity.ok(new UserDTO(user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO data) {
        User user = userRepository.findByLogin(data.login());
        if (user == null) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        if (!new BCryptPasswordEncoder().matches(data.oldPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("Senha atual incorreta");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(data.newPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Senha atualizada com sucesso");
    }

}
