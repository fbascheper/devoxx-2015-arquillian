/*
 * Copyright 2009-2015 PIANOo; TenderNed programma.
 */
package nl.famscheper.devoxx.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nl.famscheper.devoxx.service.ArtistService;
import nl.famscheper.devoxx.test.util.ArquillianDeploymentSupport;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

/**
 * Arquillian DIY integration test used to test the persistence of {@link Artist}s and {@link Song}s using PostgreSQL.
 *
 * @author Erik-Berndt Scheper
 * @since 06-11-2015
 */
@RunWith(Arquillian.class)
public class ArtistSongPersistenceDiyIT {

    private static final Logger LOGGER = getLogger(ArtistSongPersistenceDiyIT.class);
    private static Long peteSeegerId = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ArtistService artistService;

    @Deployment
    public static Archive<?> createDeploymentPackage() {
        return ArquillianDeploymentSupport.createIntegrationTestArchive(ArtistSongPersistenceDiyIT.class.getSimpleName().toLowerCase() + ".war");
    }

    /**
     * Persist a song without an artist (not allowed).
     */
    @Test
    //    @UsingDataSet(value = {"data/entity/LobEntityPersistIT-data.xml"})
    //    @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
    @Transactional(TransactionMode.COMMIT)
    @InSequence(1)
    @Ignore
    public void testPersistSongWithoutArtistFails() {

        Song song = new Song();
        song.setArtist(null);
        song.setName("We shall overcome");

        entityManager.persist(song);
    }

    /**
     * Persist initial artist, without songs.
     */
    @Test
    //    @UsingDataSet(value = {"data/entity/LobEntityPersistIT-data.xml"})
    //    @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
    @Transactional(TransactionMode.COMMIT)
    @InSequence(2)
    public void testPersistArtist() {

        Artist artist = new Artist();
        artist.setName("Pete Seeger");

        entityManager.persist(artist);

        peteSeegerId = artist.getId();
        assertThat(peteSeegerId, notNullValue());

        LOGGER.info("**** Persisted artist with id = {}", peteSeegerId);
    }

    /**
     * Add a song to an artist, using the {@link ArtistService#addSong(Long, Song)} method.
     */
    @Test
    //    @UsingDataSet(value = {"data/entity/LobEntityPersistIT-data.xml"})
    //    @Cleanup(phase = TestExecutionPhase.AFTER, strategy = CleanupStrategy.USED_ROWS_ONLY)
    @Transactional(TransactionMode.ROLLBACK)
    @InSequence(3)
    public void testAddSongToArtist() {
        assertThat(peteSeegerId, notNullValue());

        Artist found = entityManager.find(Artist.class, peteSeegerId);
        assertThat(found, notNullValue());

        LOGGER.info("**** Found Artist = {} ***", found);

        assertThat(found.getId(), is(peteSeegerId));
        int initialSongCount = found.getSongs().size();

        Song song = new Song();
        song.setArtist(found);
        song.setName("We shall overcome");

        artistService.addSong(found.getId(), song);

        // clear current persistence context
        entityManager.clear();

        Artist updated = entityManager.find(Artist.class, peteSeegerId);

        assertThat(found, not(sameInstance(updated)));

        assertThat(found.getSongs().size(), is(initialSongCount));
        assertThat(updated.getSongs().size(), is(initialSongCount + 1));
    }

}
