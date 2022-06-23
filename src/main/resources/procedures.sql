
CREATE TABLE Tasks (
    TaskID int NOT NULL AUTO_INCREMENT,
    UserID int NOT NULL,
    Description varchar(255) NOT NULL,
    Duration time NOT NULL,
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

INSERT INTO Tasks VALUES (NULL, 1, "test", "20:24");
INSERT INTO Users VALUES (NULL, "login", "pass", "Denis");

DELETE FROM Tasks WHERE TaskID = 2;

INSERT INTO Tasks VALUES (NULL, ?, ?, ?);