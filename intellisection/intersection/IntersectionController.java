package intersection;

// Environment code for project jasonTeamSimLocal.mas2j

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
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

    int     simId    = 3; // type of environment
    int     nbWorlds = 3;

    int     sleep    = 0;
    boolean running  = true;
    boolean hasGUI   = true;

    public static final int SIM_TIME = 60;  // in seconds

    Term north = Literal.parseLiteral("do(north)");
    Term south = Literal.parseLiteral("do(south)");
    Term east = Literal.parseLiteral("do(east)");
    Term west = Literal.parseLiteral("do(west)");

    public enum Move {
        NORTH, SOUTH, EAST, WEST
    };
	
	

    @Override
    public void init(String[] args) {
        hasGUI = args[2].equals("yes");
        sleep  = Integer.parseInt(args[1]);
        initWorld(Integer.parseInt(args[0]));
		try {
		  // parse that file
		  jason.mas2j.parser.mas2j parser =
			  new jason.mas2j.parser.mas2j(new FileInputStream(args[3]));
		  MAS2JProject project = parser.mas();

		  List<String> names = new ArrayList<String>();
		  // get the names from the project
		  for (AgentParameters ap : project.getAgents()) {
			 String agName = ap.name;
			 for (int cAg = 0; cAg < ap.getNbInstances(); cAg++) {
				String numberedAg = agName;
				if (ap.getNbInstances() > 1) {
				   numberedAg += (cAg + 1);
				}
				names.add(numberedAg);
			 }
		  }
		  logger.info("Agents' name: "+names);
		} catch(Exception e) {
			logger.warning("Error during init " + e);
			e.printStackTrace();
		}
    }

    public int getSimId() {
        return simId;
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
				
            } else {
                logger.info("executing: " + action + ", but not implemented!");
            }
            if (result) {
                updateAgPercept(agentId);
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
			//return 0;
			return Integer.parseInt(agName.substring(3, agName.length())) - 1;
		}
		if (agName.startsWith("ambulance")) {
			return model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian") - 1;
		}
		if (agName.startsWith("pedestrian")) {
			return Integer.parseInt(agName.substring(10, agName.length())) - 1;
		}
		
		
        return (Integer.parseInt(agName.substring(5))) - 1;
    }

    public void initWorld(int w) {
		simId = w;
        try {
            switch (w) {
            case 1:
                model = WorldModel.world1();
                break;
            case 2:
                model = WorldModel.world2();
                break;
            case 3:
                model = WorldModel.world3();
                break;
            default:
                logger.info("Invalid index!");
                return;
			}
			clearPercepts();
			addPercept(Literal.parseLiteral("gsize(" + simId + "," + model.getWidth() + "," + model.getHeight() + ")"));
			if (hasGUI) {
				view = new WorldView(model);
				view.setEnv(this);
				model.setView(view);
			}
			updateAgsPercept();
			informAgsEnvironmentChanged();
        } catch (Exception e) {
            logger.warning("Error creating world "+e);
			e.printStackTrace();
        }
    }

    public void endSimulation() {
        addPercept(Literal.parseLiteral("end_of_simulation(" + simId + ",0)"));
        informAgsEnvironmentChanged();
        if (view != null) view.setVisible(false);
        WorldModel.destroy();
    }

    private void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            updateAgPercept(i);
        }
    }

    private void updateAgPercept(int ag) {
		if (ag < model.AGENT_NUMS.get("car")) {
			updateAgPercept("car" + (ag + 1), ag);
		}
		else if(ag < model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian")) {
			updateAgPercept("pedestrian" + (ag + 1), ag);
		}
		else {
			updateAgPercept("ambulance", ag);
		}
    }

    private void updateAgPercept(String agName, int ag) {
        clearPercepts(agName);
        // its location
        Location l = model.getAgPos(ag);
        addPercept(agName, Literal.parseLiteral("pos(" + l.x + "," + l.y + ")"));

        // what's around
        updateAgPercept(agName, l.x - 1, l.y - 1);
        updateAgPercept(agName, l.x - 1, l.y);
        updateAgPercept(agName, l.x - 1, l.y + 1);
        updateAgPercept(agName, l.x, l.y - 1);
        updateAgPercept(agName, l.x, l.y);
        updateAgPercept(agName, l.x, l.y + 1);
        updateAgPercept(agName, l.x + 1, l.y - 1);
        updateAgPercept(agName, l.x + 1, l.y);
        updateAgPercept(agName, l.x + 1, l.y + 1);
		
		
		Move source = model.sourceDestMap.get(ag)[0];
		Move dest = model.sourceDestMap.get(ag)[1];
		addPercept(agName, Literal.parseLiteral("route(" + source + "," + dest + ")"));
		
    }


    private void updateAgPercept(String agName, int x, int y) {
        if (model == null || !model.inGrid(x,y)) return;
		if (model.hasObject(WorldModel.CRITICAL_CELL, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",critical)"));
		}
		if (model.hasObject(WorldModel.BROADCAST_CELL, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",broadcast)"));
		}
		if (model.hasObject(WorldModel.AGENT, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",agent)"));
		}
		if (model.hasObject(WorldModel.PEDESTRIAN_CROSSING, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",crossing)"));
		}
		if (model.hasObject(WorldModel.EMERGENCY_LANE, x, y)) {
			addPercept(agName, Literal.parseLiteral("cell(" + x + "," + y + ",emergency)"));
		}
    }

}
