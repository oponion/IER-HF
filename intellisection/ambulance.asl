// Agent ambulance in project intellisection.mas2j

/* Initial beliefs and rules */



/* Initial goals */

!go_to_hospital.

/* Plans */

+!go_to_hospital
	:	true
	<-	?pos(X,Y);
		.send(central_control_unit,tell,ambulance_pos(X,Y));
		move_towards(hospital);
		!go_to_hospital.
	
-!go_to_hospital
	:	true
	<-	!go_to_hospital.

