CREATE OR REPLACE LANGUAGE plpgsql;

--INSERT INTO USERS (name, password, userType) VALUES ('%s','%s', '%s');
DROP SEQUENCE IF EXISTS userID_seq;
CREATE SEQUENCE userID_seq START WITH 100;

CREATE OR REPLACE FUNCTION increment_userID() RETURNS TRIGGER AS
$BODY$
BEGIN
   NEW.userID = nextval('userID_seq');
   RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS increment_userID_trigger ON Users;
CREATE TRIGGER increment_userID_trigger BEFORE INSERT
ON Users FOR EACH ROW
EXECUTE PROCEDURE increment_userID();

--INSERT INTO RoomBookings (customerID, hotelID, roomNumber, bookingDate) VALUES (%d, %d, %d, '%s');
DROP SEQUENCE IF EXISTS bookingID_seq;
CREATE SEQUENCE bookingID_seq START WITH 500;

CREATE OR REPLACE FUNCTION increment_bookingID() RETURNS TRIGGER AS
$BODY$
BEGIN
   NEW.bookingID = nextval('bookingID_seq');
   RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS increment_bookingID_trigger ON RoomBookings;
CREATE TRIGGER increment_bookingID_trigger BEFORE INSERT
ON RoomBookings FOR EACH ROW
EXECUTE PROCEDURE increment_bookingID();


--INSERT INTO RoomUpdatesLog (managerID, hotelID, roomNumber, updatedOn) VALUES (%d, %d, %d, 'NOW()');
DROP SEQUENCE IF EXISTS updateNumber_seq;
CREATE SEQUENCE updateNumber_seq START WITH 50;

CREATE OR REPLACE FUNCTION increment_updateNumber() RETURNS TRIGGER AS
$BODY$
BEGIN
   NEW.updateNumber = nextval('updateNumber_seq');
   RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS increment_updateNumber_trigger ON RoomUpdatesLog;
CREATE TRIGGER increment_updateNumber_trigger BEFORE INSERT
ON RoomUpdatesLog FOR EACH ROW
EXECUTE PROCEDURE increment_updateNumber();


--INSERT INTO RoomRepairs (companyID, hotelID, roomNumber, repairDate) VALUES (%d, %d, %d, '%s');
DROP SEQUENCE IF EXISTS repairID_seq;
CREATE SEQUENCE repairID_seq START WITH 10;

CREATE OR REPLACE FUNCTION increment_repairID() RETURNS TRIGGER AS
$BODY$
BEGIN
   NEW.repairID = nextval('repairID_seq');
   RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS increment_repairID_trigger ON RoomRepairs;
CREATE TRIGGER increment_repairID_trigger BEFORE INSERT
ON RoomRepairs FOR EACH ROW
EXECUTE PROCEDURE increment_repairID();


--INSERT INTO RoomRepairRequests (managerID, repairID) VALUES (%d, %d);
DROP SEQUENCE IF EXISTS requestNumber_seq;
CREATE SEQUENCE requestNumber_seq START WITH 10;

CREATE OR REPLACE FUNCTION increment_requestNumber() RETURNS TRIGGER AS
$BODY$
BEGIN
   NEW.requestNumber = nextval('requestNumber_seq');
   RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS increment_requestNumber_trigger ON RoomRepairRequests;
CREATE TRIGGER increment_requestNumber_trigger BEFORE INSERT
ON RoomRepairRequests FOR EACH ROW
EXECUTE PROCEDURE increment_requestNumber();