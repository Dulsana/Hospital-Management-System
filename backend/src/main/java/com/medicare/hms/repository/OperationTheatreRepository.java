package com.medicare.hms.repository;

import com.medicare.hms.model.OperationTheatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Tell Spring: "This is a Database Manager component"
@Repository
public interface OperationTheatreRepository extends JpaRepository<OperationTheatre, String> {
// 2. Extends JpaRepository<Entity, PrimaryKeyType>
    //    - Entity: OperationTheatre
    //    - ID Type: String

// 3. You automatically get free database methods out of the box:
    //    - findAll()    -> SELECT * FROM operation_theatres
    //    - findById(id) -> SELECT * FROM operation_theatres WHERE id = ?
    //    - save(ot)     -> INSERT / UPDATE record
    //    - deleteById(id) -> DELETE FROM operation_theatres WHERE id = ?
}
