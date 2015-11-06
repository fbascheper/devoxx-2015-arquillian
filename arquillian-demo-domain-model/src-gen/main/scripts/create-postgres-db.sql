
    create table DVX_ARTIST (
        ID int8 not null,
        NAME varchar(255) not null,
        primary key (ID)
    );

    create table DVX_SONG (
        ID int8 not null,
        LYRIC varchar(255),
        NAME varchar(255) not null,
        ARTIST_ID int8 not null,
        primary key (ID)
    );

    alter table DVX_SONG 
        add constraint DVX_SONG_ARTIST_FK 
        foreign key (ARTIST_ID) 
        references DVX_ARTIST;

    create sequence dvx_artist_seq;

    create sequence dvx_song_seq;
