/*
 * Copyright 2009-2015 PIANOo; TenderNed programma.
 */
package nl.famscheper.devoxx.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An {@link Artist}
 *
 * @author Erik-Berndt Scheper
 * @since 06-11-2015
 */
@javax.persistence.Entity
@Table(name = "DVX_ARTIST")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dvx_artist_seq_gen")
    @SequenceGenerator(name = "dvx_artist_seq_gen", sequenceName = "dvx_artist_seq")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "artist")
    private List<Song> songs = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return Collections.unmodifiableList(songs);
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    /**
     * Build a string representation of this instance, using {@code org.apache.commons.lang.builder.ToStringBuilder} implementation.
     * <p>
     * Note: to use this class, we need this dependency (!)
     * </p>
     *
     * @return string representation of this instance
     */
    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("songs", songs)
                .toString();
    }
}
