package ais.io.workgym.services;

import ais.io.workgym.dto.user.UserRequestDTO;
import ais.io.workgym.dto.user.UserResponseDTO;
import ais.io.workgym.entities.User;
import ais.io.workgym.repositories.UserRepository;
import ais.io.workgym.services.exceptions.DatabaseException;
import ais.io.workgym.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService  {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserResponseDTO update(UUID id, UserRequestDTO userRequestDTO) {
        try {
            User userEntity = userRepository.getReferenceById(id);
            copyDtoToEntity(userRequestDTO, userEntity);
            userEntity = userRepository.save(userEntity);
            return new UserResponseDTO(userEntity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
    }

    @Transactional
    public List<UserResponseDTO> findAll() {
        List<User> userEntityList = userRepository.findAll();
        return userEntityList.stream().map(UserResponseDTO::new).toList();
    }

    @Transactional
    public UserResponseDTO findById(UUID id) {
        User userEntity = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuário não encontrado")
        );
        return new UserResponseDTO(userEntity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Erro de integridade referencial");
        }
    }

    @Transactional
    public void changePassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encryptedPassword);

        userRepository.save(user);
    }

    private void copyDtoToEntity(UserRequestDTO userRequestDTO, User userEntity) {
        userEntity.setLogin(userRequestDTO.getLogin());
        userEntity.setName(userRequestDTO.getName());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setRole(userRequestDTO.getRole());
        userEntity.setCpf(userRequestDTO.getCpf());
    }
}
