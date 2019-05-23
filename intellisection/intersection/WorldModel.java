package intersection;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.HashMap;

import intersection.IntersectionController.Move;

public class WorldModel extends GridWorldModel {

	public static final int PEDESTRIAN_CROSSING = 8;
    public static final int CRITICAL_CELL  = 16;
    public static final int BROADCAST_CELL = 32;
    public static final int EMERGENCY_LANE = 64;
	public static final int ROAD = 128;
	
	public static HashMap<String, Integer> AGENT_NUMS;

	Location criticalArea;
	Location broadcastArea;
	
	// holds location based on x coord.
	static HashMap<IntersectionController.Move, Location> trafficLightLocations = new HashMap();
	
	static HashMap<Integer, IntersectionController.Move[]> sourceDestMap = new HashMap();

    private Logger            logger   = Logger.getLogger("intellisection.mas2j." + WorldModel.class.getName());

    private String            id = "WorldModel";

    // singleton pattern
    protected static WorldModel model = null;

    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }

    public static WorldModel get() {
        return model;
    }

    public static void destroy() {
        model = null;
    }

    private WorldModel(int w, int h, int nbAgs) {
        super(w, h, nbAgs);
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }

    public Location getCriticalArea() {
        return criticalArea;
    }
	
	public Location getBroadcastArea() {
        return broadcastArea;
    }
	
	public Location getTrafficLightLocation(Location agentLocation) {
		int minDistance = Integer.MAX_VALUE;
		Location closestLight = null;
		
		for(IntersectionController.Move key : trafficLightLocations.keySet()) {
			int distance = trafficLightLocations.get(key).distance(agentLocation);
			if(distance < minDistance) {
				minDistance = distance;
				closestLight = trafficLightLocations.get(key);
			}
		}
		return closestLight;
	}
	
	public Location getDestinationLocation(int agentId) {
		switch(sourceDestMap.get(agentId)[1]) {
			case NORTH:
				return new Location(20, 0);
			case EAST:
				return new Location(39, 20);
			case SOUTH:
				return new Location(19, 39);
			case WEST:
				return new Location(0, 19);
		}
		return null;
	}

    /** Actions **/
	
	boolean moveTowards(String destName, int agId) {
		Location agentLocation = getAgPos(agId);
		Location oldLocation = agentLocation;
		
		Location dest = null;
		if (destName.equals("traffic_light")) {
			dest = getTrafficLightLocation(agentLocation);
		}
		
		if (destName.equals("destination")) {
			dest = getDestinationLocation(agId);
		}
		
		if (destName.equals("hospital")) {
			dest = new Location(0, 19);
		}
		
		if (dest == null) {
			return false;
		}
		try{
			
			if(dest.y == agentLocation.y) {
				if(dest.x - agentLocation.x > 0) {
					move(Move.EAST, agId);
				}
				else {
					move(Move.WEST, agId);
				}
			}
			else {
				if(dest.y - agentLocation.y > 0) {
					move(Move.SOUTH, agId);
				}
				else {
					move(Move.NORTH, agId);
				}
			}
		} catch(Exception e) {
			logger.warning("Error during move: " + e);
			e.printStackTrace();
		}
		
		/*if (agentLocation.x < dest.x) {
			agentLocation.x++;
		} else if (agentLocation.x > dest.x) {
			agentLocation.x--;
		}
		if (agentLocation.y < dest.y) {
			agentLocation.y++;
		} else if (agentLocation.y > dest.y) {
			agentLocation.y--;
		}
		
		setAgPos(agId, agentLocation);*/
		
		// repaint
        if (view != null) {
			//view.repaint();
            view.update(oldLocation.x, oldLocation.y);
            view.update(agentLocation.x, agentLocation.y);
        }
        return true;
	}

    boolean move(Move dir, int ag) throws Exception {
        Location l = getAgPos(ag);
        switch (dir) {
        case NORTH:
            if (isFree(l.x, l.y - 1)) {
                setAgPos(ag, l.x, l.y - 1);
            }
            break;
        case SOUTH:
            if (isFree(l.x, l.y + 1)) {
                setAgPos(ag, l.x, l.y + 1);
            }
            break;
        case EAST:
            if (isFree(l.x + 1, l.y)) {
                setAgPos(ag, l.x + 1, l.y);
            }
            break;
        case WEST:
            if (isFree(l.x - 1, l.y)) {
                setAgPos(ag, l.x - 1, l.y);
            }
            break;
        }
        return true;
    }
	
	static String getTypeFromId(int agentId) {
		if(agentId < model.AGENT_NUMS.get("car")) {
			return "car";
		}
		else if(agentId < model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian")) {
			return "pedestrian";
		}
		else {
			return "ambulance";
		}
	}

    static WorldModel world1() throws Exception {
		int height = 40;
		int width = 40;
		int numOfAgents = 21;
        WorldModel model = WorldModel.create(width, height, numOfAgents);
		//Cars
		
		// North->South
		model.setAgPos(0, 19, 0);
		model.setAgPos(4, 19, 1);
		model.setAgPos(8, 19, 2);
		model.setAgPos(12, 19, 4);
		model.setAgPos(16, 19, 8);
		/*model.setAgPos(20, 19, 9);
		model.setAgPos(24, 19, 11);*/
		
		// East->West
		model.setAgPos(1, 38, 19);
		model.setAgPos(5, 37, 19);
		model.setAgPos(9, 36, 19);
		model.setAgPos(13, 35, 19);
		model.setAgPos(17, 32, 19);
		/*model.setAgPos(21, 29, 19);
		model.setAgPos(25, 28, 19);
		model.setAgPos(29, 26, 19);*/
		
		// South->North
		model.setAgPos(2, 20, 39);
		model.setAgPos(6, 20, 38);
		model.setAgPos(10, 20, 35);
		model.setAgPos(14, 20, 34);
		model.setAgPos(18, 20, 30);
		
		// West->East
		model.setAgPos(3, 0, 20);
		model.setAgPos(7, 1, 20);
		model.setAgPos(11, 4, 20);
		model.setAgPos(15, 6, 20);
		model.setAgPos(19, 7, 20);
		/*model.setAgPos(23, 9, 20);*/
		
		// Ambulance
		model.setAgPos(20, 39, 19);
		
		// Possible routes
		IntersectionController.Move[] path0 = {IntersectionController.Move.NORTH, IntersectionController.Move.SOUTH};
		IntersectionController.Move[] path1 = {IntersectionController.Move.EAST, IntersectionController.Move.WEST};
		IntersectionController.Move[] path2 = {IntersectionController.Move.SOUTH, IntersectionController.Move.NORTH};
		IntersectionController.Move[] path3 = {IntersectionController.Move.WEST, IntersectionController.Move.EAST};
		
		// Car routes
		for(int i = 0; i < 20; ++i) {
			if(i % 4 == 0) {
				sourceDestMap.put(i, path0);
			}
			else if(i % 4 == 1) {
				sourceDestMap.put(i, path1);
			}
			else if(i % 4 == 2) {
				sourceDestMap.put(i, path2);
			}
			else if(i % 4 == 3) {
				sourceDestMap.put(i, path3);
			}
		}
		
		// Ambulance route
		sourceDestMap.put(20, path1);
		
		/*model.setAgPos(1, 1, 10);
		model.setAgPos(2, 2, 10);
		model.setAgPos(3, 3, 10);
		model.setAgPos(4, 4, 10);
		model.setAgPos(5, 5, 10);*/
		AGENT_NUMS = new HashMap<>();
		AGENT_NUMS.put("car", 20);
		//Pedestrians
		/*model.setAgPos(6, 6, 10);
		model.setAgPos(7, 7, 10);
		model.setAgPos(8, 8, 10);*/
		AGENT_NUMS.put("pedestrian", 0);
		//Ambulance
		//model.setAgPos(9, 9, 10);
		AGENT_NUMS.put("ambulance", 1);
		
		for(int i = 0; i < height; ++i) {
			model.add(WorldModel.ROAD, width/2-1, i);
			model.add(WorldModel.ROAD, width/2, i);
			if(i < height/2-1 || i > height/2) {
				model.add(WorldModel.EMERGENCY_LANE, width/2-2, i);
				model.add(WorldModel.EMERGENCY_LANE, width/2+1, i);
			}
		}
		
		for(int i = 0; i < width; ++i) {
			model.add(WorldModel.ROAD, i, height/2-1);
			model.add(WorldModel.ROAD, i, height/2);
			if(i < width/2-1 || i > width/2) {
				model.add(WorldModel.EMERGENCY_LANE, i, height/2-2);
				model.add(WorldModel.EMERGENCY_LANE, i, height/2+1);
			}
		}
		
		for(int i = width/2-3; i <= width/2+2; ++i) {
			model.add(WorldModel.PEDESTRIAN_CROSSING, i, height/2-3);
			model.add(WorldModel.PEDESTRIAN_CROSSING, i, height/2+2);
		}
		
		for(int i = height/2-3; i <= height/2+2; ++i) {
			model.add(WorldModel.PEDESTRIAN_CROSSING, width/2-3, i);
			model.add(WorldModel.PEDESTRIAN_CROSSING, width/2+2, i);
		}
		
		for(int i = height/2-2; i <= height/2+1; ++i) {
			for(int j = width/2-2; j <= width/2+1; ++j) {
				model.add(WorldModel.BROADCAST_CELL, j, i);
			}
		}
		
		for(int i = height/2-1; i <= height/2; ++i) {
			for(int j = width/2-1; j <= width/2; ++j) {
				model.add(WorldModel.CRITICAL_CELL, j, i);
			}
		}
		
		//init trafficlight locations
		
		trafficLightLocations.put(IntersectionController.Move.EAST, new Location(23, 19));
		trafficLightLocations.put(IntersectionController.Move.SOUTH, new Location(20, 23));
		trafficLightLocations.put(IntersectionController.Move.WEST, new Location(16, 20));
		trafficLightLocations.put(IntersectionController.Move.NORTH, new Location(19, 16));
		
        return model;
    }
	
	static WorldModel world2() throws Exception {
        WorldModel model = WorldModel.create(40, 40, 1);
        return model;
    }
	
	static WorldModel world3() throws Exception {
        WorldModel model = WorldModel.create(40, 40, 1);
        return model;
    }

}
