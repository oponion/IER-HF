// Agent car in project intellisection.mas2j

/* Initial beliefs and rules */

inside :- pos(X,Y) & cell(X,Y,broadcast).

going_towards_traffic_light.

/* Initial goals */

!go_to_traffic_light.

/* Plans */

+!go_to_traffic_light
	: true
	<- +going_towards_traffic_light;
	   !at(car,traffic_light).
	
+!at(car,P)
	: 	pos(X,Y) & traffic_light_pos(X,Y) & going_towards_traffic_light
	<- 	-going_towards_traffic_light;
		!wait_for_green.

+!at(car,P) : not at(car,P) & not inside
	<- 	-danger_zone;
		move_towards(P);
		!at(car,P).
	   
+!at(car,P) : not at(car,P) & inside
	<- move_towards(P);
	   +danger_zone;
	   !at(car,P).
	   
+!wait_for_green
	:   not (green(D) & my_source(S) & .substring(S,D)) & not ambulance_behind
	<-	+waiting_for_green;
		!wait_for_green.
	
+!wait_for_green
	:	green(D) & my_source(S) & .substring(S,D)
	<-	-waiting_for_green;
		!go_to_destination.
		
-!wait_for_green
	:	true
	<-	!wait_for_green.
	
+!go_to_destination
	:	true
	<-	+going_towards_destination;
		!at(car,destination).
	   
+danger_zone
	: 	true
	<-	.my_name(N);
		.send(central_control_unit,tell,inside(N)).
		
-danger_zone
	: 	true
	<-	.my_name(N);
		.send(central_control_unit,untell,inside(N)).
		
+route(Source, Dest)
	: true
	<- +my_source(Source);
	   +my_dest(Dest).
	   
+ambulance_behind
	:	pos(X,Y)
	<-	+last_position(X,Y);
		.drop_all_intentions;
		move_aside;
		-ambulance_behind.

-ambulance_behind
	:	true
	<-	!continue.
	
+!continue
	:	not (last_position(X,Y) & pos(X,Y)) & last_position(U,V)
	<-	.wait(500);
		move_to(U,V);
		.my_name(N);
		.print(N,_tried_move_to,U,_V);
		!continue.
		
-!continue
	:	true
	<-	!continue.

+!continue
	:	last_position(X,Y) & pos(X,Y) & (going_towards_traffic_light | waiting_for_green)
	<-	-last_position(_,_);
		!go_to_traffic_light.
		
+!continue
	:	last_position(X,Y) & pos(X,Y) & going_towards_destination
	<-	-last_position(_,_);
		!go_to_destination.

