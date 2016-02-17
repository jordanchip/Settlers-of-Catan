DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS commands;

CREATE TABLE games (
uuid CHAR PRIMARY KEY  NOT NULL UNIQUE,
header BLOB NOT NULL,
game BLOB NOT NULL
);

CREATE TABLE user (
id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE,
username CHAR NOT NULL  UNIQUE , password CHAR
);

CREATE TABLE commands (
id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
gameid CHAR(36) NOT NULL,
command BLOB NOT NULL
);
