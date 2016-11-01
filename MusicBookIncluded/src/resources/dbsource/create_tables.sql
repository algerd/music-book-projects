CREATE TABLE "artist" (
    "id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "name"  TEXT(255) NOT NULL,
    "genre"  TEXT(255),
    "rating"  INTEGER,
    "comment"  TEXT
);

CREATE TABLE "album" (
    "id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "id_artist"  INTEGER NOT NULL,
    "name"  TEXT(255) NOT NULL,
    "year"  INTEGER,
    "time"  TEXT,
    "reference_cover"  TEXT(255),
    "rating"  INTEGER,
    "comment"  TEXT,
    CONSTRAINT "fkey0" FOREIGN KEY ("id_artist") REFERENCES "artist" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "song" (
    "id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "id_album"  INTEGER,
    "name"  TEXT(255),
    "track"  INTEGER,
    "lyric"  TEXT,
    "time"  TEXT,
    "rating"  INTEGER,
    "comment"  TEXT,
    CONSTRAINT "fkey0" FOREIGN KEY ("id_album") REFERENCES "album" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "genre" (
    "id"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "name"  TEXT(255) NOT NULL,
    "description"  TEXT,
    CONSTRAINT "name" UNIQUE ("name")
);






