CREATE TABLE `registrations` (
  `idregistrations` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `contact` varchar(16) NOT NULL,
  `mobilestamp` varchar(256) NOT NULL,
  PRIMARY KEY (`idregistrations`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `profile` (
  `idprofile` int(11) NOT NULL AUTO_INCREMENT,
  `number` varchar(16) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`idprofile`),
  UNIQUE KEY `number_UNIQUE` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `history` (
  `idHISTORY` int(11) NOT NULL AUTO_INCREMENT,
  `fromUser` varchar(16) NOT NULL,
  `toUser` varchar(45) NOT NULL,
  `Response` varchar(250) DEFAULT NULL,
  `when` datetime NOT NULL,
  PRIMARY KEY (`idHISTORY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


