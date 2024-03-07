package com.example.musify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    @Column(name = "release_date")
    private Instant releaseDate;
    @Column(name = "album_image")
    private String image;
    @Column(name = "album_slug")
    private String slug;
    @Column(name = "origin_country")
    private String originCountry;
    private Double rating;
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Artist artist;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE,
            })
    @JoinTable(name = "album_genres",
            joinColumns = {@JoinColumn(name = "album_id")},
            inverseJoinColumns = {@JoinColumn(name = "genre_id")})
    private Set<Genre> albumGenres = new HashSet<>();

    @ManyToMany(mappedBy = "albums")
    @JsonIgnore
    private Set<AlbumList> lists = new HashSet<>();

    public void addGenre(Genre genre) {
        this.albumGenres.add(genre);
        genre.getAlbums().add(this);
    }

    public void removeGenre(UUID genreId) {
        Genre genre = this.albumGenres.stream()
                .filter(t -> t.getId().equals(genreId))
                .findFirst().orElse(null);
        if (genre != null) {
            this.albumGenres.remove(genre);
            genre.getAlbums().remove(this);
        }
    }
}
