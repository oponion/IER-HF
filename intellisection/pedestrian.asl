// Agent pedestrian in project intellisection.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!wait_for_green.

/* Plans */

+!at(pedestrian,P) : not at(pedestrian,P)
	<-	move_towards(P);
		!at(pedestrian,P).
	   
+!wait_for_green
	:   not (green(D) & my_source(S) & .substring(S,D))
	<-	+waiting_for_green;
		!wait_for_green.
	
+!wait_for_green
	:	green(D) & my_source(S) & .substring(S,D)
	<-	-waiting_for_green;
		!go_to_destination.
	
+!go_to_destination
	:	true
	<-	+going_towards_destination;
		!at(pedestrian,destination).

+route(Source, Dest)
	: true
	<- +my_source(Source);
	   +my_dest(Dest).

