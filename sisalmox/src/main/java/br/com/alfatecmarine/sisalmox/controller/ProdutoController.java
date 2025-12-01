package br.com.alfatecmarine.sisalmox.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alfatecmarine.sisalmox.model.Produto;
import br.com.alfatecmarine.sisalmox.service.ProdutoService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {
    
    private final ProdutoService service;
    
    @PostMapping
    public Produto criarProduto(@RequestBody Produto produto) {
        return service.salvarProduto(produto);
    }

    @GetMapping
    public List<Produto> listarProdutos() {
        return service.buscarTodos();
    }

    @GetMapping("/listanomes/{nome}")
    public List<Produto> listarProdutosPorNome(@PathVariable String nome) {
        return service.buscaProdutoPorNome(nome.toLowerCase());
    }

    @PutMapping("/alteranome/{nome}")
    public Produto alteraNomeProduto(@PathVariable String nome, @RequestBody Produto novosDados) {
        return service.alteraNomeProduto(nome.toLowerCase(), novosDados);
    }

    @PutMapping("/aumentaqtd/{nome}")
    public Produto aumentaProduto(@PathVariable String nome, @RequestBody Produto qtdAumenta) {
        return service.aumentaProduto(nome, qtdAumenta);
    }

    @DeleteMapping("/excluinome/{nome}")
    public ResponseEntity<String> excluirProduto(@PathVariable String nome) {
        service.excluirProduto(nome);
        return new ResponseEntity<>("Produto excluído com sucesso!", HttpStatus.OK);
    }

}
