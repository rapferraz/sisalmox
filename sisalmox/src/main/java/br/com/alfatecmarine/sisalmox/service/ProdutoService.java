package br.com.alfatecmarine.sisalmox.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.alfatecmarine.sisalmox.Exception.NomeDuplicadoException;
import br.com.alfatecmarine.sisalmox.Exception.ResourceNotFoundException;
import br.com.alfatecmarine.sisalmox.model.Produto;
import br.com.alfatecmarine.sisalmox.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    
    private final ProdutoRepository repository;

    public Produto salvarProduto(Produto produto) {

        // Verifica se o nome do produto está nulo ou vazio e a quantidade não está negativa.
        if (produto.getQuantidade() < 0 && produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro de integridade de dados: Nome do produto nulo ou vazio e quantidade em estoque negativa");
        } else if (produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("Erro de integridade de dados: A quantidade em estoque não pode ser negativa!");
        } else if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro de integridade de dados: O nome do produto não pode ser nulo ou vazio!");
        }

        Optional<Produto> possivelDuplicata = repository.findByNomeIgnoreCase(produto.getNome());

        if(possivelDuplicata.isPresent()) {
            throw new NomeDuplicadoException("Erro de integridade de dados: já existe um produto com o nome '" + produto.getNome() + "'.");
        } else {
            return repository.save(produto);
        }
    }

    public List<Produto> buscarTodos() {
        return repository.findAll();
    }

    public List<Produto> buscaProdutoPorNome(String nome) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na integridade de dados: O nome do produto não pode ser nulo ou vazio!");
        }

        List<Produto> listona = repository.findAll();
        List<Produto> listaPolida = new ArrayList<>();

        for (Produto produto : listona) {
            if (produto.getNome().contains(nome)) {
                listaPolida.add(produto);
            }
        }

        return listaPolida;
    }

    public Produto alteraNomeProduto(String nome, Produto novosDados) {

        if(novosDados.getNome() == null || novosDados.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na integridade de dados: O nome do produto não pode ser nulo ou vazio!");
        }

         Produto produtoExistente = repository.findByNomeIgnoreCase(nome)
            .orElseThrow(() -> new ResourceNotFoundException("Erro na integridade de dados: O produto '" + nome + "' não foi encontrado!"));

        if(!produtoExistente.getNome().equalsIgnoreCase(novosDados.getNome().trim())) {
            if (repository.findByNomeIgnoreCase(novosDados.getNome().trim()).isPresent()) {
                throw new NomeDuplicadoException("Erro na integridade de dados: Já existe um produto com o nome: " + novosDados.getNome());
            }
        }

        produtoExistente.setNome(novosDados.getNome().trim().toLowerCase());

        return repository.save(produtoExistente);
    }

    public void excluirProduto(String nome) {

        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException("Erro de integridade de dados: O nome do produto não pode ser nulo ou vazio!");
        }

        if (repository.findByNomeIgnoreCase(nome).isEmpty()) {
            throw new ResourceNotFoundException("Erro na integridade de dados: O produto '" + nome + "' não foi encontrado!");
        } else {
            repository.deleteByNome(nome);
        }
    }
}