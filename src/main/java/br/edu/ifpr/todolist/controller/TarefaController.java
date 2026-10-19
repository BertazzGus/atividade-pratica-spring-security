package br.edu.ifpr.todolist.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpr.todolist.model.Tarefa;
import br.edu.ifpr.todolist.repository.TarefaRepository;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping("/tarefas")
public class TarefaController {
    private TarefaRepository repository;

    public TarefaController(TarefaRepository repository) {
        this.repository = repository;
    }


    @PostMapping
    public Tarefa cadastrar(@RequestBody Tarefa tarefa){
        if(tarefa.getTitulo()== null || tarefa.getTitulo().isBlank()) {
            throw new RuntimeException("Título obrigatório");
        }

        tarefa.setStatus("PENDENTE");
        return repository.save(tarefa);
    }

    @GetMapping 
    public List<Tarefa> listar(){
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Tarefa buscarPorId(@PathVariable Long id) {
        return buscarTarefa(id);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        Tarefa tarefa = buscarTarefa(id);
        repository.delete(tarefa);
        return ResponseEntity.noContent().build();
    }  


    private Tarefa buscarTarefa(Long id){
        return repository.findById(id).orElseThrow(()-> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Tarefa não encontrada")
        );
    }



    


}
