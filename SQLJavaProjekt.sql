DROP DATABASE IF EXISTS JavaPetar

GO

CREATE DATABASE JavaPetar

GO

USE JavaPetar

GO

---Tablice---

CREATE TABLE RoleKorisnika
(
	IDRolaKorisnika INT PRIMARY KEY IDENTITY,
	Naziv NVARCHAR(255)
)

GO

INSERT INTO RoleKorisnika (Naziv) VALUES ('Administrator'),('Korisnik')

GO

CREATE TABLE Korisnik
(
	IDKorisnik INT PRIMARY KEY IDENTITY,
	KorisnickoIme NVARCHAR(255),
	Lozinka NVARCHAR(255),
	RolaKorisnikaID INT FOREIGN KEY REFERENCES RoleKorisnika(IDRolaKorisnika)
)

GO

INSERT INTO Korisnik (KorisnickoIme, Lozinka, RolaKorisnikaID) VALUES ('petar', '11872607937258188689332083300641287768688909654719204693923480669724638652197190597595516615002834998813088011474042195554646402593058461426899835667144099', 1)
--petar parker
GO

CREATE TABLE Zanr
(
	IDZanr INT PRIMARY KEY IDENTITY,
	Naziv NVARCHAR(255),
	Aktivan BIT
)

GO

CREATE TABLE Tip
(
	IDTip INT PRIMARY KEY IDENTITY,
	Nazvi NVARCHAR(255)
)

GO

INSERT INTO Tip (Nazvi) VALUES ('Redatelj'), ('Glumac')

GO

CREATE TABLE Djelatnik
(
	IDDjelatnik INT PRIMARY KEY IDENTITY,
	Ime NVARCHAR(255),
	Prezime NVARCHAR(255),
	Aktivan BIT,
	TipID INT FOREIGN KEY REFERENCES Tip(IDTip)
)

GO

CREATE TABLE Film
(
	IDFilm INT PRIMARY KEY IDENTITY,
	Naslov NVARCHAR(255),
	Opis NVARCHAR(255),
	Trajanje INT,
	Slika NVARCHAR(255),
	Aktivan BIT
)

GO

CREATE TABLE ZanarFilm
(
	IDZanarFilm INT PRIMARY KEY IDENTITY,
	FilmID INT FOREIGN KEY REFERENCES Film(IDFilm),
	ZanrID INT FOREIGN KEY REFERENCES Zanr(IDZanr)
)

GO

CREATE TABLE FilmDjelatnik
(
	IDFilmDjelatnik INT PRIMARY KEY IDENTITY,
	FilmID INT FOREIGN KEY REFERENCES Film(IDFilm),
	DjelatnikID INT FOREIGN KEY REFERENCES Djelatnik(IDDjelatnik)
)

GO

---Pogledi---

CREATE VIEW vwGlumac AS
SELECT * FROM Djelatnik WHERE TipID = 1

GO

CREATE VIEW vwRedatelj AS
SELECT * FROM Djelatnik WHERE TipID = 2

GO

CREATE VIEW vwRedateljFilm AS
SELECT * FROM FilmDjelatnik AS fd INNER JOIN Djelatnik as d ON fd.DjelatnikID = d.IDDjelatnik WHERE d.TipID = 1 

GO

CREATE VIEW vwGlumacFilm AS
SELECT * FROM FilmDjelatnik AS fd INNER JOIN Djelatnik as d ON fd.DjelatnikID = d.IDDjelatnik WHERE d.TipID = 2 

GO

---Procedure---

CREATE PROCEDURE spCheckIfUserExists
	@UserName NVARCHAR(255),
	@Password NVARCHAR(255)
AS
IF EXISTS(SELECT KorisnickoIme FROM Korisnik WHERE KorisnickoIme = @UserName AND Lozinka = @Password)
	BEGIN
		SELECT kr.Naziv AS RolaKorisnika FROM Korisnik AS k INNER JOIN RoleKorisnika as kr ON k.RolaKorisnikaID = kr.IDRolaKorisnika
		WHERE k.KorisnickoIme = @UserName AND k.Lozinka = @Password
		RETURN (1)
	END
GO

CREATE PROCEDURE spCreateUser
	@UserName NVARCHAR(255),
	@Password NVARCHAR(255),
	@ID INT OUT
AS
IF EXISTS(SELECT KorisnickoIme FROM Korisnik WHERE IDKorisnik = @ID)
	BEGIN
		SET @ID = 0 
		RETURN (1)
	END
ELSE
	BEGIN
		INSERT INTO Korisnik (KorisnickoIme, Lozinka, RolaKorisnikaID) VALUES (@UserName, @Password, 2)
		SET @ID = @@IDENTITY 
	END
GO

CREATE PROCEDURE spSelectMovies
AS
SELECT DISTINCT f.IDFilm AS ID, f.Naslov AS Naslov, f.Opis AS Opis, f.Slika AS Slika, f.Trajanje AS Trajanje FROM Film AS f where f.Aktivan = 0 

GO

CREATE PROCEDURE spSelectMovie
	@IDMovie INT
AS
SELECT IDFilm AS ID, Naslov, Opis, Trajanje, Slika FROM Film WHERE IDFilm = @IDMovie AND Aktivan = 0

GO

CREATE PROCEDURE spSelectGenresByMovieID
	@MovieID INT
AS
SELECT z.IDZanr, z.Naziv AS Zanr FROM ZanarFilm AS zf INNER JOIN Zanr AS z ON zf.ZanrID = z.IDZanr
WHERE zf.FilmID = @MovieID

GO

CREATE PROCEDURE spSelectActorsByMovieID
	@MovieID INT
AS
SELECT d.IDDjelatnik AS IDGlumac, d.Ime AS GlumacIme, d.Prezime AS GlumacPrezime FROM Djelatnik AS d INNER JOIN FilmDjelatnik AS fd ON fd.DjelatnikID = d.IDDjelatnik
WHERE fd.FilmID = @MovieID AND d.TipID = 2 AND d.Aktivan = 0

GO

CREATE PROCEDURE spSelectDirectorsByMovieID
	@MovieID INT
AS
SELECT d.IDDjelatnik AS IDRedatelj, d.Ime AS RedateljIme, d.Prezime AS RedateljPrezime FROM Djelatnik AS d INNER JOIN FilmDjelatnik AS fd ON fd.DjelatnikID = d.IDDjelatnik
WHERE fd.FilmID = @MovieID AND d.TipID = 1 AND d.Aktivan = 0

GO

CREATE PROCEDURE spCreateMovies
	@Title NVARCHAR(255),
	@Description NVARCHAR(255),
	@Duration INT,
	@Picture NVARCHAR(255),
	@ID INT OUT
AS
BEGIN
	IF EXISTS (SELECT * FROM Film WHERE Naslov = @Title AND Opis = @Description AND Trajanje = @Duration AND Slika = @Picture AND Aktivan = 0)
		BEGIN
			SET @ID = 0
			RETURN (1)
		END
	ELSE
		BEGIN
			INSERT INTO Film (Naslov, Opis, Trajanje, Slika, Aktivan) VALUES (@Title, @Description, @Duration, @Picture, 0)
			SET @ID = @@IDENTITY
			RETURN(2)
		END
	END
GO

CREATE PROCEDURE spCreateMovie
	@Title NVARCHAR(255),
	@Description NVARCHAR(255),
	@Duration INT,
	@ImagePath NVARCHAR(255),
	@IDMovie INT OUT
AS
IF EXISTS (SELECT * FROM Film WHERE Naslov = @Title AND Opis = @Description AND Trajanje = @Duration AND Slika = @ImagePath AND Aktivan = 0)
		BEGIN
			SET @IDMovie = 0
			RETURN (1)
		END
	ELSE
		BEGIN
			INSERT INTO Film (Naslov, Opis, Trajanje, Slika, Aktivan) VALUES (@Title, @Description, @Duration, @ImagePath, 0)
			SET @IDMovie = @@IDENTITY
			RETURN(2)
		END
GO

CREATE PROCEDURE spCreateActors
	@FirstName NVARCHAR(255),
	@LastName NVARCHAR(255),
	@ActorID INT OUT
AS
IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
	BEGIN
		SET @ActorID = (SELECT IDDjelatnik FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
		RETURN(1)
	END
ELSE IF NOT EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
	BEGIN
		INSERT INTO vwGlumac (Ime, Prezime, Aktivan) VALUES (@FirstName, @LastName, 0)
		SET @ActorID = @@IDENTITY
		RETURN (2)
	END
ELSE IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
	BEGIN
		SET @ActorID = (SELECT IDDjelatnik FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
		RETURN (3)
	END
ELSE
	BEGIN
		INSERT INTO vwGlumac (Ime, Prezime, Aktivan) VALUES (@FirstName, @LastName, 0)
		SET @ActorID = @@IDENTITY
		RETURN (4)
	END
GO

CREATE PROCEDURE spCreateMovieActor
	@MovieID int,
	@ActorID int
AS
INSERT INTO vwGlumacFilm (FilmID, DjelatnikID) VALUES (@MovieID, @ActorID)

GO

CREATE PROCEDURE spCreateGenres
	@Name NVARCHAR(255),
	@GenreID INT OUT
AS
IF EXISTS (SELECT * FROM Zanr WHERE Naziv = @Name AND Aktivan = 0)
	BEGIN
		SET @GenreID = (SELECT IDZanr FROM Zanr WHERE Naziv = @Name)
		RETURN (1)
	END
ELSE
	BEGIN
		INSERT INTO Zanr(Naziv, Aktivan) VALUES (@Name, 0)
		SET @GenreID = @@IDENTITY
		RETURN (2)
	END

GO

CREATE PROCEDURE spCreateMovieGenre
	@MovieID INT,
	@GenreID INT
AS
INSERT INTO ZanarFilm(FilmID, ZanrID) VALUES (@MovieID, @GenreID)

GO

CREATE PROCEDURE spCreateDirectors
	@FirstName NVARCHAR(255),
	@LastName NVARCHAR(255),
	@DirectorID INT OUT
AS
IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
	BEGIN
		SET @DirectorID = (SELECT IDDjelatnik FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
		RETURN(1)
	END
ELSE IF NOT EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
	BEGIN
		INSERT INTO vwGlumac (Ime, Prezime, Aktivan) VALUES (@FirstName, @LastName, 0)
		SET @DirectorID = @@IDENTITY
		RETURN (2)
	END
ELSE IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
	BEGIN
		SET @DirectorID = (SELECT IDDjelatnik FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
		RETURN (3)
	END
ELSE
	BEGIN
		INSERT INTO vwRedatelj(Ime, Prezime, Aktivan) VALUES (@FirstName, @LastName, 0)
		SET @DirectorID = @@IDENTITY
		RETURN (4)
	END
GO

CREATE PROCEDURE spCreateMovieDirector
	@MovieID INT,
	@DirectorID INT
AS
INSERT INTO vwRedateljFilm (FilmID, DjelatnikID) VALUES (@MovieID, @DirectorID)

GO

CREATE PROCEDURE spDeleteMovies
AS
UPDATE Film 
SET Aktivan = 1

GO

CREATE PROCEDURE spDeleteMovie
	@IDMovie INT,
	@ID INT OUT
AS
UPDATE Film SET Aktivan = 1 WHERE IDFilm = @IDMovie
SET @ID = 0

GO

CREATE PROCEDURE spUpdateMovie
	@IDMovie INT,
	@Title NVARCHAR(255),
	@Descrription NVARCHAR(255),
	@Duration INT,
	@ImagePath NVARCHAR(255)
AS
UPDATE Film SET Naslov = @Title, Opis = @Descrription, Trajanje = @Duration, Slika = @ImagePath WHERE IDFilm = @IDMovie 

GO

CREATE PROCEDURE spSelectActors
AS
SELECT IDDjelatnik AS IDGlumac, Ime AS GlumacIme, Prezime as GlumacPrezime  FROM Djelatnik WHERE Aktivan = 0 AND TipID = 2

GO

CREATE PROCEDURE spCreateActor
	@FirstName NVARCHAR(255),
	@LastName NVARCHAR(255),
	@ID INT OUT
AS
IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 2)
	BEGIN
		SET @ID = 0
		RETURN (1)
	END
ELSE IF (LEN(@FirstName) > 0 AND LEN(@LastName) > 0)
	BEGIN
		INSERT INTO vwGlumac (Ime, Prezime, Aktivan, TipID) VALUES (@FirstName, @LastName, 0, 2)
		SET @ID = @@IDENTITY
		RETURN (2)
	END
Go

CREATE PROCEDURE spSelectDirectors
AS
SELECT IDDjelatnik AS IDRedatelj, Ime AS RedateljIme, Prezime as RedateljPrezime  FROM Djelatnik WHERE Aktivan = 0 AND TipID = 1

GO

CREATE PROCEDURE spCreateDirector
	@FirstName NVARCHAR(255),
	@LastName NVARCHAR(255),
	@ID INT OUT
AS
IF EXISTS (SELECT * FROM Djelatnik WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0 AND TipID = 1)
	BEGIN
		SET @ID = 0
		RETURN (1)
	END
ELSE
	BEGIN
		INSERT INTO vwRedatelj (Ime, Prezime, Aktivan, TipID) VALUES (@FirstName, @LastName, 0, 1)
		SET @ID = @@IDENTITY
		RETURN (2)
	END
GO

CREATE PROCEDURE spSelectPersons
AS
SELECT DISTINCT d.IDDjelatnik, d.Ime, d.Prezime, t.Nazvi as Tip FROM Djelatnik AS d 
INNER JOIN Tip AS t ON t.IDTip = d.TipID 
WHERE Aktivan = 0

GO

CREATE PROCEDURE spUpdatePerson
	@IDPerson INT,
	@FirstName NVARCHAR(255),
	@LastName NVARCHAR(255),
	@NewFirstName NVARCHAR(255),
	@NewLastName NVARCHAR(255),
	@ID INT OUT
AS
IF EXISTS(SELECT * FROM Djelatnik WHERE IDDjelatnik = @IDPerson AND Aktivan = 0)
	BEGIN 
		UPDATE Djelatnik SET Ime = @NewFirstName, Prezime = @NewLastName WHERE Ime = @FirstName AND Prezime = @LastName AND Aktivan = 0
		SET @ID = ( SELECT TipID FROM Djelatnik WHERE IDDjelatnik = @IDPerson AND Aktivan = 0 )
		RETURN(1)
	END
GO

CREATE PROCEDURE spSelectPerson
	@IDPerson INT
AS
SELECT d.IDDjelatnik, d.Ime, d.Prezime, t.Nazvi as Tip FROM Djelatnik AS d 
INNER JOIN Tip AS t ON t.IDTip = d.TipID
WHERE d.IDDjelatnik = @IDPerson

GO

CREATE PROCEDURE spDeletePerson
	@IDPerson INT,
	@ID INT OUT
AS
UPDATE Djelatnik SET Aktivan = 1
WHERE IDDjelatnik = @IDPerson
SET @ID = 0

GO

CREATE PROCEDURE spSelectGenres
AS
SELECT IDZanr, Naziv AS Zanr FROM Zanr WHERE Aktivan = 0

GO

CREATE PROCEDURE spCreateGenre
	@Name NVARCHAR(255),
	@GenreID INT OUT
AS
IF EXISTS(SELECT * FROM Zanr WHERE Naziv = @Name AND Aktivan = 0)
	BEGIN
		SET @GenreID = 0
		RETURN (1)
	END
ELSE
	BEGIN
		INSERT INTO Zanr (Naziv, Aktivan) VALUES (@Name, 0)
		SET @GenreID = @@IDENTITY
		RETURN(2)
	END
GO

CREATE PROCEDURE spSelectGenre
	@IDGenre INT
AS
SELECT IDZanr, Naziv as Zanr FROM Zanr WHERE IDZanr = @IDGenre AND Aktivan = 0

GO

CREATE PROCEDURE spDeleteGenre
	@IDGenre INT,
	@ID INT OUT
AS
UPDATE Zanr SET Aktivan = 1
WHERE IDZanr = @IDGenre
SET @ID = 0

GO

CREATE PROCEDURE spUpdateGenre
	@IDGenre INT,
	@Name NVARCHAR(255),
	@ID INT OUT
AS
UPDATE Zanr SET Naziv = @Name WHERE IDZanr = @IDGenre AND Aktivan = 0
SET @ID = 0

GO