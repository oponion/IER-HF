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

    public void setEnv(IntersectionController env) {
        this.env = env;
        scenarios.setSelectedIndex(env.getSimId()-1);
    }

    JLabel    jlMouseLoc;
    JComboBox scenarios;
    JSlider   jSpeed;
    JLabel    jGoldsC;
	JButton   addCarNorth;
	JButton   addCarEast;
	JButton   addCarSouth;
	JButton   addCarWest;

    @Override
    public void initComponents(int width) {
        super.initComponents(width);
        scenarios = new JComboBox();
        for (int i=1; i<=3; i++) {
            scenarios.addItem(i);
        }
        JPanel args = new JPanel();
        args.setLayout(new BoxLayout(args, BoxLayout.Y_AXIS));

        JPanel sp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sp.setBorder(BorderFactory.createEtchedBorder());
        sp.add(new JLabel("Scenario:"));
        sp.add(scenarios);

        jSpeed = new JSlider();
        jSpeed.setMinimum(0);
        jSpeed.setMaximum(400);
        jSpeed.setValue(50);
        jSpeed.setPaintTicks(true);
        jSpeed.setPaintLabels(true);
        jSpeed.setMajorTickSpacing(100);
        jSpeed.setMinorTickSpacing(20);
        jSpeed.setInverted(true);
        Hashtable<Integer,Component> labelTable = new Hashtable<Integer,Component>();
        labelTable.put( 0, new JLabel("max") );
        labelTable.put( 200, new JLabel("speed") );
        labelTable.put( 400, new JLabel("min") );
        jSpeed.setLabelTable( labelTable );
        JPanel p = new JPanel(new FlowLayout());
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(jSpeed);

        args.add(sp);
        args.add(p);

        JPanel msg = new JPanel();
        msg.setLayout(new BoxLayout(msg, BoxLayout.Y_AXIS));
        msg.setBorder(BorderFactory.createEtchedBorder());

        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        msg.add(p);
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(new JLabel("(mouse at:"));
        jlMouseLoc = new JLabel("0,0)");
        p.add(jlMouseLoc);
        msg.add(p);
        p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        jGoldsC = new JLabel("0");
        p.add(jGoldsC);
        msg.add(p);

        JPanel s = new JPanel(new BorderLayout());
        s.add(BorderLayout.WEST, args);
        s.add(BorderLayout.CENTER, msg);
        getContentPane().add(BorderLayout.SOUTH, s);

        // Events handling
        jSpeed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (env != null) {
                    env.setSleep((int)jSpeed.getValue());
                }
            }
        });

        scenarios.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ievt) {
                int w = ((Integer)scenarios.getSelectedItem()).intValue();
                if (env != null && env.getSimId() != w) {
                    env.endSimulation();
                    env.initWorld(w);
                }
            }
        });

        getCanvas().addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) { }
            public void mouseMoved(MouseEvent e) {
                int col = e.getX() / cellSizeW;
                int lin = e.getY() / cellSizeH;
                if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
                    jlMouseLoc.setText(col+","+lin+")");
                }
            }
        });
    }

    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
        case WorldModel.PEDESTRIAN_CROSSING:
            drawCrossing(g, x, y);
            break;
        case WorldModel.CRITICAL_CELL:
            drawCritical(g, x, y);
            break;
        case WorldModel.BROADCAST_CELL:
            drawBroadcast(g, x, y);
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

    public void drawCritical(Graphics g, int x, int y) {
        g.setColor(Color.orange);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.pink);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawBroadcast(Graphics g, int x, int y) {
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        int[] vx = new int[4];
        int[] vy = new int[4];
        vx[0] = x * cellSizeW + (cellSizeW / 2);
        vy[0] = y * cellSizeH;
        vx[1] = (x + 1) * cellSizeW;
        vy[1] = y * cellSizeH + (cellSizeH / 2);
        vx[2] = x * cellSizeW + (cellSizeW / 2);
        vy[2] = (y + 1) * cellSizeH;
        vx[3] = x * cellSizeW;
        vy[3] = y * cellSizeH + (cellSizeH / 2);
        g.fillPolygon(vx, vy, 4);
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
