// Agent central_control_unit in project intellisection.mas2j

/* Initial beliefs and rules */

nobody_inside :-
	.count(inside(_),N) & N == 0.

green(west_east).

/* Initial goals */

!seamless_traffic.

/* Plans */

@s1[atomic]
+!seamless_traffic
	: (green(west_east) | last_green(west_east)) & nobody_inside
	<- .wait(8000);
	   .broadcast(untell,green(west_east));
	   .broadcast(tell,green(south_north));
	   .print(green_south_north);
	   -last_green(west_east);
	   -+green(south_north);
	   !seamless_traffic.
	
@s2[atomic]
+!seamless_traffic
	:  (green(south_north) | last_green(south_north)) & nobody_inside
	<- .wait(8000);
	   .broadcast(untell,green(south_north));
	   .broadcast(tell,green(west_east));
	   .print(green_west_east);
	   -last_green(south_north);
	   -+green(west_east);
	   !seamless_traffic.
	   
+!seamless_traffic
	: 	not nobody_inside
	<-	?green(D);
		.broadcast(untell,green(south_north));
		.broadcast(untell,green(west_east));
		-green(south_north);
		-green(west_east);
		+last_green(D);
		!seamless_traffic.
		

//+!congestion_control

