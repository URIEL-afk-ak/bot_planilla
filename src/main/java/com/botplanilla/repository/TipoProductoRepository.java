package com.botplanilla.repository;

import com.botplanilla.model.TipoProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoProductoRepository extends JpaRepository<TipoProductoEntity, Long> {
    Optional<TipoProductoEntity> findByNombre(String nombre);
    
    List<TipoProductoEntity> findAllByOrderByNombreAsc();
}



