CREATE TABLE filesstored(
	id INT NOT NULL,
	i_am_responsible BOOLEAN DEFAULT false,
	peer_requesting INT
);

CREATE TABLE peer(
	id INT NOT NULL,
	ip VARCHAR(15) NOT NULL,
	port INT NOT NULL
);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_PK Primary Key (id);

ALTER TABLE peer
   ADD CONSTRAINT peer_PK Primary Key (id);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_FK Foreign Key (peer_requesting)
   REFERENCES peer;

