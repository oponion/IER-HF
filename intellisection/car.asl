// Agent car in project intellisection.mas2j

/* Initial beliefs and rules */

//TODO megjegyezni, hogy elozo lepesben bent volt-e, es attol fuggoen dumalni a central_control_unitnak
inside :- pos(X,Y) & cell(X,Y,broadcast).

going_towards_traffic_light.

/* Initial goals */

//!ask_driver.
!go_to_traffic_light.

/* Plans */

/*+!start : true
	<- ?my_pos(X, Y);
	   utils.get_source_direction(X,Y,D).*/
	   

/*+my_pos(X,Y)
	: going_towards_intersection
	<- step_towards_traffic_light(X, Y).*/

+!go_to_traffic_light
	: true
	<- +going_towards_traffic_light;
	   !at(car,traffic_light).
	
+!at(car,P)
	: 	pos(X,Y) & traffic_light_pos(X,Y) & going_towards_traffic_light
	<- 	.print(reached_traffic_light);
		-going_towards_traffic_light;
		!wait_for_green.

+!at(car,P) : not at(car,P) & not inside
	<- 	!untell_central_unit;
		move_towards(P);
		!at(car,P).
	   
+!at(car,P) : not at(car,P) & inside
	<- move_towards(P);
	   !tell_central_unit;
	   !at(car,P).
	   
+!wait_for_green
	:   not (green(D) & my_source(S) & .substring(S,D))
	<-  !wait_for_green.
	
+!wait_for_green
	:	green(D) & my_source(S) & .substring(S,D)
	<-	!go_to_destination.
	
+!go_to_destination
	:	true
	<-	+going_towards_destination;
		.print(going_to_destination);
		!at(car,destination).
	   
+!ask_driver
	: true
	<- .my_name(N);
	   utils.get_driver_name(N,D);
	   +my_driver(D);
	   .send(D, achieve, tell_destination(N)).
	   
+!tell_central_unit
	: 	true
	<-	.my_name(N);
		.print(iNSIDEYEAH);
		.send(central_control_unit,tell,inside(N)).
		
+!untell_central_unit
	: 	true
	<-	.my_name(N);
		.send(central_control_unit,untell,inside(N)).

+route(Source, Dest)
	: true
	<- +my_source(Source);
	   +my_dest(Dest).


