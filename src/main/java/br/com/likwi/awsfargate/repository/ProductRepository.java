package br.com.likwi.awsfargate.repository;

import br.com.likwi.awsfargate.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByCode(String code);
}
