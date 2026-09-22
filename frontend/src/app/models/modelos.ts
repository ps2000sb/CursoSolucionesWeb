export interface Area { id?: number; nombre: string; descripcion?: string; }
export interface Documento { id?: number; codigo: string; tipoDocumento: string; remitente: string; dniRuc: string; asunto: string; fechaRecepcion: string; descripcion?: string; estado: string; areaDestino?: Area; }
export interface Tramite { id?: number; codigo: string; tipoTramite: string; solicitante: string; dniRuc: string; asunto: string; fechaRegistro: string; descripcion?: string; estado: string; areaResponsable?: Area; }
export interface Historial { id: number; estado: string; fecha: string; responsable: string; observacion: string; area?: Area; }
export interface Resumen { totalDocumentos: number; documentosPendientes: number; tramitesEnProceso: number; tramitesConcluidos: number; porEstado: Record<string,number>; }
