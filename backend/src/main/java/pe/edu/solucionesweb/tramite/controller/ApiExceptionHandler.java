package pe.edu.solucionesweb.tramite.controller;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler { @ExceptionHandler({NoSuchElementException.class,IllegalArgumentException.class}) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> manejar(RuntimeException e){return Map.of("mensaje",e.getMessage());} }
