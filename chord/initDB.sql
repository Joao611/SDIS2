CREATE TABLE filesstored(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	file_id VARCHAR(16) NOT NULL,
	i_am_responsible BOOLEAN DEFAULT false,
	peer_requesting VARCHAR(16),
	desired_rep_degree INTEGER
);

CREATE TABLE peers(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	peer_id VARCHAR(16) NOT NULL,
	ip VARCHAR(15) NOT NULL,
	port INT NOT NULL
);

CREATE TABLE backupsrequested(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	file_id VARCHAR(16) NOT NULL,
	filename VARCHAR(128) NOT NULL,
<<<<<<< HEAD
	encrypt_key VARCHAR(256),
	numberOfChunks INTEGER NOT NULL
=======
	desired_rep_degree INTEGER NOT NULL,
	encrypt_key VARCHAR(256) 
>>>>>>> branch 'master' of https://github.com/Joao611/SDIS2.git
);

CREATE TABLE chunksstored(
	id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	chunk_id INT NOT NULL,
	file_id VARCHAR(16) NOT NULL,
	actual_rep_degree INTEGER
);

ALTER TABLE chunksstored
   ADD CONSTRAINT chunksstored_PK Primary Key (id);
ALTER TABLE chunksstored
   ADD CONSTRAINT chunksstored_UNIQUE UNIQUE (chunk_id,file_id);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_PK Primary Key (id);
ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_UNQIUE UNIQUE (file_id);

ALTER TABLE peers
   ADD CONSTRAINT peers_PK Primary Key (id);
ALTER TABLE peers
   ADD CONSTRAINT peers_UNIQUE1 UNIQUE (peer_id);
ALTER TABLE peers
   ADD CONSTRAINT peers_UNIQUE2 UNIQUE (ip,port);

ALTER TABLE backupsrequested
   ADD CONSTRAINT backupsrequested_PK PRIMARY KEY (id);
ALTER TABLE backupsrequested
   ADD CONSTRAINT backupsrequested_UNIQUE UNIQUE (file_id);

ALTER TABLE filesstored
   ADD CONSTRAINT filesstored_FK Foreign Key (peer_requesting)
   REFERENCES peers(peer_id);

ALTER TABLE chunksstored
   ADD CONSTRAINT chunksstored_FK Foreign Key (file_id) REFERENCES filesstored(file_id);


