package br.com.desafio.domain.entities;

import lombok.Data;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="Seller")
@Data
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;

}
