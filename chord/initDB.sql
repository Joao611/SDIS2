CREATE TABLE filesstored(
	id INT NOT NULL,
	i_am_responsible BOOLEAN DEFAULT false,
	desired_rep_degree INT,
	actual_rep_degree INT,
	peer_which_requested INT
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
   ADD CONSTRAINT filesstored_FK Foreign Key (peer_which_requested)
   REFERENCES peer;

