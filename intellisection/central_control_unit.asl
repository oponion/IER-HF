// Agent central_control_unit in project intellisection.mas2j

/* Initial beliefs and rules */

green(west_east).

/* Initial goals */

!seamless_traffic.

/* Plans */

@s1[atomic]
+!seamless_traffic
	: green(west_east)
	<- .wait(1000);
	   .broadcast(untell,green(west_east));
	   .broadcast(tell,green(south_north));
	   .print(green_south_north);
	   -+green(south_north);
	   !seamless_traffic.
	
@s2[atomic]
+!seamless_traffic
	: green(south_north)
	<- .wait(1000);
	   .broadcast(untell,green(south_north));
	   .broadcast(tell,green(west_east));
	   .print(green_west_east);
	   -+green(west_east);
	   !seamless_traffic.

//+!congestion_control

