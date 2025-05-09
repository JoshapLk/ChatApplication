package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_logs")
public class ChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;

    @OneToOne
    @JoinColumn(name = "chat_id")
    @Getter
    @Setter
    private Chat chat;

    @Column(name = "file_path")
    @Getter
    @Setter
    private String filePath;

}
