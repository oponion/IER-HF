package utils;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.StringTerm;
import jason.asSyntax.Term;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class get_driver_name extends DefaultInternalAction {
	
	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
		String carName = (String)(terms[0]).toString();
		
		String driverName = "driver" + carName.substring(3, carName.length());
		
		return un.unifies(terms[1], new Atom(driverName));
	}
}
