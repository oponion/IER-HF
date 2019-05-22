// Agent car in project intellisection.mas2j

/* Initial beliefs and rules */
going_towards_intersection.

/* Initial goals */

!at(car,traffic_light).

/* Plans */

/*+!start : true
	<- ?my_pos(X, Y);
	   utils.get_source_direction(X,Y,D).*/
	   

/*+my_pos(X,Y)
	: going_towards_intersection
	<- step_towards_traffic_light(X, Y).*/

+!at(car,P) : at(car,P) <- true.
+!at(car,P) : not at(car,P)
	<- move_towards(P);
	   !at(car,P).

