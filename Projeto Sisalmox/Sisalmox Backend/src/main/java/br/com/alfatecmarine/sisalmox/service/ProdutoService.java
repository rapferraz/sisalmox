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
        } else if (produto.getCategoria() == null || produto.getCategoria().isEmpty()) {
            throw new IllegalArgumentException("Erro de integridade de dados: A categoria do produto não pode ser nula ou vazia!");
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

    public Produto alteraNomeProduto(String nome, Produto novoNome) {

        if(novoNome.getNome() == null || novoNome.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro na integridade de dados: O nome do produto não pode ser nulo ou vazio!");
        }

         Produto produtoExistente = repository.findByNomeIgnoreCase(nome)
            .orElseThrow(() -> new ResourceNotFoundException("Erro na integridade de dados: O produto '" + nome + "' não foi encontrado!"));

        if(!produtoExistente.getNome().equalsIgnoreCase(novoNome.getNome().trim())) {
            if (repository.findByNomeIgnoreCase(novoNome.getNome().trim()).isPresent()) {
                throw new NomeDuplicadoException("Erro na integridade de dados: Já existe um produto com o nome: " + novoNome.getNome());
            }
        }

        produtoExistente.setNome(novoNome.getNome().trim().toLowerCase());
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

    public Produto baixaProduto (String nome, Produto qtdBaixa) {

        if (qtdBaixa == null || qtdBaixa.getQuantidade() < 0) {
            throw new IllegalArgumentException("Erro na integridade de dados: A quantidade não pode ser nula ou negativa!");
        }

        Produto produtoExistente = repository.findByNomeIgnoreCase(nome)
            .orElseThrow(() -> new ResourceNotFoundException("Erro na integridade de dados: O produto '" + nome + "' não foi encontrado!"));
        
        produtoExistente.setQuantidade(produtoExistente.getQuantidade() - qtdBaixa.getQuantidade());

        return repository.save(produtoExistente);
    }

    public Produto aumentaProduto(String nome, Produto qtdAumenta) {


        if (qtdAumenta == null || qtdAumenta.getQuantidade() < 0) {
            throw new IllegalArgumentException("Erro na integridade de dados: A quantidade não pode ser nula ou negativa!");
        }

        Produto produtoExistente = repository.findByNomeIgnoreCase(nome)
            .orElseThrow(() -> new ResourceNotFoundException("Erro na integridade de dados: O produto '" + nome + "' não foi encontrado!"));
        
        produtoExistente.setQuantidade(produtoExistente.getQuantidade() + qtdAumenta.getQuantidade());

        return repository.save(produtoExistente);
    }
}