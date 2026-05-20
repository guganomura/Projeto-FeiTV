package model;

// Essa interface define o "contrato" de situação de um vídeo.
// Qualquer classe que implementar Situacao é obrigada a ter esses dois métodos.
// No projeto, só Serie implementa essa interface.
public interface Situacao {

    // Retorna o nome da situação (ex: "Em Andamento", "Finalizada", "Cancelada")
    String getNome();

    // Retorna uma descrição mais detalhada (ex: "3 temporada(s) · 30 episódio(s)")
    String getDescricao();
}
