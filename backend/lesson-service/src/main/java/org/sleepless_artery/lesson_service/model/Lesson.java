package org.sleepless_artery.lesson_service.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "lesson")
@Getter @Setter
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "sequence_number", nullable = false)
    private  Long sequenceNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "content")
    private String content;
}