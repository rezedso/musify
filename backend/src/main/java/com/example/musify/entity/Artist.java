package com.example.musify.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(name = "origin_country")
    private String originCountry;
    @Column(name = "formed_year")
    private Year formedYear;
    @Column(name = "artist_image")
    private String image;
    @Column(name = "artist_slug")
    private String slug;
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "artist_genres",
            joinColumns = {@JoinColumn(name = "artist_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")})
    private Set<Genre> artistGenres = new HashSet<>();

    public void addGenre(Genre genre) {
        this.artistGenres.add(genre);
        genre.getArtists().add(this);
    }

    public void removeGenre(UUID genreId) {
        Genre genre = this.artistGenres.stream()
                .filter(t -> t.getId().equals(genreId))
                .findFirst().orElse(null);
        if (genre != null) {
            this.artistGenres.remove(genre);
            genre.getArtists().remove(this);
        }
    }
}
