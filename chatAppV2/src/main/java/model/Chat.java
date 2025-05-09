package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;


    @Column(name = "created_time")
    @Getter
    @Setter
    private LocalDateTime createdTime;

    @Column(name= "name")
    @Getter
    @Setter
    private String name;

    @Column(name = "is_active")
    @Getter
    @Setter
    private boolean isActive;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<Subscription> subscriptions;

    @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private ChatLog chatLog;

    // Getters and Setters
}
