package br.edu.ifpr.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpr.todolist.model.Tarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>{

}
