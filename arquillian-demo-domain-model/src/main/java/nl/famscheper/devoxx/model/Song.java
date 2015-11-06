/*
 * Copyright 2009-2015 PIANOo; TenderNed programma.
 */
package nl.famscheper.devoxx.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;

/**
 * A song performed by an {@link Artist}.
 *
 * @author Erik-Berndt Scheper
 * @since 06-11-2015
 */
@javax.persistence.Entity
@Table(name = "DVX_SONG")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dvx_song_seq_gen")
    @SequenceGenerator(name = "dvx_song_seq_gen", sequenceName = "dvx_song_seq")
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "LYRIC", nullable = true)
    private String lyric;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ARTIST_ID")
    @ForeignKey(name = "DVX_SONG_ARTIST_FK")
    private Artist artist;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     * Build a string representation of this instance, using {@code org.apache.commons.lang.builder.ToStringBuilder} implementation.
     * <p>
     * Note: to use this class, we need this dependency (!)
     * </p>
     *
     * @return string representation of this instance
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("lyric", lyric)
                .append("artist", artist)
                .toString();
    }
}
