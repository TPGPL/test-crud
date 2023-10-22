package pl.edu.pw.mwotest.models;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Orders")
@NotNull(message = "The order must not be null.")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int id;
    @NotNull(message = "The order client must not be null.")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;
    @NotNull(message = "The order status must not be null.")
    @Enumerated(value = EnumType.ORDINAL)
    private OrderStatus status;
    @Valid
    @NotNull(message = "The order lines must not be null.")
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@Valid OrderLine> lines = new ArrayList<>();
}
