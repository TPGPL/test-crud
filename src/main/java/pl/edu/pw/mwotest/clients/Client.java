package pl.edu.pw.mwotest.clients;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
@Table(name = "Clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Setter
    @Column(nullable = false)
    @NotNull(message = "Name must not be null.")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
    private String name;
    @Setter
    @Column(nullable = false)
    @NotNull(message = "Surname must not be null.")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters.")
    private String surname;
    @Setter
    @Column(nullable = false)
    @NotNull(message = "Email must not be null.")
    @Size(min = 2, max = 50, message = "Email must be between 2 and 50 characters.")
    @Email(message = "Email must be in a valid format.")
    private String email;

    protected Client() {
    }

    public Client(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
