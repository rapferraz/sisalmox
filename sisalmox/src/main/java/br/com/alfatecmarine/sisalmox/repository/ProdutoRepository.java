package br.com.alfatecmarine.sisalmox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.alfatecmarine.sisalmox.model.Produto;
import jakarta.transaction.Transactional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    Optional<Produto> findByNomeIgnoreCase(String nome);

    List<Produto> findAllByNomeIgnoreCase(String nome);

    @Transactional
    void deleteByNome(String nome);
}
