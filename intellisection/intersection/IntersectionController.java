package intersection;

// Environment code for project jasonTeamSimLocal.mas2j

import jason.asSyntax.*;
import jason.environment.grid.Location;
import jason.mas2j.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

import java.io.FileInputStream;

public class IntersectionController extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("jasonTeamSimLocal.mas2j." + IntersectionController.class.getName());

    WorldModel  model;
    WorldView   view;

    int     numberOfSpeedChoices = 3;

    int     sleep    = 0;
    boolean running  = true;
    boolean hasGUI   = true;

    public static final int SIM_TIME = 60;  // in seconds

    public enum Move {
        NORTH, SOUTH, EAST, WEST
    };
	
    @Override
    public void init(String[] args) {
        hasGUI = args[2].equals("yes");
        sleep  = Integer.parseInt(args[1]);
        initWorld();
    }

    public void setSleep(int s) {
        sleep = s;
    }

    @Override
    public void stop() {
        running = false;
        super.stop();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        boolean result = false;
        try {
            if (sleep > 0) {
                Thread.sleep(sleep);
            }

            // get the agent id based on its name
			int agentId = getAgIdBasedOnName(ag);
				
            if (action.getFunctor().equals("move_towards")) {
				String destination = action.getTerm(0).toString();
				
				try {
					
					result = model.moveTowards(destination, agentId);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
            }
			else if (action.getFunctor().equals("move_aside")) {
				try {
					
					result = model.moveAside(agentId);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
            }
			else if (action.getFunctor().equals("move_to")) {
				int toX = (int)((NumberTerm)action.getTerm(0)).solve();
				int toY = (int)((NumberTerm)action.getTerm(1)).solve();
				
				try {
					
					result = model.moveTo(toX, toY, agentId);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
            } else if (action.getFunctor().equals("set_lights")) {
				
				String state = action.getTerm(0).toString();
				
				try {
					
					result = model.setLights(state);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
            } else {
                logger.info("executing: " + action + ", but not implemented!");
            }
			if(agentId == -1) {
				view.update(25, 17);
				view.update(22, 25);
				view.update(14, 22);
				view.update(17, 14);
				return true;
			}
            if (result) {
                updateAgPercept(getAgentNameById(agentId), agentId);
                return true;
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error executing " + action + " for " + ag, e);
        }
        return false;
    }

    private int getAgIdBasedOnName(String agName) {
		
		if (agName.startsWith("car")) {
			return Integer.parseInt(agName.substring(3, agName.length())) - 1;
		}
		if (agName.startsWith("ambulance")) {
			return model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian");
		}
		if (agName.startsWith("pedestrian")) {
			return Integer.parseInt(agName.substring(10, agName.length())) + model.AGENT_NUMS.get("car") - 1;
		}
		return -1;
    }

    public void initWorld() {
		try{
			model = WorldModel.world();
		clearPercepts();
		if (hasGUI) {
			view = new WorldView(model);
			view.setEnv(this);
			model.setView(view);
		}
		updateAgsPercept();
		informAgsEnvironmentChanged();
		} catch(Exception e) {
			logger.warning("Error creating world: " + e);
		}
		
    }

    public void endSimulation() {
        if (view != null) view.setVisible(false);
        WorldModel.destroy();
    }

    private void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            updateAgPercept(getAgentNameById(i), i);
        }
    }
	
	private String getAgentNameById(int id) {
		if (id < model.AGENT_NUMS.get("car")) {
			return "car" + (id + 1);
		}
		else if(id < model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian")) {
			return "pedestrian" + (id - model.AGENT_NUMS.get("car")+ 1);
		}
		else {
			return "ambulance";
		}
	}

    private void updateAgPercept(String agName, int ag) {
        clearPercepts(agName);
        // its location
        Location l = model.getAgPos(ag);
        addPercept(agName, Literal.parseLiteral("pos(" + l.x + "," + l.y + ")"));
		
		String source = model.sourceDestMap.get(ag)[0].toString().toLowerCase();
		String dest = model.sourceDestMap.get(ag)[1].toString().toLowerCase();
		addPercept(agName, Literal.parseLiteral("route(" + source + "," + dest + ")"));
		
		// what's around (only relevant fields)
		switch(dest) {
			case "north":
				updateAgPercept(agName, l.x, l.y - 1);		// ^
				updateAgPercept(agName, l.x, l.y);			// o
				updateAgPercept(agName, l.x + 1, l.y);		// >
				updateAgPercept(agName, l.x, l.y + 1);		// ˇ
			case "east":
				updateAgPercept(agName, l.x, l.y);			// o
				updateAgPercept(agName, l.x + 1, l.y);		// >
				updateAgPercept(agName, l.x, l.y + 1);		// ˇ
				updateAgPercept(agName, l.x - 1, l.y);		// <
			case "south":
				updateAgPercept(agName, l.x, l.y);			// o
				updateAgPercept(agName, l.x, l.y + 1);		// ˇ
				updateAgPercept(agName, l.x - 1, l.y);		// <
				updateAgPercept(agName, l.x, l.y - 1);		// ^
			case "west":
				updateAgPercept(agName, l.x, l.y);			// o
				updateAgPercept(agName, l.x, l.y - 1);		// ^
				updateAgPercept(agName, l.x - 1, l.y);		// <
				updateAgPercept(agName, l.x + 1, l.y);		// >
		}
		
		if(model.getTypeFromId(ag).equals("car")) {
			Location trafficLightLocation = model.getTrafficLightLocation(l);
			addPercept(agName, Literal.parseLiteral("traffic_light_pos(" + trafficLightLocation.x + "," + trafficLightLocation.y + ")"));
		}
		
		if(model.getTypeFromId(ag).equals("ambulance")) {
			if(model.hasObject(WorldModel.AGENT, l.x - 1, l.y)) {
				addPercept(getAgentNameById(model.getAgAtPos(l.x - 1, l.y)), Literal.parseLiteral("ambulance_behind"));
			}
		}
    }


    private void updateAgPercept(String agName, int x, int y) {
        if (model == null || !model.inGrid(x,y)) return;
		if (model.hasObject(WorldModel.BROADCAST_CELL, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",broadcast)"));
		}
		if (model.hasObject(WorldModel.AGENT, x, y)) {
			if(getAgentNameById(model.getAgAtPos(x, y)).equals("ambulance")) {
				boolean ambulance_behind = false;
				switch(model.sourceDestMap.get(getAgIdBasedOnName(agName))[1]){
					case NORTH:
						if(y > model.getAgPos(getAgIdBasedOnName(agName)).y) {
							ambulance_behind = true;
						}
						break;
					case EAST:
						if(x < model.getAgPos(getAgIdBasedOnName(agName)).x) {
							ambulance_behind = true;
						}
						break;
					case SOUTH:
						if(y < model.getAgPos(getAgIdBasedOnName(agName)).y) {
							ambulance_behind = true;
						}
						break;
					case WEST:
						if(x > model.getAgPos(getAgIdBasedOnName(agName)).x) {
							ambulance_behind = true;
						}
						break;
					default:
						break;
				}
				if(ambulance_behind) {
				addPercept(agName, Literal.parseLiteral("ambulance_behind"));
				}
			}
		}
    }
}
