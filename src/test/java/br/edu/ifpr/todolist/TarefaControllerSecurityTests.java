package br.edu.ifpr.todolist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.edu.ifpr.todolist.model.Tarefa;
import br.edu.ifpr.todolist.repository.TarefaRepository;

@SpringBootTest
class TarefaControllerSecurityTests {

    private static final String JSON_TAREFA = "{\"titulo\":\"Estudar Spring Security\"}";

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private TarefaRepository repository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ---------- Sem autenticação: nada é permitido ----------

    @Test
    void semAutenticacaoNaoListaTarefas() throws Exception {
        mockMvc.perform(get("/tarefas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacaoNaoBuscaTarefaPorId() throws Exception {
        mockMvc.perform(get("/tarefas/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacaoNaoCadastraTarefa() throws Exception {
        mockMvc.perform(post("/tarefas").contentType("application/json").content(JSON_TAREFA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void semAutenticacaoNaoExcluiTarefa() throws Exception {
        mockMvc.perform(delete("/tarefas/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void senhaInvalidaEhRejeitada() throws Exception {
        mockMvc.perform(get("/tarefas").with(httpBasic("usuario", "senha-errada")))
                .andExpect(status().isUnauthorized());
    }

    // ---------- USER ----------

    @Test
    void userListaTarefas() throws Exception {
        mockMvc.perform(get("/tarefas").with(httpBasic("usuario", "usuario123")))
                .andExpect(status().isOk());
    }

    @Test
    void userBuscaTarefaPorId() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Tarefa()));

        mockMvc.perform(get("/tarefas/1").with(httpBasic("usuario", "usuario123")))
                .andExpect(status().isOk());
    }

    @Test
    void userCadastraTarefa() throws Exception {
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/tarefas")
                        .with(httpBasic("usuario", "usuario123"))
                        .contentType("application/json")
                        .content(JSON_TAREFA))
                .andExpect(status().isOk());
    }

    @Test
    void userNaoExcluiTarefa() throws Exception {
        mockMvc.perform(delete("/tarefas/1").with(httpBasic("usuario", "usuario123")))
                .andExpect(status().isForbidden());
    }

    // ---------- ADMIN ----------

    @Test
    void adminListaTarefas() throws Exception {
        mockMvc.perform(get("/tarefas").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void adminBuscaTarefaPorId() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Tarefa()));

        mockMvc.perform(get("/tarefas/1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void adminCadastraTarefa() throws Exception {
        when(repository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/tarefas")
                        .with(httpBasic("admin", "admin123"))
                        .contentType("application/json")
                        .content(JSON_TAREFA))
                .andExpect(status().isOk());
    }

    @Test
    void adminExcluiTarefa() throws Exception {
        when(repository.findById(1L)).thenReturn(Optional.of(new Tarefa()));

        mockMvc.perform(delete("/tarefas/1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());
    }
}
