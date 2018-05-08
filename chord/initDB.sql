CREATE TABLE filesstored(
	id INT NOT NULL,
	i_am_responsible BOOLEAN DEFAULT false,
	peer_requesting INT
);

CREATE TABLE peers(
	id INT NOT NULL,
	ip VARCHAR(15) NOT NULL,
	port INT NOT NULL
);

CREATE TABLE backupsrequested(
	file_id INT NOT NULL,
	encrypt_key VARCHAR(256) 
);

CREATE TABLE chunksstored(
	id INT NOT NULL,
	file_id INT NOT NULL,
);

ALTER TABLE chunksstored
   ADD CONSTRAINT chunksstored_PK Primary Key (id,file_id);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_PK Primary Key (id);

ALTER TABLE peers
   ADD CONSTRAINT peers_PK Primary Key (id);

ALTER TABLE backupsrequested
   ADD CONSTRAINT backupsrequested_PK PRIMARY KEY (file_id);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_FK Foreign Key (peer_requesting)
   REFERENCES peers;

ALTER TABLE chunksstored
   ADD CONSTRAINT chunksstored_FK Foreign Key (file_id) REFERENCES filesstored;


