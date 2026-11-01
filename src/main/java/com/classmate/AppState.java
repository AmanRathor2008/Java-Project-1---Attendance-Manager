package com.classmate;

import jakarta.persistence.*;
import java.time.Instant;

/** One row holding the whole ClassMate data set (students, columns, ticks, attendance, history) as JSON. */
@Entity
public class AppState {
    @Id
    private Long id = 1L;

    @Lob
    @Column(nullable = false)
    private String json = "{}";

    private Instant updatedAt = Instant.now();

    public Long getId() { return id; }
    public String getJson() { return json; }
    public void setJson(String json) { this.json = json; this.updatedAt = Instant.now(); }
    public Instant getUpdatedAt() { return updatedAt; }
}
