// Agent driver in project intellisection.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world.").

+!tell_destination(Car)
	: true
	<- utils.choose_destination(D);
	   .send(Car, tell, destination(D)).
	   

