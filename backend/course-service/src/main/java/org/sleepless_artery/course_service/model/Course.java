package org.sleepless_artery.course_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "course")
@NoArgsConstructor
@Getter @Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name = "description")
    private String description;


    @PrePersist
    public void onCreate() {
        this.creationDate = this.lastUpdateDate = LocalDate.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastUpdateDate = LocalDate.now();
    }
}
