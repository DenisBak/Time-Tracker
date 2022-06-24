
CREATE TABLE Tracks (
    TaskID int NOT NULL AUTO_INCREMENT,
    UserID int NOT NULL,
    Description varchar(255) NOT NULL,
    Duration VARCHAR(255) NOT NULL,
    Date DATE NOT NULL,
    PRIMARY KEY (TaskID)
);

CREATE TABLE Users (
    UserID int NOT NULL AUTO_INCREMENT,
    UserName varchar(255) NOT NULL,
    Password varchar(255) NOT NULL,
    Name varchar(255) NOT NULL,
    PRIMARY KEY (UserID),
    UNIQUE (UserName)
);

INSERT INTO Tracks VALUES (NULL, 1, "test", "20:24");
INSERT INTO Users VALUES (NULL, "login", "pass", "Denis");

DELETE FROM Tracks WHERE TaskID = 2;

INSERT INTO Tracks VALUES (NULL, ?, ?, ?);