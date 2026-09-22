package pe.edu.solucionesweb.tramite.dto; import jakarta.validation.constraints.NotNull; public record DerivacionRequest(@NotNull Long areaDestinoId, String responsable, String observacion) {}
