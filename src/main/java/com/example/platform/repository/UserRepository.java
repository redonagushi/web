//Layer i DB (Spring Data JPA).
//
//Metoda tipike:
//
//findByEmail(...)
//
//existsByEmail(...)
//
//search query (si ajo që më tregove me :q)
//
//findAll(Pageable pageable) për DataTables
package com.example.platform.repository;

import com.example.platform.entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ...

    @Query("""
        SELECT u FROM User u
        WHERE (:q IS NULL OR :q = '' OR
               lower(u.emri) LIKE lower(concat('%', :q, '%')) OR
               lower(u.mbiemri) LIKE lower(concat('%', :q, '%')) OR
               lower(u.email) LIKE lower(concat('%', :q, '%')) OR
               u.nrTel LIKE concat('%', :q, '%'))
    """)
    Page<User> search(@Param("q") String q, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByNrTel(String nrTel);
    Optional<User> findByEmail(String email);

    Optional<User> findByNrTel(String nrTel);
}
