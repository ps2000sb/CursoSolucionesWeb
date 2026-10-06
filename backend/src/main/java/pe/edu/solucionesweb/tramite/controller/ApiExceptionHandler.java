package pe.edu.solucionesweb.tramite.controller;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler({NoSuchElementException.class,IllegalArgumentException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> manejar(RuntimeException e){return Map.of("mensaje",e.getMessage());}
 @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
 public ResponseEntity<Map<String,String>> manejarEstado(org.springframework.web.server.ResponseStatusException e){return ResponseEntity.status(e.getStatusCode()).body(Map.of("mensaje",e.getReason()==null?"No se pudo completar la operación.":e.getReason()));}
}
