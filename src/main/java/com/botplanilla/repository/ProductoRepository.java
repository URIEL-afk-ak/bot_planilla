package com.botplanilla.repository;

import com.botplanilla.model.Producto;
import com.botplanilla.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    List<Producto> findByTipo(TipoProducto tipo);
    
    List<Producto> findByTipoAndNombreContainingIgnoreCase(TipoProducto tipo, String nombre);
}

