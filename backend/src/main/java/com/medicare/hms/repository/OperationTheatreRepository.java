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
}
