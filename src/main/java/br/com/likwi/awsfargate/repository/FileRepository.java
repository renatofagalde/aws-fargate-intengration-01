package br.com.likwi.awsfargate.repository;

import br.com.likwi.awsfargate.model.File;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends CrudRepository<File, Long> {

    Optional<File> findByFileNumber(String fileNumber);
    List<File> findAllByCustomer(String customer);
}
