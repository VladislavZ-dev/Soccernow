package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@MappedSuperclass
@Table(name = "soccerNowUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false)
	private String name;

	public User() {}

	public User(String name) {
        this.name = name;
    }

	public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
