/*
 * Copyright 2009-2015 PIANOo; TenderNed programma.
 */
package nl.famscheper.devoxx.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nl.famscheper.devoxx.model.Artist;
import nl.famscheper.devoxx.model.Song;

/**
 * Simple transactional service for Artists
 *
 * @author Erik-Berndt Scheper
 * @since 09-11-2015
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ArtistService {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Add a song to the {@link Artist} with the given id
     *
     * @param artistId {@code id} of the artist
     * @param song     the newly added song
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addSong(Long artistId, Song song) {
        Artist artist = entityManager.find(Artist.class, artistId);
        artist.addSong(song);
        song.setArtist(artist);

        entityManager.persist(song);
    }
}
