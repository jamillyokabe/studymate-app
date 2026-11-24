package br.com.studymate.service;
import br.com.studymate.dto.*;
import br.com.studymate.model.Usuario;
import br.com.studymate.repository.UsuarioRepository;
import br.com.studymate.exception.EmailEmUsoException;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service @Validated
public class UsuarioCadastroService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    public UsuarioCadastroService(UsuarioRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }
 // Internal persistence entry point. HTTP registration uses email ownership proof first.
 @Transactional
 public UsuarioResponse criar(@NotNull @Valid RegisterRequest request){
  String email=request.getEmail().trim().toLowerCase(Locale.ROOT);
  if(repository.existsByEmailIgnoreCase(email))throw new EmailEmUsoException();
  if(request.getSenha().getBytes(StandardCharsets.UTF_8).length>72)
   throw new IllegalArgumentException("A senha deve ocupar no máximo 72 bytes em UTF-8.");
  Usuario user=repository.saveAndFlush(new Usuario(request.getNome().trim(),email,encoder.encode(request.getSenha())));
  return new UsuarioResponse(user);
 }
}
