Main function modification:

	Before going into the todo functions, we changed the main functions so that it identifies the logged in user as either manager or customer. It uses sql to check the user type in the dataset with the string authorisedUser after login. Therefore, customers are allowed to view hotels, view rooms, view booking history, and book rooms. On the other hand, managers have access to all functions above and also, update room information, view 5 recent room updates info, view booking history of the hotel, view 5 regular customers, place, and view room repair requests.

To-do functions implementation:

	(1) The first function is to view hotels based on latitude and longitude. After getting the user’s input of latitude and longitude, the function calculate_distance(%f, %f, H.latitude, H.longitude) is implemented in create_table.sql. The distance is equal to sqrt((user_input_latitude-hotel_i_latitude)^2+(user_input_longitude-hotel_i_longitude)^2). Select the row only if the distance is <= 30.

	(2) The second function is the view room. It requires the hotel id and date using java’s simple date format to accept the date in the correct format. The sql query will use CASE to check if the rooms table has the booking id or not. If the booking id is null, which means the room at the date is available for booking. And if the return value after executing the query is empty, there is no room available for the given hotel Id and date.

	(3) Third function is used to book rooms. It requires hotel id, room number, and booking date. The query will return a table that satisfies the given input. If the return table is empty, it means the room is available with the given input. Once a valid room is booked by the user, it will insert the new booking information into the room booking dataset with a trigger function. In which, it will push the new room booking info to the back of the room booking dataset.
	
	(4) View recent booking history is a function that uses the user id. It selects the room that has the booking user id by looping through the room bookings table and rooms table first. Then, it sorted the table by booking date from latest at the top to oldest at the bottom. Lastly, it returns the latest 5 booking history by using LIMIT 5 in sql query.

	(5) Update room info is a function that requires the user to be the manager of the input hotel at the beginning. It first checks the hotel id and manager user id from the hotel table. If the hotel manager id matches the hotel id, it will require the manager to input room id, new room price, and new room URL. Before updating, the function will first create a table with price and image URL from rooms table that matched hotel id and room id. Then, it will use UPDATE in the sql query to update the two attributes of the selected room id. Lastly it will also INSERT into the room update log with a trigger to push back the update info.

	(6) View recent updates pulls table from room update log where manager id matches. It is in order from the latest update date at the top and oldest update at the bottom. Only the first 5 will be presented by LIMIT 5.

	(7) View booking history checks from a start date to an end date given by user input. Then it will search through room bookings table and users table, where customer id matches user id in room booking tables, as well as the booking date is in between the user input’s dates. Ordered by booking date.

	(8) View 5 regular customers function is only available for managers to check the hotel that they are managing. Therefore, it first checks if the input of the hotel id matches the manager’s id or not. The regular customers are found by using COUNT(booking id) in sql query for each customer. Counting the booking id will return how many times each customer had booked the rooms in the hotel. Similarly, LIMIT 5 and ORDER BY the count total will give 5 customers who have booked the most. 

	(9) Place room repair requests also require the user to be the manager of the hotel by checking if the manager user id matches the user id, as well as the hotel id in the hotel table.
If not matched, the system will print an error message. If it matches, it will first insert repair company id, hotel id, room number, and request date into the room repairs table using trigger and insert. Then it will select it from the room repair table and insert it into the room repair requests table using trigger and repair. 

(10) Lastly, view room repair requests history checks the hotel to see if the user is the manager of the hotel, and the manager id in room repair as well as room repair request table using JOIN. 

Index implementation:
	Only create indexes on Hotel and Rooms as Hotel(hotelID, managerUserID) and Rooms(hotelID, roomNumber) are frequently used in WHERE clauses in many SQL queries. Some tables like RoomBookings and RoomUpdatesLog do not use indexes as they update frequently.

Trigger implementation:
	5 triggers are implemented as there are 5 insert operations on several tables. All the triggers are doing the same thing: before each insert, increment the serial number on a specific table.
