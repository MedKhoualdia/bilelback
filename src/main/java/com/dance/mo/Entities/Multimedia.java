package com.dance.mo.Entities;



import com.dance.mo.Entities.Enumarations.FileType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Multimedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_multimedia;

    private String videoUrl;
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(cascade = CascadeType.ALL)
    private Competition competition;
}
