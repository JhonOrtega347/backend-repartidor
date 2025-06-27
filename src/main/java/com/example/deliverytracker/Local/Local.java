package com.example.deliverytracker.Local;

import com.example.deliverytracker.Usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "locales")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sede;
    private String direccion;

    private String telefono;
    private String horario;
}