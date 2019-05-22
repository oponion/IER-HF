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
	static HashMap<Integer, Location> trafficLightLocations = new HashMap();

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

    /** Actions **/
	
	boolean moveTowards(String destName, int agId) {
		Location agentLocation = getAgPos(agId);
		Location oldLocation = agentLocation;
		
		logger.info(agentLocation.x + " " + agentLocation.y);
		
		Location dest = null;
		if (destName.equals("traffic_light")) {
			dest = trafficLightLocations.get(agentLocation.x);
		}
		
		if (dest == null) {
			return false;
		}
		
		if (agentLocation.x < dest.x) {
			agentLocation.x++;
		} else if (agentLocation.x > dest.x) {
			agentLocation.x--;
		}
		if (agentLocation.y < dest.y) {
			agentLocation.y++;
		} else if (agentLocation.y > dest.y) {
			agentLocation.y--;
		}
		
		setAgPos(agId, agentLocation);
		
		// repaint
        if (view != null) {
			view.repaint();
            /*view.update(oldLocation.x, oldLocation.y);
            view.update(agentLocation.x, agentLocation.y);*/
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

    static WorldModel world1() throws Exception {
		int height = 40;
		int width = 40;
		int numOfAgents = 1;
        WorldModel model = WorldModel.create(width, height, numOfAgents);
		//Cars
		model.setAgPos(0, 19, 0);
		/*model.setAgPos(1, 1, 10);
		model.setAgPos(2, 2, 10);
		model.setAgPos(3, 3, 10);
		model.setAgPos(4, 4, 10);
		model.setAgPos(5, 5, 10);*/
		AGENT_NUMS = new HashMap<>();
		AGENT_NUMS.put("car", 1);
		//Pedestrians
		/*model.setAgPos(6, 6, 10);
		model.setAgPos(7, 7, 10);
		model.setAgPos(8, 8, 10);*/
		AGENT_NUMS.put("pedestrian", 0);
		//Ambulance
		//model.setAgPos(9, 9, 10);
		AGENT_NUMS.put("ambulance", 0);
		
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
		
		trafficLightLocations.put(23, new Location(23, 19));
		trafficLightLocations.put(20, new Location(20, 23));
		trafficLightLocations.put(16, new Location(16, 20));
		trafficLightLocations.put(19, new Location(19, 16));
		
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
