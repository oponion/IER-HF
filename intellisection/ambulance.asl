// Agent ambulance in project intellisection.mas2j

/* Initial beliefs and rules */

close_to_intersection :-
	pos(X,Y) & X < 28 & X > 17.
	
left_intersection :-
	pos(X,Y) &  X == 17.

/* Initial goals */

!go_to_hospital.

/* Plans */

+!go_to_hospital
	:	not close_to_intersection & not left_intersection
	<-	?pos(X,Y);
		move_towards(hospital);
		!go_to_hospital.
		
+!go_to_hospital
	:	close_to_intersection
	<-	?pos(X,Y);
		.send(central_control_unit,tell,ambulance_coming);
		move_towards(hospital);
		!go_to_hospital.
		
+!go_to_hospital
	:	left_intersection
	<-	?pos(X,Y);
		.send(central_control_unit,untell,ambulance_coming);
		move_towards(hospital);
		!go_to_hospital.
	
-!go_to_hospital
	:	true
	<-	!go_to_hospital.

