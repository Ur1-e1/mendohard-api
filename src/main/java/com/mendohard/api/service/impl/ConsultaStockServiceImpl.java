package com.mendohard.api.service.impl;

import com.mendohard.api.dto.*;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.*;
import com.mendohard.api.repository.*;
import com.mendohard.api.service.ConsultaStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaStockServiceImpl implements ConsultaStockService {

    private final ComercioRepository comercioRepository;
    private final ProductoRepository productoRepository;
    private final ConsumidorRepository consumidorRepository;
    private final ConsultaStockRepository consultaStockRepository;
    private final EstadoConsultaStockRepository estadoConsultaStockRepository;
    private final NivelStockRepository nivelStockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComercioResumenDTO> obtenerComerciosActivosParaUI45() {
        return comercioRepository.findByCFechaBajaIsNullAndCFechaAltaIsNotNull().stream()
                .map(this::mapComercioToResumenDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MapaStockComercioResponseDTO> consultarStockSucursalEspecifica(ConsultaSucursalEspecificaRequestDTO dto, String usuarioEmail) {
        Producto producto = getProductoOrThrow(dto.getPCodigo());
        Comercio comercio = getComercioOrThrow(dto.getCCodigo());
        Consumidor consumidor = getConsumidorOrThrow(usuarioEmail);

        crearConsultaStockSiCorresponde(comercio, producto, consumidor, null, null);

        return obtenerMapaStockDisponibilidad(dto.getPCodigo());
    }

    @Override
    @Transactional
    public List<MapaStockComercioResponseDTO> consultarStockCincoCercanos(ConsultaCincoCercanosRequestDTO dto, String usuarioEmail) {
        Producto producto = getProductoOrThrow(dto.getPCodigo());
        Consumidor consumidor = getConsumidorOrThrow(usuarioEmail);

        List<Comercio> comerciosActivos = comercioRepository.findByCFechaBajaIsNullAndCFechaAltaIsNotNull().stream()
                .filter(c -> c.getCLatitud() != null && c.getCLongitud() != null)
                .collect(Collectors.toList());

        List<Comercio> cincoCercanos = comerciosActivos.stream()
                .sorted(Comparator.comparingDouble(c -> calcularDistanciaHaversine(
                        dto.getCSLatitudDemanda(), dto.getCSLongitudDemanda(),
                        c.getCLatitud(), c.getCLongitud())))
                .limit(5)
                .collect(Collectors.toList());

        for (Comercio comercio : cincoCercanos) {
            crearConsultaStockSiCorresponde(comercio, producto, consumidor, dto.getCSLatitudDemanda(), dto.getCSLongitudDemanda());
        }

        return obtenerMapaStockDisponibilidad(dto.getPCodigo());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MapaStockComercioResponseDTO> obtenerMapaStockDisponibilidad(String pCodigo) {
        Producto producto = getProductoOrThrow(pCodigo);
        List<Comercio> comerciosActivos = comercioRepository.findByCFechaBajaIsNullAndCFechaAltaIsNotNull();

        return comerciosActivos.stream().map(comercio -> {
            ComercioResumenDTO comercioDTO = mapComercioToResumenDTO(comercio);
            Optional<ConsultaStock> ultimaConsultaOpt = consultaStockRepository
                    .findFirstByComercioAndProductoOrderByCSFechaHoraSolicitudDesc(comercio, producto);

            UltimaConsultaComercioDTO ultimaConsultaDTO = null;

            if (ultimaConsultaOpt.isPresent()) {
                ConsultaStock ultimaConsulta = ultimaConsultaOpt.get();
                ultimaConsultaDTO = mapConsultaToUltimaConsultaDTO(ultimaConsulta);
                
                if ("StockDisponible".equals(ultimaConsulta.getEstadoConsultaStock().getECSNombre())) {
                    Integer cantidadRespuesta = ultimaConsulta.getCSCantidadRespuesta();
                    if (cantidadRespuesta != null && cantidadRespuesta > 0) {
                        Categoria categoria = producto.getCategoria();
                        Optional<NivelStock> nivelStockOpt = nivelStockRepository
                                .findNivelStockActivoPorCategoriaYCantidad(categoria, cantidadRespuesta);
                        if (nivelStockOpt.isPresent()) {
                            ultimaConsultaDTO.setNSCodigo(nivelStockOpt.get().getNSCodigo());
                            ultimaConsultaDTO.setNSNombre(nivelStockOpt.get().getNSNombre());
                        }
                    }
                }
            }

            return MapaStockComercioResponseDTO.builder()
                    .comercio(comercioDTO)
                    .ultimaConsulta(ultimaConsultaDTO)
                    .build();
        }).collect(Collectors.toList());
    }

    private void crearConsultaStockSiCorresponde(Comercio comercio, Producto producto, Consumidor consumidor, Float latDemanda, Float lonDemanda) {
        List<ConsultaStock> consultasPrevias = consultaStockRepository.findByComercioAndProducto(comercio, producto);
        
        boolean existeConsultaActiva = consultasPrevias.stream()
                .anyMatch(c -> !"ConsultaExpirada".equals(c.getEstadoConsultaStock().getECSNombre()));

        if (!existeConsultaActiva) {
            EstadoConsultaStock estadoPendiente = estadoConsultaStockRepository
                    .findByECSNombreAndECSFechaBajaIsNull("StockPendiente")
                    .orElseThrow(() -> new ResourceNotFoundException("Estado Consulta Stock 'StockPendiente' no encontrado"));

            ConsultaStock nuevaConsulta = ConsultaStock.builder()
                    .CSFechaHoraSolicitud(LocalDateTime.now())
                    .CSFechaHoraExpiracion(LocalDateTime.now().plusDays(5))
                    .CSLatitudDemanda(latDemanda)
                    .CSLongitudDemanda(lonDemanda)
                    .estadoConsultaStock(estadoPendiente)
                    .consumidor(consumidor)
                    .producto(producto)
                    .comercio(comercio)
                    // Atributo contador será asignado igual al ID luego de guardar o se puede usar un trigger.
                    // Para Spring Data, usualmente seteamos un valor temporal y luego actualizamos o dejamos que el DB maneje
                    .CSContador(0L) 
                    .build();

            ConsultaStock saved = consultaStockRepository.save(nuevaConsulta);
            saved.setCSContador(saved.getId());
            consultaStockRepository.save(saved);
        }
    }

    private Producto getProductoOrThrow(String pCodigo) {
        return productoRepository.findByPCodigoAndPFechaBajaIsNull(pCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el código: " + pCodigo));
    }

    private Comercio getComercioOrThrow(String cCodigo) {
        return comercioRepository.findByCodigoComercio(cCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado con el código: " + cCodigo));
    }

    private Consumidor getConsumidorOrThrow(String email) {
        return consumidorRepository.findByEmailActivo(email)
                .orElseThrow(() -> new ResourceNotFoundException("Consumidor no encontrado o inactivo"));
    }

    private ComercioResumenDTO mapComercioToResumenDTO(Comercio comercio) {
        return ComercioResumenDTO.builder()
                .CCodigo(comercio.getCCodigo())
                .CNombreFantasia(comercio.getCNombreFantasia())
                .CTelefono(comercio.getCTelefono())
                .CDireccionCalle(comercio.getCDireccionCalle())
                .CNumeroEnCalle(comercio.getCNumeroEnCalle())
                .CHorarioAtencion(comercio.getCHorarioAtencion())
                .CLatitud(comercio.getCLatitud())
                .CLongitud(comercio.getCLongitud())
                .build();
    }

    private UltimaConsultaComercioDTO mapConsultaToUltimaConsultaDTO(ConsultaStock consulta) {
        return UltimaConsultaComercioDTO.builder()
                .CSFechaHoraSolicitud(consulta.getCSFechaHoraSolicitud())
                .CSFechaHoraRespuesta(consulta.getCSFechaHoraRespuesta())
                .CSMarcasRespuesta(consulta.getCSMarcasRespuesta())
                .CSDescripcionRespuesta(consulta.getCSDescripcionRespuesta())
                .CSPrecioRespuesta(consulta.getCSPrecioRespuesta())
                .ECSNombre(consulta.getEstadoConsultaStock().getECSNombre())
                .build();
    }

    // Fórmula Haversine
    private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
