package intersection;

import jason.environment.grid.GridWorldView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class WorldView extends GridWorldView {

    IntersectionController env = null;
	WorldModel model = null;

    public WorldView(WorldModel model) {
        super(model, "Intellisection", 1000);
		this.model = model;
        setVisible(true);
        repaint();
    }

    JComboBox speedSelection;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        speedSelection = new JComboBox();
        speedSelection.addItem("slow");
		speedSelection.addItem("medium");
		speedSelection.addItem("fast");
        JPanel args = new JPanel();
        args.setLayout(new BoxLayout(args, BoxLayout.Y_AXIS));

        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sp.setBorder(BorderFactory.createEtchedBorder());
        sp.add(new JLabel("Speed:"));
        sp.add(speedSelection);

        args.add(sp);

        JPanel s = new JPanel(new BorderLayout());
        s.add(BorderLayout.WEST, args);
        getContentPane().add(BorderLayout.SOUTH, s);

        speedSelection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ievt) {
                String speed = ((String)speedSelection.getSelectedItem());
                if (env != null) {
                    switch(speed) {
						case "fast":
							env.setSleep(20);
							break;
						case "medium":
							env.setSleep(100);
							break;
						case "slow":
							env.setSleep(400);
							break;
					}
						
                }
            }
        });
    }
	
	public void setEnv(IntersectionController env) {
        this.env = env;
    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
        case WorldModel.PEDESTRIAN_CROSSING:
            drawCrossing(g, x, y);
            break;
		case WorldModel.EMERGENCY_LANE:
            drawEmergency(g, x, y);
            break;
		case WorldModel.ROAD:
            drawRoad(g, x, y);
            break;
		case WorldModel.TRAFFIC_LIGHT_H:
            drawHorizontalLight(g, x, y);
            break;
		case WorldModel.TRAFFIC_LIGHT_V:
            drawVerticalLight(g, x, y);
            break;
        }
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
		if(id < model.AGENT_NUMS.get("car")) {
			super.drawAgent(g, x, y, Color.blue, -1);
			Color labelColor = Color.white;
			g.setColor(labelColor);
			drawString(g, x, y, defaultFont, "c" + (id + 1));
		}
		else if(id < model.AGENT_NUMS.get("car") + model.AGENT_NUMS.get("pedestrian")) {
			super.drawAgent(g, x, y, Color.black, -1);
			Color labelColor = Color.white;
			g.setColor(labelColor);
			drawString(g, x, y, defaultFont, "p" + (id - model.AGENT_NUMS.get("car") + 1));
		}
		else {
			super.drawAgent(g, x, y, Color.red, -1);
			Color labelColor = Color.white;
			g.setColor(labelColor);
			drawString(g, x, y, defaultFont, "+");
		}
    }

    public void drawCrossing(Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW/4, cellSizeH/4);
		g.fillRect(x * cellSizeW, y * cellSizeH + 3*cellSizeH/4, cellSizeW/4, cellSizeH/4);
		g.fillRect(x * cellSizeW + 3*cellSizeW/4, y * cellSizeH, cellSizeW/4, cellSizeH/4);
		g.fillRect(x * cellSizeW + 3*cellSizeW/4, y * cellSizeH + 3*cellSizeH/4, cellSizeW/4, cellSizeH/4);
    }
	
	public void drawEmergency(Graphics g, int x, int y) {
        g.setColor(new Color(119, 121, 122, 60));
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    }
	
	public void drawRoad(Graphics g, int x, int y) {
        g.setColor(new Color(217, 219, 221, 60));
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
    }
	
	public void drawHorizontalLight(Graphics g, int x, int y) {
        if(model.isHorizontalLightGreen()) {
			g.setColor(Color.green);
		}
		else {
			g.setColor(Color.red);
		}
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        
    }
	
	public void drawVerticalLight(Graphics g, int x, int y) {
        if(model.isVerticalLightGreen()) {
			g.setColor(Color.green);
		}
		else {
			g.setColor(Color.red);
		}
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    }

    public static void main(String[] args) throws Exception {
        IntersectionController env = new IntersectionController();
        env.init(new String[] {"5","50","yes"});
    }
}
