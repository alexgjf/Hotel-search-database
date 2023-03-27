-- I did not add index to other tables as some of their values updates frequently,
-- and some of their attributes are not used frequently in the queries

--Create a index on Hotel table as its hotelID and managerUserID are used frequently in the queries
CREATE INDEX index_Hotel ON Hotel (hotelID, managerUserID);

--Create a index on Rooms table as its hotelID and roomNumber are used frequently in the queries
CREATE INDEX index_Rooms ON Rooms (hotelID, roomNumber);